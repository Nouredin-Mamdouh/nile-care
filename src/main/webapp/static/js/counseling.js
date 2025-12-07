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
    initializeDatepicker();
    renderCounselors();
    renderAppointments();
});

// Initialize Bootstrap Datepicker
function initializeDatepicker() {
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
            <div class="d-flex justify-content-center flex-wrap gap-1 mb-3">
                ${counselor.specialty.map(spec =>
            `<span class="badge bg-light text-dark border small">${spec}</span>`
        ).join('')}
            </div>
            <button class="btn w-100 text-white" 
                    style="background: linear-gradient(to right, #3b82f6, #22c55e);"
                    onclick="selectCounselor('${counselor.id}')">
                Book Session
            </button>
        `;

        col.appendChild(card);
        container.appendChild(col);
    });
}

// Select counselor and open modal
function selectCounselor(counselorId) {
    selectedCounselor = counselors.find(c => c.id === counselorId);
    updateBookingSummary();

    const modal = new bootstrap.Modal(document.getElementById('bookingModal'));
    modal.show();
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
    if (!selectedCounselor || !selectedDate || !selectedTime) {
        alert('Please select a counselor, date, and time slot');
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

    // Close modal
    const modal = bootstrap.Modal.getInstance(document.getElementById('bookingModal'));
    modal.hide();

    // Reset selections
    selectedCounselor = null;
    selectedTime = null;

    // Show success message
    showToast('Appointment booked successfully!', 'success');
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
    showToast('Joining video call...', 'info');
    // In a real application, this would open the video call interface
}

// Cancel appointment
function cancelAppointment(appointmentId) {
    if (confirm('Are you sure you want to cancel this appointment?')) {
        const appointment = appointments.find(a => a.id === appointmentId);
        if (appointment) {
            appointment.status = 'cancelled';
            renderAppointments();
            showToast('Appointment cancelled', 'warning');
        }
    }
}

// Show toast notification
function showToast(message, type) {
    // Create toast element
    const toast = document.createElement('div');
    toast.className = `alert alert-${type} position-fixed top-0 end-0 m-3`;
    toast.style.zIndex = '9999';
    toast.style.minWidth = '300px';
    toast.innerHTML = `
        <div class="d-flex align-items-center justify-content-between">
            <span>${message}</span>
            <button type="button" class="btn-close btn-close-white ms-3" onclick="this.parentElement.parentElement.remove()"></button>
        </div>
    `;

    document.body.appendChild(toast);

    // Auto remove after 3 seconds
    setTimeout(function () {
        toast.remove();
    }, 3000);
}
