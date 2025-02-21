// add_campaign.js
document.addEventListener("DOMContentLoaded", function() {

    // Flatpickr initialization (Date pickers)
    const startDatePicker = flatpickr("#startDate", {
        enableTime: true,
        dateFormat: "d/m/Y H:i",
        time_24hr: true,
        minDate: "today",
        onChange: function(selectedDates, dateStr, instance) {
            endDatePicker.set('minDate', dateStr);
        }
    });

    const endDatePicker = flatpickr("#endDate", {
        enableTime: true,
        dateFormat: "d/m/Y H:i",
        time_24hr: true,
        minDate: "today"
    });

    const form = document.querySelector('form');
    const startDateInput = document.getElementById("startDate");
    const endDateInput = document.getElementById("endDate");

    function validateDates() {
        const startDate = startDatePicker.selectedDates[0];
        const endDate = endDatePicker.selectedDates[0];

        if (!startDate || !endDate) {
            return false;
        }

        if (endDate <= startDate) {
            endDateInput.classList.add("is-invalid");
            endDateInput.nextElementSibling.textContent = "End date must be later than the start date.";
            return false;
        } else {
            endDateInput.classList.remove("is-invalid");
            return true;
        }
    }

    endDateInput.addEventListener('change', validateDates);

    form.addEventListener('submit', function(event) {
        if (!startDatePicker.selectedDates[0]) {
            startDateInput.classList.add("is-invalid");
            event.preventDefault();
            event.stopPropagation();
        } else {
            startDateInput.classList.remove("is-invalid");
        }

        if (!endDatePicker.selectedDates[0]) {
            endDateInput.classList.add("is-invalid");
            event.preventDefault();
            event.stopPropagation();
        } else if (!validateDates()) {
            event.preventDefault();
            event.stopPropagation();
        }
    });

    //Goal Amount validation
    document.getElementById("goalAmount").addEventListener("input", function(event) {
        let inputField = event.target;
        let value = inputField.value.trim();
        let errorMessage = "";

        if (value === "") {
            errorMessage = "Please enter the amount you want to raise for your campaign.";
        } else if (!/^\d+(\.\d{1,2})?$/.test(value)) {
            errorMessage = "Please enter a valid number in currency format (e.g., 10.00).";
        } else if (parseFloat(value) < 10) {
            errorMessage = "The minimum goal amount must be at least 10.";
        } else if (parseFloat(value) <= 0) {
            errorMessage = "Please enter a positive amount greater than zero.";
        }

        if (errorMessage) {
            inputField.classList.add("is-invalid");
            inputField.nextElementSibling.textContent = errorMessage;
        } else {
            inputField.classList.remove("is-invalid");
            inputField.classList.add("is-valid");
            inputField.nextElementSibling.textContent = "";
        }
    });

    //Bootstrap Forms validation
    (() => {
        'use strict'
        const forms = document.querySelectorAll('.needs-validation')

        Array.from(forms).forEach(form => {
            form.addEventListener('submit', event => {
                if (!form.checkValidity() || !validateForm()) { // Added validateForm
                    event.preventDefault()
                    event.stopPropagation()
                }
                form.classList.add('was-validated')
            }, false)
        })
    })();

    window.validateForm = function() {
        if (!window.validateAddress()) {
            alert('Please ensure the address is correctly validated before submitting.');
            return false;
        }
        return true;
    }

});
