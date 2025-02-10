document.addEventListener("DOMContentLoaded", function() {
    const fields = ["inputStreet", "inputNumber", "inputCity", "inputPostcode", "inputCountry"];
    const addressInput = document.getElementById("address");
    const suggestionsList = document.getElementById("suggestions");
    let manualInput = false;

    let latitudeElement = document.getElementById('latitude');
    let longitudeElement = document.getElementById('longitude');
    let mapElement = document.getElementById('map');

    let map;
    let marker;

    // Initialize map
    function initializeMap(lat, lon) {
        if (!map) {
            map = L.map('map').setView([lat, lon], 13);

            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                attribution: '© OpenStreetMap contributors'
            }).addTo(map);
        } else {
            map.setView([lat, lon], 13);
        }

        if (marker) map.removeLayer(marker); // Remove previous marker
        marker = L.marker([lat, lon]).addTo(map);
    }

    // Set initial map view if latitude and longitude are available
    if (latitudeElement && longitudeElement && mapElement) {
        let latitude = parseFloat(latitudeElement.value);
        let longitude = parseFloat(longitudeElement.value);

        if (!isNaN(latitude) && !isNaN(longitude)) {
            initializeMap(latitude, longitude);
        } else {
            initializeMap(0, 0); // Default map view if invalid coordinates
        }
    }

    function updateMap(lat, lon) {
        if (map) {
            map.setView([lat, lon], 13);
            if (marker) {
                map.removeLayer(marker);
            }
            marker = L.marker([lat, lon]).addTo(map);
        }
        document.getElementById("latitude").value = lat;
        document.getElementById("longitude").value = lon;
    }

    // Address validation and map update logic
    async function validateAddress() {
        if (manualInput) return;

        let address = fields.map(id => document.getElementById(id).value).filter(val => val).join(", ");
        if (address.length < 5) return;

        let response = await fetch(`https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(address)}&addressdetails=1`);
        let data = await response.json();

        if (data.length > 0) {
            updateMap(data[0].lat, data[0].lon);
        }
    }

    addressInput.addEventListener("input", async function() {
        let query = this.value.trim();
        if (query.length < 3) return;

        let response = await fetch(`https://nominatim.openstreetmap.org/search?format=json&q=${query}&addressdetails=1`);
        let data = await response.json();
        suggestionsList.innerHTML = "";

        if (data.length === 0) return;

        data.forEach(place => {
            let li = document.createElement("li");
            li.textContent = place.display_name;
            li.classList.add("list-group-item");
            li.onclick = () => {
                manualInput = false;
                addressInput.value = place.display_name;
                suggestionsList.innerHTML = "";

                document.getElementById("inputStreet").value = place.address.road || "";
                document.getElementById("inputNumber").value = place.address.number || "";
                document.getElementById("inputCity").value = place.address.city || "";
                document.getElementById("inputPostcode").value = place.address.postcode || "";
                document.getElementById("inputCountry").value = place.address.country || "";

                updateMap(place.lat, place.lon);
            };
            suggestionsList.appendChild(li);
        });
    });

    // Handle Start Date and End Date pickers
    const today = new Date();
    const todayString = today.toISOString().split('T')[0]; // Format: YYYY-MM-DD

    const startDateInput = document.getElementById("startDate");
    const startDateValue = startDateInput.value; // pre-filled date value

    // If the pre-filled date is in the past (before today), set it to today's date
    if (startDateValue && new Date(startDateValue) < today) {
        startDateInput.value = todayString; // Set to today's date if pre-filled date is in the past
    }

    // Initialize Flatpickr for the start date picker
    const startDatePicker = flatpickr(startDateInput, {
        enableTime: true,
        dateFormat: "d/m/Y H:i",  // Ensure this matches the backend format
        time_24hr: true,
        minDate: "today", // Disable past dates
        defaultDate: startDateInput.value, // Use the pre-filled date
        onChange: function(selectedDates, dateStr, instance) {
            // Update the minimum date for end date based on start date selection
            endDatePicker.set('minDate', dateStr);
        }
    });

    // Initialize Flatpickr for the end date picker
    const endDateInput = document.getElementById("endDate");
    const endDateValue = endDateInput.value; // pre-filled date value

    // Set the initial end date to today's date if it's earlier than today
    if (endDateValue && new Date(endDateValue) < today) {
        endDateInput.value = todayString;
    }

    const endDatePicker = flatpickr(endDateInput, {
        enableTime: true,
        dateFormat: "d/m/Y H:i",  // Match the backend format "dd/MM/yyyy HH:mm"
        time_24hr: true,
        minDate: "today", // Disable past dates
        defaultDate: endDateInput.value, // Use the pre-filled date
        onChange: function(selectedDates, dateStr, instance) {
            // Ensure end date is always greater than start date
            if (selectedDates[0] <= startDatePicker.selectedDates[0]) {
                // If end date is before or equal to start date, reset the end date
                instance.setDate(startDatePicker.selectedDates[0], true); // Set end date to be the same as start date
            }
        }
    });

    // Form validation
    const form = document.querySelector('form');
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
        let isStartDateValid = true;
        let isEndDateValid = true;

        if (!startDatePicker.selectedDates[0]) {
            startDateInput.classList.add("is-invalid");
            event.preventDefault();
            event.stopPropagation();
            isStartDateValid = false;
        } else {
            startDateInput.classList.remove("is-invalid");
        }

        if (!endDatePicker.selectedDates[0]) {
            endDateInput.classList.add("is-invalid");
            event.preventDefault();
            event.stopPropagation();
            isEndDateValid = false;
        } else if (!validateDates()) {
            event.preventDefault();
            event.stopPropagation();
            isEndDateValid = false;
        }

        if (!isStartDateValid || !isEndDateValid) {
            return;
        }
    });

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

    (() => {
        'use strict'

        const forms = document.querySelectorAll('.needs-validation')

        Array.from(forms).forEach(form => {
            form.addEventListener('submit', event => {
                if (!form.checkValidity()) {
                    event.preventDefault()
                    event.stopPropagation()
                }

                form.classList.add('was-validated')
            }, false)
        })
    })();

    fields.forEach(field => {
        document.getElementById(field).addEventListener("input", () => {
            manualInput = true;
            validateAddress();
        });
    });
});
