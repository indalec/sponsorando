document.addEventListener("DOMContentLoaded", function () {
    const fields = ["inputStreet", "inputNumber", "inputCity", "inputPostcode", "inputCountry"];
    const addressInput = document.getElementById("address");
    const suggestionsList = document.getElementById("suggestions");

    let latitudeElement = document.getElementById('latitude');
    let longitudeElement = document.getElementById('longitude');
    let mapElement = document.getElementById('map');

    let map;
    let marker;

    function initializeMap(lat, lon) {
        if (!map) {
            map = L.map('map').setView([lat, lon], 13);

            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                attribution: '© OpenStreetMap contributors'
            }).addTo(map);
        } else {
            map.setView([lat, lon], 13);
        }

        if (marker) map.removeLayer(marker);
        marker = L.marker([lat, lon]).addTo(map);
    }

    if (latitudeElement && longitudeElement && mapElement) {
        let latitude = parseFloat(latitudeElement.value);
        let longitude = parseFloat(longitudeElement.value);

        if (!isNaN(latitude) && !isNaN(longitude)) {
            initializeMap(latitude, longitude);
        } else {
            initializeMap(0, 0);
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

    async function validateAddress() {
        let address = fields.map(id => document.getElementById(id).value).filter(val => val).join(", ");

        if (address.length < 5) return;

        let response = await fetch(`https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(address)}&addressdetails=1`);
        let data = await response.json();

        if (data.length > 0) {

            updateMap(data[0].lat, data[0].lon);
            addressInput.classList.remove("is-invalid");
            addressInput.nextElementSibling.textContent = "";
        } else {

            addressInput.classList.add("is-invalid");
            const errorMessage = addressInput.nextElementSibling;
            errorMessage.textContent = "Invalid address, please check the entered details.";
            errorMessage.style.color = "red";
        }
    }

    addressInput.addEventListener("input", async function () {
        let query = this.value.trim();
        if (query.length < 3) return;

        let response = await fetch(`https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(query)}&addressdetails=1`);
        let data = await response.json();
        suggestionsList.innerHTML = ""; // Clear previous suggestions

        if (data.length === 0) return;

        let addressMap = new Map();

        data.forEach(place => {

            let addressText = place.display_name;
            let hasHouseNumber = place.address.house_number;

            if (hasHouseNumber) {
                addressText = `${place.address.house_number} ${place.address.road}, ${place.address.city || place.address.town || place.address.village || ""}, ${place.address.postcode || ""}, ${place.address.country}`;
            }

            if (hasHouseNumber && addressMap.has(place.display_name)) {
                addressMap.delete(place.display_name);
            }

            addressMap.set(addressText, place);
        });

        addressMap.forEach((place, addressText) => {
            let li = document.createElement("li");
            li.textContent = addressText; // Display the address
            li.classList.add("list-group-item");

            li.onclick = () => {
                addressInput.value = addressText;
                suggestionsList.innerHTML = "";

                document.getElementById("inputStreet").value = place.address.road || "";
                document.getElementById("inputNumber").value = place.address.house_number || "";  // Ensure street number is autofilled
                document.getElementById("inputCity").value = place.address.city || place.address.town || place.address.village || ""; // Handle city, town, village
                document.getElementById("inputPostcode").value = place.address.postcode || "";
                document.getElementById("inputCountry").value = place.address.country || "";

                updateMap(place.lat, place.lon);
            };

            suggestionsList.appendChild(li);
        });
    });

    const today = new Date();
    const todayString = today.toISOString().split('T')[0];

// Round the current time to the nearest multiple of 5 minutes
    const roundedMinutes = Math.ceil(today.getMinutes() / 5) * 5;
    today.setMinutes(roundedMinutes);
    today.setSeconds(0); // Set seconds to 0 for clean time
    today.setMilliseconds(0); // Set milliseconds to 0 for clean time

    const startDateInput = document.getElementById("startDate");
    const startDateValue = startDateInput.value;

    if (startDateValue && new Date(startDateValue) < today) {
        startDateInput.value = todayString;
    }

    const startDatePicker = flatpickr(startDateInput, {
        enableTime: true,
        dateFormat: "d/m/Y H:i",
        time_24hr: true,
        minDate: "today",
        defaultDate: startDateInput.value,
        minuteIncrement: 5,
        onReady: function (selectedDates, dateStr, instance) {

            const currentTime = `${today.getHours()}:${today.getMinutes().toString().padStart(2, '0')}`;
            if (instance.input.value && new Date(instance.input.value).toISOString().split('T')[0] === todayString) {
                instance.set("minTime", currentTime);
            }
        },
        onChange: function (selectedDates, dateStr, instance) {
            if (selectedDates.length > 0) {
                const selectedDate = selectedDates[0];
                const currentDate = new Date(); // Get the current date and time

                // Add 1 hour to the current time
                currentDate.setHours(currentDate.getHours() + 1);

                // If the selected date is before 1 hour from now, update it to 1 hour ahead
                if (selectedDate.getTime() < currentDate.getTime()) {
                    selectedDate.setTime(currentDate.getTime()); // Set the selected date to 1 hour from now
                    instance.setDate(selectedDate, true); // Update the date picker with the adjusted time
                }

                // If today, set the minimum time to the current time
                if (selectedDate.toISOString().split('T')[0] === todayString) {
                    const currentTime = `${currentDate.getHours()}:${currentDate.getMinutes().toString().padStart(2, '0')}`;
                    instance.set("minTime", currentTime);
                } else {
                    instance.set("minTime", null); // No minTime restriction for future dates
                }
            }
            // Adjust the end date picker to reflect the selected start date
            endDatePicker.set('minDate', dateStr);
        }

    });



    const endDateInput = document.getElementById("endDate");
    const endDateValue = endDateInput.value;

    if (endDateValue && new Date(endDateValue) < today) {
        endDateInput.value = todayString;
    }

    const endDatePicker = flatpickr(endDateInput, {
        enableTime: true,
        dateFormat: "d/m/Y H:i",
        time_24hr: true,
        minDate: "today",
        defaultDate: endDateInput.value,
        minuteIncrement: 5,
        onChange: function (selectedDates, dateStr, instance) {
            if (selectedDates[0] <= startDatePicker.selectedDates[0]) {
                instance.setDate(startDatePicker.selectedDates[0], true);
            }
        }
    });

    const form = document.querySelector('form');

    function validateDates() {
        const startDate = startDatePicker.selectedDates[0];
        const endDate = endDatePicker.selectedDates[0];

        if (!startDate || !endDate) {
            return false;
        }

        // Compare both date and time (using getTime to include the exact time)
        if (endDate.getTime() <= startDate.getTime()) {
            endDateInput.classList.add("is-invalid");
            endDateInput.nextElementSibling.textContent = "End date and time must be later than the start date and time.";
            return false;
        } else {
            endDateInput.classList.remove("is-invalid");
            return true;
        }
    }



    endDateInput.addEventListener('change', validateDates);

    form.addEventListener('submit', function (event) {
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

    document.getElementById("goalAmount").addEventListener("input", function (event) {
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
            validateAddress();
        });
    });
});
