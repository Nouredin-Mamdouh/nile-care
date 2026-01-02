// Counseling Module JavaScript
// Handles appointment booking, calendar, and counselor selection

// Counselor data
const counselors = [
    {
        id: '1',
        name: 'Dr. Sarah Williams',
        title: 'Licensed Clinical Psychologist',
        specialty: ['Anxiety', 'Depression', 'Stress Management'],
        rating: 4.9,
        avatar: 'https://ui-avatars.com/api/?name=Sarah+Williams&background=3b82f6&color=fff'
    },
    {
        id: '2',
        name: 'Dr. Michael Chen',
        title: 'Mental Health Counselor',
        specialty: ['Relationships', 'Trauma', 'Self-esteem'],
        rating: 4.8,
        avatar: 'https://ui-avatars.com/api/?name=Michael+Chen&background=22c55e&color=fff'
    },
    {
        id: '3',
        name: 'Dr. Emily Rodriguez',
        title: 'Psychiatrist',
        specialty: ['Mood Disorders', 'ADHD', 'Medication Management'],
        rating: 4.9,
        avatar: 'https://ui-avatars.com/api/?name=Emily+Rodriguez&background=a855f7&color=fff'
    }
];

// Available time slots
const availableTimeSlots = [
    '9:00 AM', '10:00 AM', '11:00 AM',
    '1:00 PM', '2:00 PM', '3:00 PM', '4:00 PM'
];

// Appointments data
let appointments = [
    {
        id: '1',
        counselorId: '1',
        counselorName: 'Dr. Sarah Williams',
        date: '2025-12-15',
        time: '10:00 AM',
        type: 'video',
        status: 'upcoming'
    }
];

// State
let selectedDate = null;
let selectedTime = null;
let selectedCounselor = null;

// Initialize on page load
document.addEventListener('DOMContentLoaded', function () {
    // Render counselors and appointments first (these don't depend on datepicker)
    renderCounselors();
    renderAppointments();
    renderTimeSlots(); // Render initial "please select a date" message

    // Initialize datepicker (may fail if jQuery not loaded)
    try {
        initializeDatepicker();
    } catch (error) {
        console.error('Datepicker initialization failed:', error);
        // Show a fallback message in the calendar container
        document.getElementById('datepicker-container').innerHTML =
            '<p class="text-muted text-center py-4">Calendar loading... Please refresh if it doesn\'t appear.</p>';
    }

    // Set up modal controls (with timeout for DOM readiness)
    setTimeout(function () {
        const bookingModal = document.getElementById('bookingModal');
        const counselorSelectionModal = document.getElementById('counselorSelectionModal');
        const cancelConfirmModal = document.getElementById('cancelConfirmModal');
        const modalOverlay = document.getElementById('modalOverlay');
        
        const cancelBookingBtn = document.getElementById('cancelBookingBtn');
        const confirmBookingBtn = document.getElementById('confirmBookingBtn');
        const bookAppointmentBtn = document.getElementById('bookAppointmentBtn');
        const closeCounselorSelectionBtn = document.getElementById('closeCounselorSelectionBtn');
        const cancelConfirmCancelBtn = document.getElementById('cancelConfirmCancelBtn');
        const confirmCancelBtn = document.getElementById('confirmCancelBtn');

        // Book appointment button - opens counselor selection modal
        if (bookAppointmentBtn) {
            bookAppointmentBtn.addEventListener('click', function () {
                renderCounselorSelectionList();
                if (counselorSelectionModal) {
                    counselorSelectionModal.classList.add('active');
                }
                if (modalOverlay) {
                    modalOverlay.classList.add('active');
                }
                document.body.style.overflow = 'hidden';
            });
        }

        // Close counselor selection modal
        if (closeCounselorSelectionBtn) {
            closeCounselorSelectionBtn.addEventListener('click', function () {
                if (counselorSelectionModal) counselorSelectionModal.classList.remove('active');
                if (modalOverlay) modalOverlay.classList.remove('active');
                document.body.style.overflow = '';
            });
        }

        // Cancel booking button - closes booking modal
        if (cancelBookingBtn) {
            cancelBookingBtn.addEventListener('click', function () {
                if (bookingModal) bookingModal.classList.remove('active');
                if (modalOverlay) modalOverlay.classList.remove('active');
                document.body.style.overflow = '';
            });
        }

        // Confirm button - processes booking
        if (confirmBookingBtn) {
            confirmBookingBtn.addEventListener('click', confirmBooking);
        }

        // Cancel appointment modal - cancel button
        if (cancelConfirmCancelBtn) {
            cancelConfirmCancelBtn.addEventListener('click', function () {
                if (cancelConfirmModal) cancelConfirmModal.classList.remove('active');
                if (modalOverlay) modalOverlay.classList.remove('active');
                document.body.style.overflow = '';
            });
        }

        // Cancel appointment modal - confirm cancel button
        if (confirmCancelBtn) {
            confirmCancelBtn.addEventListener('click', function () {
                confirmCancelAppointment();
            });
        }

        // Modal overlay click to close
        if (modalOverlay) {
            modalOverlay.addEventListener('click', function (e) {
                if (e.target === modalOverlay) {
                    // Close all modals
                    if (bookingModal) bookingModal.classList.remove('active');
                    if (counselorSelectionModal) counselorSelectionModal.classList.remove('active');
                    if (cancelConfirmModal) cancelConfirmModal.classList.remove('active');
                    modalOverlay.classList.remove('active');
                    document.body.style.overflow = '';
                }
            });
        }
    }, 100);
});

