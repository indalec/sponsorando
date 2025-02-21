
document.addEventListener("DOMContentLoaded", function() {
    const streetInput = document.getElementById("inputStreet");
    const numberInput = document.getElementById("inputNumber");
    const cityInput = document.getElementById("inputCity");
    const postcodeInput = document.getElementById("inputPostcode");
    const countryInput = document.getElementById("inputCountry");
    const latitudeInput = document.getElementById("latitude");
    const longitudeInput = document.getElementById("longitude");
    const geocodeStatus = document.getElementById("geocode-status");
    const fields = [streetInput, numberInput, cityInput, postcodeInput, countryInput];
    let countries = [];
    let map;
    let marker;
    let addressValidated = false;
    let lastValidatedAddress = '';

    function initMap() {
        map = L.map('map').setView([0, 0], 2);
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '© OpenStreetMap contributors'
        }).addTo(map);
    }
    initMap();

    function updateMap(lat, lng) {
        const newLatLng = new L.LatLng(lat, lng);
        map.setView(newLatLng, 15);
        if (marker) {
            marker.setLatLng(newLatLng);
        } else {
            marker = L.marker(newLatLng).addTo(map);
        }
    }

    function debounce(func, delay) {
        let timeout;
        return function(...args) {
            clearTimeout(timeout);
            timeout = setTimeout(() => func.apply(this, args), delay);
        };
    }

    function clearValidation() {
        addressValidated = false;
        latitudeInput.value = '';
        longitudeInput.value = '';
        geocodeStatus.textContent = '';
        geocodeStatus.className = 'form-text';
        fields.forEach(field => {
            field.classList.remove('is-valid', 'is-invalid');
        });
        if (marker) {
            map.removeLayer(marker);
            marker = null;
            map.setView([0, 0], 2);
        }
    }

    function fetchCountries() {
        const overpassUrl = 'https://overpass-api.de/api/interpreter';
        const query = '[out:json];relation["admin_level"="2"]["ISO3166-1"];out tags;';

        fetch(`${overpassUrl}?data=${encodeURIComponent(query)}`)
            .then(response => response.json())
            .then(data => {
                countries = data.elements.map(element => {
                        return {
                            name: element.tags['name:en'] || element.tags.name,
                            code: element.tags['ISO3166-1']
                        };
                    }
                ).sort((a, b) => a.name.localeCompare(b.name));
                populateCountries();
            })
            .catch(error => {
                console.error('Error fetching countries:', error);
                // Fallback to a default list if the API fails
                countries = [
                    {name: 'Germany', code: 'DE'},
                    {name: 'United States', code: 'US'},
                    {name: 'United Kingdom', code: 'GB'},
                    {name: 'France', code: 'FR'},
                    {name: 'Italy', code: 'IT'},
                    {name: 'Spain', code: 'ES'},
                    {name: 'Netherlands', code: 'NL'},
                    {name: 'Belgium', code: 'BE'},
                    {name: 'Switzerland', code: 'CH'},
                    {name: 'Austria', code: 'AT'}
                ];
                populateCountries();
            });
    }


    function populateCountries() {
        const countrySelect = document.getElementById('inputCountry');
        countries.forEach(country => {
            const option = document.createElement('option');
            option.value = country.code;
            option.textContent = country.name;
            countrySelect.appendChild(option);
        });
    }

    fetchCountries();

    function geocodeAddress(street, number, city, postcode, countryCode) {
        const country = countries.find(c => c.code === countryCode)?.name || countryCode;
        const addressString = `${number} ${street}, ${postcode} ${city}, ${country}`;

        if (addressString === lastValidatedAddress) {
            return; // Skip geocoding if the address hasn't changed
        }

        fetch(`https://nominatim.openstreetmap.org/search?q=${encodeURIComponent(addressString)}&format=json&limit=1`)
            .then(response => response.json())
            .then(data => {
                if (data && data.length > 0) {
                    const result = data[0];
                    const lat = result.lat;
                    const lon = result.lon;

                    latitudeInput.value = lat;
                    longitudeInput.value = lon;

                    geocodeStatus.textContent = 'Address validated successfully!';
                    geocodeStatus.className = 'form-text text-success';
                    updateMap(lat, lon);

                    // Clear all error messages
                    fields.forEach(field => {
                        field.classList.remove('is-invalid');
                        field.classList.add('is-valid');
                    });
                    addressValidated = true;
                    lastValidatedAddress = addressString;
                } else {
                    clearValidation();
                    geocodeStatus.textContent = 'Address not found. Please check the address.';
                    geocodeStatus.className = 'form-text text-danger';

                    fields.forEach(field => {
                        field.classList.remove('is-valid');
                        field.classList.add('is-invalid');
                    });
                }
            })
            .catch(error => {
                console.error('Error geocoding address:', error);
                clearValidation();
                geocodeStatus.textContent = 'Error geocoding address.';
                geocodeStatus.className = 'form-text text-danger';
                fields.forEach(field => {
                    field.classList.remove('is-valid');
                    field.classList.add('is-invalid');
                });
            });
    }

    const debouncedGeocode = debounce(function() {
        const street = streetInput.value.trim();
        const number = numberInput.value.trim();
        const city = cityInput.value.trim();
        const postcode = postcodeInput.value.trim();
        const country = countryInput.value.trim();

        if (street && number && city && postcode && country) {
            geocodeAddress(street, number, city, postcode, country);
        } else {
            clearValidation();
        }
    }, 500);

    fields.forEach(field => {
        field.addEventListener('input', function() {
            debouncedGeocode();
        });

    });

    window.validateForm = function() {
        if (!addressValidated) {
            alert('Please ensure the address is correctly validated before submitting.');
            return false;
        }
        return true;
    }


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
});
