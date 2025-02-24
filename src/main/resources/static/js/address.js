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
        if (!lat || !lng) {
            console.warn("Invalid latitude or longitude:", lat, lng);
            return;
        }
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
            option.selected = country.name === 'Germany';
            countrySelect.appendChild(option);
        });
    }
    function isValidCoordinate(coord) {
        return typeof coord === 'number' && !isNaN(coord);
    }
    function geocodeAddress(street, number, city, postcode, countryCode) {
        const country = countries.find(c => c.code === countryCode)?.name || countryCode;
        const addressString = `${number} ${street}, ${postcode} ${city}, ${country}`;
        if (addressString === lastValidatedAddress) {
            return;
        }
        fetch(`https://nominatim.openstreetmap.org/search?q=${encodeURIComponent(addressString)}&format=json&addressdetails=1&limit=1`)
            .then(response => response.json())
            .then(data => {
                if (data && data.length > 0) {
                    const result = data[0];
                    const lat = parseFloat(result.lat);
                    const lon = parseFloat(result.lon);
                    if (!isValidCoordinate(lat) || !isValidCoordinate(lon)) {
                        throw new Error('Invalid coordinates received from geocoding.');
                    }
                    latitudeInput.value = lat;
                    longitudeInput.value = lon;
                    // git .textContent = 'Address validated successfully!';
                    geocodeStatus.className = 'form-text text-success';
                    updateMap(lat, lon);
                    // Update the input fields with corrected data
                    streetInput.value = result.address.road || street;
                    cityInput.value = result.address.city || result.address.town || result.address.village || city;
                    fields.forEach(field => {
                        field.classList.remove('is-invalid');
                        field.classList.add('is-valid');
                    });
                    addressValidated = true;
                    lastValidatedAddress = addressString;
                    window.addressData = {
                        street: streetInput.value,
                        number: numberInput.value,
                        city: cityInput.value,
                        postcode: postcodeInput.value,
                        country: countryInput.value,
                        latitude: latitudeInput.value,
                        longitude: longitudeInput.value
                    };
                } else {
                    clearValidation();
                    // geocodeStatus.textContent = 'Address not found. Please check the address.';
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
                // geocodeStatus.textContent = 'Error geocoding address.';
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
        const country = countryInput.value;
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
    fetchCountries();
    window.validateAddress = function() {
        return addressValidated;
    }
    if (latitudeInput.value && longitudeInput.value) {
        const initialLat = parseFloat(latitudeInput.value);
        const initialLng = parseFloat(longitudeInput.value);
        if (isValidCoordinate(initialLat) && isValidCoordinate(initialLng)) {
            updateMap(initialLat, initialLng);
        } else {
            console.warn("Invalid initial latitude or longitude:", initialLat, initialLng);
        }
    }
});
