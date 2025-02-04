document.addEventListener("DOMContentLoaded", function() {
        const fields = ["inputStreet", "inputNumber", "inputCity", "inputPostcode", "inputCountry"];
        const addressInput = document.getElementById("address");
        const suggestionsList = document.getElementById("suggestions");
        let manualInput = false;  // Flag to check if user is manually entering address
        let map = L.map('map').setView([0, 0], 2);

        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '© OpenStreetMap contributors'
        }).addTo(map);

        let marker;

        function updateMap(lat, lon) {
            if (marker) map.removeLayer(marker);
            marker = L.marker([lat, lon]).addTo(map);
            map.setView([lat, lon], 13);
            document.getElementById("latitude").value = lat;
            document.getElementById("longitude").value = lon;
        }

        async function validateAddress() {
            if (manualInput) return; // If user manually enters address, don't override it

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
                    manualInput = false; // Reset manual input mode
                    addressInput.value = place.display_name;
                    suggestionsList.innerHTML = "";

                    // Autofill structured address fields
                    document.getElementById("inputStreet").value = place.address.road || "";
                    document.getElementById("inputNumber").value = place.address.house_number || "";
                    document.getElementById("inputCity").value = place.address.city || place.address.town || place.address.village || "";
                    document.getElementById("inputPostcode").value = place.address.postcode || "";
                    document.getElementById("inputCountry").value = place.address.country || "";

                    updateMap(place.lat, place.lon);
                };
                suggestionsList.appendChild(li);
            });
        });

        // Initialize Flatpickr for start date picker
        const startDatePicker = flatpickr("#startDate", {
            enableTime: true,
            dateFormat: "d/m/Y H:i",
            time_24hr: true,
            minDate: "today",
            onChange: function(selectedDates, dateStr, instance) {
                endDatePicker.set('minDate', dateStr);
            }
        });

        // Initialize Flatpickr for end date picker
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

        document.getElementById("goalAmount").addEventListener("input", function (event) {
            let inputField = event.target;
            let value = inputField.value.trim();
            let errorMessage = "";

            // Ensure the value is a valid currency format
            if (value === "") {
                errorMessage = "Please enter the amount you want to raise for your campaign.";
            } else if (!/^\d+(\.\d{1,2})?$/.test(value)) {
                errorMessage = "Please enter a valid number in currency format (e.g., 10.00).";
            } else if (parseFloat(value) < 10) {
                errorMessage = "The minimum goal amount must be at least 10.";
            } else if (parseFloat(value) <= 0) {
                errorMessage = "Please enter a positive amount greater than zero.";
            }

            // Display or remove error message
            if (errorMessage) {
                inputField.classList.add("is-invalid");
                inputField.nextElementSibling.textContent = errorMessage; // Set the error message
            } else {
                inputField.classList.remove("is-invalid");
                inputField.classList.add("is-valid");
                inputField.nextElementSibling.textContent = ""; // Remove error message
            }
        });

        (() => {
            'use strict'

            // Fetch all the forms we want to apply custom Bootstrap validation styles to
            const forms = document.querySelectorAll('.needs-validation')

            // Loop over them and prevent submission
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

        // Add event listeners for manual address input
        fields.forEach(field => {
            document.getElementById(field).addEventListener("input", () => {
                manualInput = true;
                validateAddress();
            });
        });
});
