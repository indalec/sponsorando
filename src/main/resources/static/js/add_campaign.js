
document.addEventListener("DOMContentLoaded", function() {

    const now = new Date();

    function roundToNearestFiveMinutes(date) {
        const coeff = 1000 * 60 * 5;
        return new Date(Math.ceil(date.getTime() / coeff) * coeff);
    }

    const roundedNow = roundToNearestFiveMinutes(now);

    const startDateInput = document.getElementById("startDate");
    const endDateInput = document.getElementById("endDate");

    function getOneHourLater(date = new Date()) {
        const later = new Date(date.getTime() + 60 * 60 * 1000);
        return roundToNearestFiveMinutes(later);
    }

    const startDatePicker = flatpickr("#startDate", {
        enableTime: true,
        dateFormat: "d/m/Y H:i",
        time_24hr: true,
        minDate: getOneHourLater(),
        defaultDate: getOneHourLater(),
        minuteIncrement: 5,
        onChange: function (selectedDates, dateStr, instance) {
            endDatePicker.set('minDate', dateStr);
            const minEndTime = new Date(selectedDates[0]);
            minEndTime.setHours(minEndTime.getHours() + 1);
            endDatePicker.set('minTime', minEndTime);

            if (endDatePicker.selectedDates[0] <= selectedDates[0]) {
                endDatePicker.setDate(minEndTime);
            }
        }
    });

    const endDatePicker = flatpickr("#endDate", {
        enableTime: true,
        dateFormat: "d/m/Y H:i",
        time_24hr: true,
        minDate: getOneHourLater(getOneHourLater()),
        defaultDate: getOneHourLater(getOneHourLater()),
        minuteIncrement: 5,
        onChange: function (selectedDates, dateStr, instance) {
            if (selectedDates[0] <= startDatePicker.selectedDates[0]) {
                instance.setDate(getOneHourLater(startDatePicker.selectedDates[0]), true);
            }
        }
    });

    function isAtLeastOneHourInFuture(date) {
        const now = new Date();
        const oneHourFromNow = new Date(now.getTime() + 60 * 60 * 1000);
        return date >= oneHourFromNow;
    }

    function validateDates() {
        const startDate = startDatePicker.selectedDates[0];
        const endDate = endDatePicker.selectedDates[0];

        if (!startDate || !endDate) {
            return false;
        }

        if (!isAtLeastOneHourInFuture(startDate)) {
            startDateInput.classList.add("is-invalid");
            startDateInput.nextElementSibling.textContent = "Start date must be at least 1 hour in the future from now.";
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

    const form = document.querySelector('form');


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

    // Title validation
    const titleInput = document.getElementById("title");
    titleInput.addEventListener("input", function(event) {
        const minLength = 2;
        const maxLength = 255;
        const currentLength = event.target.value.length;
        const errorElement = document.getElementById("titleLengthError");

        if (currentLength < minLength || currentLength > maxLength) {
            titleInput.classList.add("is-invalid");
            errorElement.textContent = `Title must be between ${minLength} and ${maxLength} characters. Current length: ${currentLength}`;
        } else {
            titleInput.classList.remove("is-invalid");
            titleInput.classList.add("is-valid");
            errorElement.textContent = "";
        }
    });

    // Description validation
    const descriptionInput = document.getElementById("description");
    descriptionInput.addEventListener("input", function(event) {
        const maxLength = 5000;
        const currentLength = event.target.value.length;
        const errorElement = descriptionInput.nextElementSibling;

        if (currentLength > maxLength) {
            descriptionInput.classList.add("is-invalid");
            errorElement.textContent = `Description must not exceed ${maxLength} characters. Current length: ${currentLength}`;
        } else {
            descriptionInput.classList.remove("is-invalid");
            descriptionInput.classList.add("is-valid");
            errorElement.textContent = "";
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
                const submitErrorMessage = document.getElementById('submitErrorMessage');
                if (!form.checkValidity() || !window.validateAddress() || !validateDates()) {
                    event.preventDefault()
                    event.stopPropagation()

                    // Display error message
                    submitErrorMessage.style.display = 'block';
                    // Hide the error message after 3 seconds
                    setTimeout(() => {
                        submitErrorMessage.style.display = 'none';
                    }, 3000);
                } else {
                    // Hide error message if form is valid
                    submitErrorMessage.style.display = 'none';
                }
                form.classList.add('was-validated')
            }, false)
        })
    })();

    // window.validateForm = function() {
    //     if (!window.validateAddress()) {
    //         alert('Please ensure the address is correctly validated before submitting.');
    //         return false;
    //     }
    //     return true;
    // }

});