// Initialize Bootstrap Datepicker
function initializeDatepicker() {
    if (typeof $ === 'undefined') {
        throw new Error('jQuery is not loaded');
    }

    $('#datepicker-container').datepicker({
        inline: true,
        startDate: new Date(),
        todayHighlight: true,
        format: 'yyyy-mm-dd'
    }).on('changeDate', function (e) {
        selectedDate = e.format();
        updateSelectedDateDisplay();
        renderTimeSlots();
    });
}

// Update selected date display
function updateSelectedDateDisplay() {
    const displayElement = document.getElementById('selected-date-display');
    if (selectedDate) {
        const date = new Date(selectedDate);
        const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
        displayElement.textContent = date.toLocaleDateString('en-US', options);
        displayElement.className = 'mb-3 fw-semibold';
        displayElement.style.color = '#2563eb';
    } else {
        displayElement.textContent = 'Please select a date';
        displayElement.className = 'mb-3 text-muted small';
    }
}

// Render time slots
function renderTimeSlots() {
    const container = document.getElementById('time-slots-container');

    if (!selectedDate) {
        container.innerHTML = '<p class="text-muted small text-center py-4">Please select a date to view available time slots</p>';
        return;
    }

    container.innerHTML = '';
    availableTimeSlots.forEach(function (slot) {
        const button = document.createElement('button');
        button.className = 'btn btn-outline-primary w-100 text-start mb-2';
        button.innerHTML = `<i class="far fa-clock me-2"></i>${slot}`;

        if (selectedTime === slot) {
            button.classList.remove('btn-outline-primary');
            button.classList.add('btn-primary');
        }

        button.onclick = function () {
            selectedTime = slot;
            renderTimeSlots();
        };

        container.appendChild(button);
    });
}

// Render counselors
function renderCounselors() {
    const container = document.getElementById('counselors-container');
    container.innerHTML = '';

    counselors.forEach(function (counselor) {
        const col = document.createElement('div');
        col.className = 'col-md-4';

        const card = document.createElement('div');
        card.className = 'card card-custom p-4 text-center h-100 hover-shadow';

        card.innerHTML = `
            <div class="mb-3">
                <img src="${counselor.avatar}" 
                     class="rounded-circle shadow-sm" 
                     width="80" 
                     height="80"
                     alt="${counselor.name}">
            </div>
            <h6 class="fw-bold mb-1" style="color: #1e3a8a;">${counselor.name}</h6>
            <p class="text-muted small mb-2">${counselor.title}</p>
            <div class="d-flex justify-content-center align-items-center gap-1 mb-3">
                <i class="fas fa-star text-warning"></i>
                <span class="small fw-semibold">${counselor.rating}</span>
            </div>
            <div class="d-flex justify-content-center flex-wrap gap-1">
                ${counselor.specialty.map(spec =>
            `<span class="badge bg-light text-dark border small">${spec}</span>`
        ).join('')}
            </div>
        `;

        col.appendChild(card);
        container.appendChild(col);
    });
}

