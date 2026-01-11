// Counseling Module JavaScript
// Handles appointment booking, calendar, and counselor selection

// Counselor data (Static for now, could be fetched from API later)
const counselors = [
    {
        id: 1, // Changed to number to match Java Long
        name: 'Dr. Sarah Williams',
        title: 'Licensed Clinical Psychologist',
        specialty: ['Anxiety', 'Depression', 'Stress Management'],
        rating: 4.9,
        avatar: 'https://ui-avatars.com/api/?name=Sarah+Williams&background=3b82f6&color=fff'
    },
    {
        id: 2,
        name: 'Dr. Michael Chen',
        title: 'Mental Health Counselor',
        specialty: ['Relationships', 'Trauma', 'Self-esteem'],
        rating: 4.8,
        avatar: 'https://ui-avatars.com/api/?name=Michael+Chen&background=22c55e&color=fff'
    },
    {
        id: 3,
        name: 'Dr. Emily Rodriguez',
        title: 'Psychiatrist',
        specialty: ['Mood Disorders', 'ADHD', 'Medication Management'],
        rating: 4.9,
        avatar: 'https://ui-avatars.com/api/?name=Emily+Rodriguez&background=a855f7&color=fff'
    }
];

// Available time slots (Matches what backend can parse)
const availableTimeSlots = [
    '9:00 AM', '10:00 AM', '11:00 AM',
    '1:00 PM', '2:00 PM', '3:00 PM', '4:00 PM'
];

// Global state for appointments (populated from API)
let appointments = [];

// State for selection
let selectedDate = null;
let selectedTime = null;
let selectedCounselor = null;

// Initialize on page load
document.addEventListener('DOMContentLoaded', function () {
    renderCounselors();
    renderTimeSlots();

    // FETCH REAL DATA FROM BACKEND
    renderAppointments();

    // Initialize datepicker
    try {
        initializeDatepicker();
    } catch (error) {
        console.error('Datepicker initialization failed:', error);
        const dpContainer = document.getElementById('datepicker-container');
        if (dpContainer) {
            dpContainer.innerHTML =
                '<p class="text-muted text-center py-4">Calendar loading... Please refresh.</p>';
        }
    }

    // Initialize Modal Event Listeners
    initModalListeners();
});

function initModalListeners() {
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

        // Book appointment button
        if (bookAppointmentBtn) {
            bookAppointmentBtn.addEventListener('click', function () {
                renderCounselorSelectionList();
                if (counselorSelectionModal) counselorSelectionModal.classList.add('active');
                if (modalOverlay) modalOverlay.classList.add('active');
                document.body.style.overflow = 'hidden';
            });
        }

        // Close counselor selection
        if (closeCounselorSelectionBtn) {
            closeCounselorSelectionBtn.addEventListener('click', function () {
                closeModal(counselorSelectionModal);
            });
        }

        // Cancel booking (Close modal)
        if (cancelBookingBtn) {
            cancelBookingBtn.addEventListener('click', function () {
                closeModal(bookingModal);
            });
        }

        // Confirm Booking (The key API Action)
        if (confirmBookingBtn) {
            confirmBookingBtn.addEventListener('click', confirmBooking);
        }

        // Close Cancel Confirmation
        if (cancelConfirmCancelBtn) {
            cancelConfirmCancelBtn.addEventListener('click', function () {
                closeModal(cancelConfirmModal);
            });
        }

        // Confirm Cancellation Action
        if (confirmCancelBtn) {
            confirmCancelBtn.addEventListener('click', confirmCancelAppointment);
        }

        // Overlay Click
        if (modalOverlay) {
            modalOverlay.addEventListener('click', function (e) {
                if (e.target === modalOverlay) {
                    closeAllModals();
                }
            });
        }
    }, 100);
}

// Helper to close specific modal
function closeModal(modal) {
    if (modal) modal.classList.remove('active');
    const modalOverlay = document.getElementById('modalOverlay');
    if (modalOverlay) modalOverlay.classList.remove('active');
    document.body.style.overflow = '';
}

