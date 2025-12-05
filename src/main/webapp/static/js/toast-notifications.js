/**
 * Toast Notification System
 * Reusable toast notification utility for success messages across the application
 * Features collapsing/stacking like Sonner, with smart auto-dismiss for success toasts
 * 
 * Usage:
 *   ToastNotifications.showSuccessToast('Your message here');
 *   ToastNotifications.showErrorAlert('Error message', 'danger');
 */

const ToastNotifications = (() => {
    const TOAST_DURATION = 5000; // 5 seconds for success toasts
    const TOAST_CONTAINER_ID = 'appToastContainer';
    const MAX_TOASTS = 3;
    let toastContainer = null;
    const toasts = [];

    console.log('[Toast] System initialized, available methods: showSuccessToast, showToast, showErrorAlert');

    /**
     * Get or create the toast container
     */
    function getToastContainer() {
        if (!toastContainer) {
            console.log('[Toast] Creating toast container...');
            toastContainer = document.createElement('div');
            toastContainer.id = TOAST_CONTAINER_ID;
            document.body.appendChild(toastContainer);
            console.log('[Toast] Container created and appended to body');
        }
        return toastContainer;
    }

    /**
     * Update toast positions for collapsing/stacking effect
     */
    function updateToastPositions() {
        toasts.forEach((toastObj, idx) => {
            const element = toastObj.element;
            element.setAttribute('data-index', String(idx));
        });
    }

    /**
     * Show a success toast notification (auto-dismisses)
     * @param {string} message - The message to display
     */
    function showSuccessToast(message) {
        console.log('[Toast] showSuccessToast called:', message);
        const container = getToastContainer();

        // Create toast element
        const toast = document.createElement('div');
        toast.className = 'app-toast success';
        toast.setAttribute('role', 'status');
        toast.setAttribute('aria-live', 'polite');
        
        const toastMessage = document.createElement('span');
        toastMessage.textContent = message;
        
        toast.appendChild(toastMessage);
        container.appendChild(toast);
        console.log('[Toast] Toast element appended, total toasts:', toasts.length + 1);

        // Add to tracking array with metadata
        const toastObj = { 
            element: toast, 
            timeout: null,
            startTime: Date.now()
        };
        toasts.unshift(toastObj); // Add to front for stacking order
        updateToastPositions();

        // Setup swipe-to-dismiss gesture
        setupSwipeGesture(toast, toastObj);

        // If we exceed max, remove the oldest one gracefully
        if (toasts.length > MAX_TOASTS) {
            const oldest = toasts.pop();
            if (oldest.timeout) clearTimeout(oldest.timeout);
            removeToastGracefully(oldest);
        }

        // Set auto-hide timer (7 seconds)
        toastObj.timeout = setTimeout(() => {
            console.log('[Toast] Auto-removing success toast');
            removeToastGracefully(toastObj);
        }, TOAST_DURATION);

        return toastObj;
    }

    /**
     * Show a generic toast notification
     * @param {string} message - The message to display
     * @param {string} type - The toast type ('error', 'warning', 'info')
     */
    function showToast(message, type = 'info') {
        console.log('[Toast] showToast called:', message, type);
        const container = getToastContainer();

        // Create toast element
        const toast = document.createElement('div');
        toast.className = `app-toast ${type}`;
        toast.setAttribute('role', 'status');
        toast.setAttribute('aria-live', 'polite');
        
        const toastMessage = document.createElement('span');
        toastMessage.textContent = message;
        
        toast.appendChild(toastMessage);
        container.appendChild(toast);

        // Add to tracking array with metadata
        const toastObj = { 
            element: toast, 
            timeout: null,
            startTime: Date.now()
        };
        toasts.unshift(toastObj); // Add to front for stacking order
        updateToastPositions();

        // Setup swipe-to-dismiss gesture
        setupSwipeGesture(toast, toastObj);

        // If we exceed max, remove the oldest one gracefully
        if (toasts.length > MAX_TOASTS) {
            const oldest = toasts.pop();
            if (oldest.timeout) clearTimeout(oldest.timeout);
            removeToastGracefully(oldest);
        }

        // Set auto-hide timer
        toastObj.timeout = setTimeout(() => {
            console.log('[Toast] Auto-removing toast');
            removeToastGracefully(toastObj);
        }, TOAST_DURATION);

        return toastObj;
    }

    /**
     * Show a persistent error alert (requires manual dismissal)
     * @param {string} message - The message to display
     * @param {string} type - Bootstrap alert type ('danger', 'warning', 'info')
     * @param {HTMLElement} insertBefore - Optional element to insert alert before
     */
    function showErrorAlert(message, type = 'danger', insertBefore = null) {
        console.log('[Toast] showErrorAlert called:', message, type);
        // Create alert element
        const alertDiv = document.createElement('div');
        alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
        alertDiv.role = 'alert';
        alertDiv.innerHTML = `
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        `;

        // Insert at specified location or top of page
        if (insertBefore) {
            insertBefore.parentNode.insertBefore(alertDiv, insertBefore);
        } else {
            const firstElement = document.querySelector('.mb-4');
            if (firstElement) {
                firstElement.parentNode.insertBefore(alertDiv, firstElement.nextSibling);
            } else {
                document.body.insertBefore(alertDiv, document.body.firstChild);
            }
        }
        console.log('[Toast] Error alert inserted into DOM');
    }

    /**
     * Setup swipe-to-dismiss gesture for mobile
     * @param {HTMLElement} element - The toast element
     * @param {Object} toastObj - The toast object
     */
    function setupSwipeGesture(element, toastObj) {
        let startX = 0;
        let currentX = 0;
        let isDragging = false;
        let originalTransform = '';
        let originalOpacity = '';

        element.addEventListener('touchstart', (e) => {
            startX = e.touches[0].clientX;
            isDragging = true;
            // Store original transform and opacity before modification
            originalTransform = element.style.transform;
            originalOpacity = element.style.opacity;
            element.style.transition = 'none'; // Disable transition during drag
        }, { passive: true });

        element.addEventListener('touchmove', (e) => {
            if (!isDragging) return;
            currentX = e.touches[0].clientX;
            const diffX = startX - currentX;
            
            // Only allow swiping to the right (away)
            if (diffX > 0) {
                element.style.transform = `translateX(${diffX}px)`;
                element.style.opacity = 1 - (diffX / 400); // Fade as you swipe
            }
        }, { passive: true });

        element.addEventListener('touchend', (e) => {
            if (!isDragging) return;
            isDragging = false;
            const diffX = startX - currentX;
            
            // If swiped more than 100px, dismiss
            if (diffX > 100) {
                console.log('[Toast] Swiped to dismiss');
                removeToastGracefully(toastObj);
            } else {
                // Reset position to original state
                element.style.transition = 'all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1)';
                element.style.transform = originalTransform;
                element.style.opacity = originalOpacity;
            }
        }, { passive: true });
    }

    /**
     * Remove a toast with graceful animation
     * @param {Object} toastObj - The toast object to remove
     */
    function removeToastGracefully(toastObj) {
        console.log('[Toast] removeToastGracefully called');
        const { element, timeout } = toastObj;
        if (timeout) clearTimeout(timeout);
        
        // Add removing class for slide-out animation
        element.classList.add('removing');
        
        // Remove from DOM after animation completes
        setTimeout(() => {
            element.remove();
            // Remove from tracking array
            const index = toasts.indexOf(toastObj);
            if (index > -1) toasts.splice(index, 1);
            console.log('[Toast] Toast removed, remaining:', toasts.length);
            updateToastPositions();
            
            // If no more toasts, remove container so it doesn't block UI
            if (toasts.length === 0 && toastContainer) {
                toastContainer.remove();
                toastContainer = null;
                console.log('[Toast] Container removed (no more toasts)');
            }
        }, 300);
    }

    // Public API
    return {
        showSuccessToast,
        showToast,
        showErrorAlert,
        removeToastGracefully
    };
})();