// Render counselor selection list in modal
function renderCounselorSelectionList() {
    const container = document.getElementById('counselor-selection-list');
    container.innerHTML = '';

    counselors.forEach(function (counselor) {
        const col = document.createElement('div');
        col.className = 'col-md-6';

        const card = document.createElement('div');
        card.className = 'card p-3 cursor-pointer';
        card.style.cursor = 'pointer';
        card.style.border = selectedCounselor && selectedCounselor.id === counselor.id ? '2px solid #3b82f6' : '1px solid #e2e8f0';
        card.style.borderRadius = '8px';
        card.style.transition = 'all 0.2s ease';

        card.innerHTML = `
            <div class="d-flex align-items-center gap-3">
                <img src="${counselor.avatar}" 
                     class="rounded-circle" 
                     width="50" 
                     height="50"
                     alt="${counselor.name}">
                <div class="flex-grow-1">
                    <h6 class="fw-bold mb-1" style="color: #1e3a8a;">${counselor.name}</h6>
                    <p class="text-muted small mb-0">${counselor.title}</p>
                    <div class="d-flex gap-1 mt-1">
                        <i class="fas fa-star text-warning" style="font-size: 12px;"></i>
                        <span class="small fw-semibold">${counselor.rating}</span>
                    </div>
                </div>
            </div>
        `;

        card.addEventListener('click', function () {
            selectedCounselor = counselor;
            renderCounselorSelectionList(); // Re-render to show selection
            setTimeout(function () {
                // Open booking modal
                const counselorSelectionModal = document.getElementById('counselorSelectionModal');
                const bookingModal = document.getElementById('bookingModal');
                if (counselorSelectionModal) counselorSelectionModal.classList.remove('active');
                if (bookingModal) bookingModal.classList.add('active');
                updateBookingSummary();
            }, 100);
        });

        card.addEventListener('mouseover', function () {
            card.style.boxShadow = '0 2px 8px rgba(0, 0, 0, 0.1)';
            card.style.transform = 'translateY(-2px)';
        });

        card.addEventListener('mouseout', function () {
            card.style.boxShadow = '';
            card.style.transform = '';
        });

        col.appendChild(card);
        container.appendChild(col);
    });
}

// Update booking summary in modal
function updateBookingSummary() {
    const container = document.getElementById('booking-summary');

    let html = '';

    // Selected counselor
    if (selectedCounselor) {
        html += `
            <div class="p-3 rounded-3 mb-3" style="background-color: #eff6ff; border: 1px solid #dbeafe;">
                <div class="d-flex align-items-center gap-3">
                    <img src="${selectedCounselor.avatar}" 
                         class="rounded-circle" 
                         width="48" 
                         height="48"
                         alt="${selectedCounselor.name}">
                    <div>
                        <h6 class="fw-bold mb-0" style="color: #1e3a8a;">${selectedCounselor.name}</h6>
                        <p class="small text-muted mb-0">${selectedCounselor.title}</p>
                    </div>
                </div>
            </div>
        `;
    }

    // Selected date
    if (selectedDate) {
        const date = new Date(selectedDate);
        const options = { weekday: 'long', month: 'long', day: 'numeric', year: 'numeric' };
        html += `
            <div class="p-3 rounded-3 mb-3" style="background-color: #f0fdf4; border: 1px solid #bbf7d0;">
                <div class="d-flex align-items-center gap-2">
                    <i class="fas fa-calendar text-success"></i>
                    <span style="color: #166534;">${date.toLocaleDateString('en-US', options)}</span>
                </div>
            </div>
        `;
    } else {
        html += `
            <div class="alert alert-warning mb-3">
                <i class="fas fa-exclamation-triangle me-2"></i>
                Please select a date from the calendar
            </div>
        `;
    }

    // Selected time
    if (selectedTime) {
        html += `
            <div class="p-3 rounded-3 mb-3" style="background-color: #faf5ff; border: 1px solid #e9d5ff;">
                <div class="d-flex align-items-center gap-2">
                    <i class="fas fa-clock text-purple"></i>
                    <span style="color: #7c3aed;">${selectedTime}</span>
                </div>
            </div>
        `;
    } else {
        html += `
            <div class="alert alert-warning mb-3">
                <i class="fas fa-exclamation-triangle me-2"></i>
                Please select a time slot
            </div>
        `;
    }

    container.innerHTML = html;
}