function closeAllModals() {
    document.querySelectorAll('.modal-custom.active').forEach(m => m.classList.remove('active'));
    const modalOverlay = document.getElementById('modalOverlay');
    if (modalOverlay) modalOverlay.classList.remove('active');
    document.body.style.overflow = '';
}

// Initialize Bootstrap Datepicker
function initializeDatepicker() {
    if (typeof $ === 'undefined') throw new Error('jQuery is not loaded');

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

function renderTimeSlots() {
    const container = document.getElementById('time-slots-container');
    if (!selectedDate) {
        container.innerHTML = '<p class="text-muted small text-center py-4">Please select a date first</p>';
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

function renderCounselors() {
    const container = document.getElementById('counselors-container');
    container.innerHTML = '';
    counselors.forEach(function (counselor) {
        const col = document.createElement('div');
        col.className = 'col-md-4';
        col.innerHTML = `
            <div class="card card-custom p-4 text-center h-100 hover-shadow">
                <div class="mb-3">
                    <img src="${counselor.avatar}" class="rounded-circle shadow-sm" width="80" height="80" alt="${counselor.name}">
                </div>
                <h6 class="fw-bold mb-1" style="color: #1e3a8a;">${counselor.name}</h6>
                <p class="text-muted small mb-2">${counselor.title}</p>
                <div class="d-flex justify-content-center align-items-center gap-1 mb-3">
                    <i class="fas fa-star text-warning"></i>
                    <span class="small fw-semibold">${counselor.rating}</span>
                </div>
                <div class="d-flex justify-content-center flex-wrap gap-1">
                    ${counselor.specialty.map(spec => `<span class="badge bg-light text-dark border small">${spec}</span>`).join('')}
                </div>
            </div>`;
        container.appendChild(col);
    });
}

function renderCounselorSelectionList() {
    const container = document.getElementById('counselor-selection-list');
    container.innerHTML = '';
    counselors.forEach(function (counselor) {
        const col = document.createElement('div');
        col.className = 'col-md-6';
        const isSelected = selectedCounselor && selectedCounselor.id === counselor.id;

        col.innerHTML = `
            <div class="card p-3 cursor-pointer" 
                 onclick="selectCounselor(${counselor.id})"
                 style="cursor: pointer; border: ${isSelected ? '2px solid #3b82f6' : '1px solid #e2e8f0'}; border-radius: 8px;">
                <div class="d-flex align-items-center gap-3">
                    <img src="${counselor.avatar}" class="rounded-circle" width="50" height="50" alt="${counselor.name}">
                    <div class="flex-grow-1">
                        <h6 class="fw-bold mb-1" style="color: #1e3a8a;">${counselor.name}</h6>
                        <p class="text-muted small mb-0">${counselor.title}</p>
                        <div class="d-flex gap-1 mt-1">
                            <i class="fas fa-star text-warning" style="font-size: 12px;"></i>
                            <span class="small fw-semibold">${counselor.rating}</span>
                        </div>
                    </div>
                </div>
            </div>`;
        container.appendChild(col);
    });
}

// Helper function called by onclick in renderCounselorSelectionList
window.selectCounselor = function (id) {
    selectedCounselor = counselors.find(c => c.id === id);
    renderCounselorSelectionList();

    setTimeout(function () {
        closeModal(document.getElementById('counselorSelectionModal'));
        const bookingModal = document.getElementById('bookingModal');
        if (bookingModal) bookingModal.classList.add('active');
        // Ensure overlay stays or comes back
        document.getElementById('modalOverlay').classList.add('active');
        updateBookingSummary();
    }, 150);
};

function updateBookingSummary() {
    const container = document.getElementById('booking-summary');
    let html = '';

    if (selectedCounselor) {
        html += `
            <div class="p-3 rounded-3 mb-3" style="background-color: #eff6ff; border: 1px solid #dbeafe;">
                <div class="d-flex align-items-center gap-3">
                    <img src="${selectedCounselor.avatar}" class="rounded-circle" width="48" height="48">
                    <div>
                        <h6 class="fw-bold mb-0" style="color: #1e3a8a;">${selectedCounselor.name}</h6>
                        <p class="small text-muted mb-0">${selectedCounselor.title}</p>
                    </div>
                </div>
            </div>`;
    }

    if (selectedDate) {
        const date = new Date(selectedDate);
        html += `
            <div class="p-3 rounded-3 mb-3" style="background-color: #f0fdf4; border: 1px solid #bbf7d0;">
                <div class="d-flex align-items-center gap-2">
                    <i class="fas fa-calendar text-success"></i>
                    <span style="color: #166534;">${date.toLocaleDateString('en-US', { weekday: 'long', month: 'long', day: 'numeric', year: 'numeric' })}</span>
                </div>
            </div>`;
    } else {
        html += `<div class="alert alert-warning mb-3"><i class="fas fa-exclamation-triangle me-2"></i>Please select a date</div>`;
    }

    if (selectedTime) {
        html += `
            <div class="p-3 rounded-3 mb-3" style="background-color: #faf5ff; border: 1px solid #e9d5ff;">
                <div class="d-flex align-items-center gap-2">
                    <i class="fas fa-clock text-purple"></i>
                    <span style="color: #7c3aed;">${selectedTime}</span>
                </div>
            </div>`;
    } else {
        html += `<div class="alert alert-warning mb-3"><i class="fas fa-exclamation-triangle me-2"></i>Please select a time</div>`;
    }

    container.innerHTML = html;
}

// ==========================================
// 1. API INTEGRATION: CONFIRM BOOKING
// ==========================================
function confirmBooking() {
    // Validation
    if (!selectedCounselor) return showToast('Please select a counselor', 'warning');
    if (!selectedDate) return showToast('Please select a date', 'warning');
    if (!selectedTime) return showToast('Please select a time slot', 'warning');

    const confirmBtn = document.getElementById('confirmBookingBtn');
    const originalText = confirmBtn.innerHTML;
    confirmBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Booking...';
    confirmBtn.disabled = true;

    // Prepare Request Body
    const requestData = {
        counselorId: selectedCounselor.id,
        date: selectedDate, // "2025-12-15"
        time: selectedTime, // "10:00 AM"
        notes: document.getElementById('appointmentNotes').value
    };

    // Send POST Request
    fetch('/api/counseling/book', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
            // Add 'X-CSRF-TOKEN': token if using Spring Security CSRF
        },
        body: JSON.stringify(requestData)
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                showToast(data.message || 'Appointment booked!', 'success');

                // Refresh list
                renderAppointments();

                // Reset and Close
                selectedCounselor = null;
                selectedTime = null;
                selectedDate = null;
                closeAllModals();
            } else {
                showToast(data.message || 'Booking failed', 'danger');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showToast('Server connection error', 'danger');
        })
        .finally(() => {
            confirmBtn.innerHTML = originalText;
            confirmBtn.disabled = false;
        });
}

// ==========================================
// 2. API INTEGRATION: FETCH APPOINTMENTS
// ==========================================
function renderAppointments() {
    const container = document.getElementById('appointments-container');
    container.innerHTML = '<div class="text-center py-4"><i class="fas fa-spinner fa-spin text-primary"></i> Loading...</div>';

    fetch('/api/counseling/my-appointments')
        .then(response => {
            if (!response.ok) throw new Error("Failed to fetch");
            return response.json();
        })
        .then(data => {
            // Update global variable so cancellation works
            window.appointments = data;

            if (!data || data.length === 0) {
                container.innerHTML = '<p class="text-center text-muted py-4">No appointments scheduled</p>';
                return;
            }

            container.innerHTML = '';
            data.forEach(function (appointment) {
                const appointmentDiv = document.createElement('div');
                appointmentDiv.className = 'p-3 rounded-3 border mb-3';
                appointmentDiv.style.backgroundColor = '#f8fafc';

                let statusClass, statusIcon;
                // Mapping backend status to UI styles
                if (appointment.status === 'upcoming') {
                    statusClass = 'bg-primary text-white';
                    statusIcon = 'fa-clock';
                } else if (appointment.status === 'completed') {
                    statusClass = 'bg-success text-white';
                    statusIcon = 'fa-check-circle';
                } else if (appointment.status === 'cancelled') {
                    statusClass = 'bg-danger text-white';
                    statusIcon = 'fa-times-circle';
                } else {
                    statusClass = 'bg-secondary text-white';
                    statusIcon = 'fa-question';
                }

                // Initial formatting for avatar initials
                const initials = appointment.counselorName
                    ? appointment.counselorName.split(' ').map(n => n[0]).join('')
                    : 'DR';

                appointmentDiv.innerHTML = `
                    <div class="d-flex flex-column flex-md-row justify-content-between align-items-start align-items-md-center gap-3">
                        <div class="d-flex gap-3 align-items-center flex-grow-1">
                            <div class="rounded-circle d-flex align-items-center justify-content-center flex-shrink-0" 
                                 style="width: 50px; height: 50px; background: linear-gradient(135deg, #3b82f6, #22c55e);">
                                <span class="text-white fw-bold">${initials}</span>
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
                                    <span><i class="fas fa-video me-1"></i>${appointment.type || 'Online'}</span>
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
        })
        .catch(err => {
            console.error(err);
            container.innerHTML = '<p class="text-center text-danger py-4">Error loading appointments</p>';
        });
}

function joinAppointment(id) {
    showToast('Joining video call room...', 'info');
    // Logic to open video call window goes here
}

function cancelAppointment(appointmentId) {
    window.appointmentToCancel = appointmentId;
    const modal = document.getElementById('cancelConfirmModal');
    if (modal) {
        modal.classList.add('active');
        document.getElementById('modalOverlay').classList.add('active');
        document.body.style.overflow = 'hidden';
    }
}

// NOTE: Since you didn't provide a Cancel Endpoint in the backend, 
// this currently only simulates cancellation on the frontend.
function confirmCancelAppointment() {
    const appointmentId = window.appointmentToCancel;
    if (!appointmentId) return;

    // TODO: When backend supports it, use: fetch(`/api/counseling/cancel/${appointmentId}`, { method: 'POST' })

    // Optimistic UI update for now
    showToast('Cancellation request sent', 'warning');

    // Simulate refresh
    renderAppointments();

    closeAllModals();
    window.appointmentToCancel = null;
}

// Universal Toast Handler
function showToast(message, type) {
    // Check if global ToastNotifications object exists (from other modules)
    if (typeof ToastNotifications !== 'undefined') {
        if (type === 'success') ToastNotifications.showSuccessToast(message);
        else if (type === 'error' || type === 'danger') ToastNotifications.showErrorAlert(message, 'danger');
        else ToastNotifications.showToast(message, type || 'info');
        return;
    }

    // Fallback implementation
    const container = document.getElementById('toast-container') || createToastContainer();
    const toast = document.createElement('div');

    let alertClass = 'alert-info', icon = 'fa-info-circle';
    if (type === 'success') { alertClass = 'alert-success'; icon = 'fa-check-circle'; }
    if (type === 'danger' || type === 'error') { alertClass = 'alert-danger'; icon = 'fa-exclamation-circle'; }
    if (type === 'warning') { alertClass = 'alert-warning'; icon = 'fa-exclamation-triangle'; }

    toast.className = `alert ${alertClass} position-fixed top-0 end-0 m-3 shadow`;
    toast.style.zIndex = '10003';
    toast.style.minWidth = '300px';
    toast.innerHTML = `
        <div class="d-flex align-items-center justify-content-between">
            <div><i class="fas ${icon} me-2"></i>${message}</div>
            <button type="button" class="btn-close ms-2" onclick="this.parentNode.parentNode.remove()"></button>
        </div>`;

    container.appendChild(toast);
    setTimeout(() => toast.remove(), 3000);
}

function createToastContainer() {
    const div = document.createElement('div');
    div.id = 'toast-container';
    document.body.appendChild(div);
    return div;
}