// Confirm booking
function confirmBooking() {
    // Validate all required fields
    if (!selectedCounselor) {
        ToastNotifications.showToast('Please select a counselor', 'warning');
        return;
    }
    if (!selectedDate) {
        ToastNotifications.showToast('Please select a date', 'warning');
        return;
    }
    if (!selectedTime) {
        ToastNotifications.showToast('Please select a time slot', 'warning');
        return;
    }

    const newAppointment = {
        id: Date.now().toString(),
        counselorId: selectedCounselor.id,
        counselorName: selectedCounselor.name,
        date: selectedDate,
        time: selectedTime,
        type: 'video',
        status: 'upcoming'
    };

    appointments.unshift(newAppointment);
    renderAppointments();

    // Close modal using custom system
    const bookingModal = document.getElementById('bookingModal');
    const modalOverlay = document.getElementById('modalOverlay');
    if (bookingModal) {
        bookingModal.classList.remove('active');
    }
    if (modalOverlay) {
        modalOverlay.classList.remove('active');
    }
    document.body.style.overflow = '';

    // Reset selections
    selectedCounselor = null;
    selectedTime = null;
    selectedDate = null;

    // Show success message with toast
    ToastNotifications.showSuccessToast('Appointment booked successfully!');
}

// Render appointments
function renderAppointments() {
    const container = document.getElementById('appointments-container');

    if (appointments.length === 0) {
        container.innerHTML = '<p class="text-center text-muted py-4">No appointments scheduled</p>';
        return;
    }

    container.innerHTML = '';
    appointments.forEach(function (appointment) {
        const appointmentDiv = document.createElement('div');
        appointmentDiv.className = 'p-3 rounded-3 border mb-3';
        appointmentDiv.style.backgroundColor = '#f8fafc';

        let statusClass, statusIcon;
        if (appointment.status === 'upcoming') {
            statusClass = 'bg-primary text-white';
            statusIcon = 'fa-clock';
        } else if (appointment.status === 'completed') {
            statusClass = 'bg-success text-white';
            statusIcon = 'fa-check-circle';
        } else {
            statusClass = 'bg-danger text-white';
            statusIcon = 'fa-times-circle';
        }

        appointmentDiv.innerHTML = `
            <div class="d-flex flex-column flex-md-row justify-content-between align-items-start align-items-md-center gap-3">
                <div class="d-flex gap-3 align-items-center flex-grow-1">
                    <div class="rounded-circle d-flex align-items-center justify-content-center flex-shrink-0" 
                         style="width: 50px; height: 50px; background: linear-gradient(135deg, #3b82f6, #22c55e);">
                        <span class="text-white fw-bold">${appointment.counselorName.split(' ').map(n => n[0]).join('')}</span>
                    </div>
                    <div class="flex-grow-1">
                        <div class="d-flex align-items-center gap-2 mb-1">
                            <h6 class="fw-bold mb-0" style="color: #1e3a8a;">${appointment.counselorName}</h6>
                            <span class="badge ${statusClass}">
                                <i class="fas ${statusIcon} me-1"></i>${appointment.status.charAt(0).toUpperCase() + appointment.status.slice(1)}
                            </span>
                        </div>
                        <div class="small text-muted d-flex flex-wrap gap-3">
                            <span><i class="far fa-calendar me-1"></i>${appointment.date}</span>
                            <span><i class="far fa-clock me-1"></i>${appointment.time}</span>
                            <span><i class="fas fa-video me-1"></i>Online</span>
                        </div>
                    </div>
                </div>
                ${appointment.status === 'upcoming' ? `
                    <div class="d-flex gap-2">
                        <button class="btn btn-sm btn-outline-success" onclick="joinAppointment('${appointment.id}')">
                            <i class="fas fa-video me-1"></i>Join
                        </button>
                        <button class="btn btn-sm btn-outline-danger" onclick="cancelAppointment('${appointment.id}')">
                            <i class="fas fa-times"></i>
                        </button>
                    </div>
                ` : ''}
            </div>
        `;

        container.appendChild(appointmentDiv);
    });
}

// Join appointment (placeholder)
function joinAppointment(appointmentId) {
    ToastNotifications.showToast('Joining video call...', 'info');
    // In a real application, this would open the video call interface
}

// Cancel appointment
function cancelAppointment(appointmentId) {
    // Store the appointment ID for confirmation
    window.appointmentToCancel = appointmentId;
    
    // Open cancel confirmation modal
    const cancelConfirmModal = document.getElementById('cancelConfirmModal');
    const modalOverlay = document.getElementById('modalOverlay');
    if (cancelConfirmModal) {
        cancelConfirmModal.classList.add('active');
    }
    if (modalOverlay) {
        modalOverlay.classList.add('active');
    }
    document.body.style.overflow = 'hidden';
}

// Confirm cancel appointment
function confirmCancelAppointment() {
    const appointmentId = window.appointmentToCancel;
    if (appointmentId) {
        const appointment = appointments.find(a => a.id === appointmentId);
        if (appointment) {
            appointment.status = 'cancelled';
            renderAppointments();
            ToastNotifications.showToast('Appointment cancelled', 'warning');
        }
    }
    
    // Close modal
    const cancelConfirmModal = document.getElementById('cancelConfirmModal');
    const modalOverlay = document.getElementById('modalOverlay');
    if (cancelConfirmModal) {
        cancelConfirmModal.classList.remove('active');
    }
    if (modalOverlay) {
        modalOverlay.classList.remove('active');
    }
    document.body.style.overflow = '';
    window.appointmentToCancel = null;
}

// Show toast notification
function showToast(message, type) {
    // Use global ToastNotifications if available
    if (typeof ToastNotifications !== 'undefined') {
        if (type === 'success') {
            ToastNotifications.showSuccessToast(message);
        } else if (type === 'error' || type === 'danger') {
            ToastNotifications.showErrorAlert(message, 'danger');
        } else if (type === 'warning') {
            ToastNotifications.showToast(message, 'warning');
        } else {
            ToastNotifications.showToast(message, type || 'info');
        }
        return;
    }

    // Fallback to custom toast if ToastNotifications not available
    const toastContainer = document.getElementById('toast-container') || createToastContainer();
    const toast = document.createElement('div');
    
    let alertClass = 'alert-success';
    let icon = 'fa-check-circle';
    
    if (type === 'error' || type === 'danger') {
        alertClass = 'alert-danger';
        icon = 'fa-exclamation-circle';
    } else if (type === 'warning') {
        alertClass = 'alert-warning';
        icon = 'fa-exclamation-triangle';
    } else if (type === 'info') {
        alertClass = 'alert-info';
        icon = 'fa-info-circle';
    }
    
    toast.className = `alert ${alertClass} position-fixed top-0 end-0 m-3`;
    toast.style.zIndex = '10003'; // Above modals (10002) and overlay (10001)
    toast.style.minWidth = '300px';
    toast.style.animation = 'slideIn 0.3s ease-out';
    toast.innerHTML = `
        <div class="d-flex align-items-center justify-content-between">
            <div class="d-flex align-items-center gap-2">
                <i class="fas ${icon}"></i>
                <span>${message}</span>
            </div>
            <button type="button" class="btn-close ms-3" onclick="this.parentElement.parentElement.remove()"></button>
        </div>
    `;

    toastContainer.appendChild(toast);

    // Auto remove after 3 seconds
    setTimeout(function () {
        toast.remove();
    }, 3000);
}

// Helper to create toast container if it doesn't exist
function createToastContainer() {
    let container = document.getElementById('toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        document.body.appendChild(container);
    }
    return container;
}
