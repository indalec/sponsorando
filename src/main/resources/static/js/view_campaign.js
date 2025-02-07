document.addEventListener('DOMContentLoaded', function() {
    let latitudeElement = document.getElementById('latitude');
    let longitudeElement = document.getElementById('longitude');
    let mapElement = document.getElementById('map');

    if (latitudeElement && longitudeElement && mapElement) {
        let latitude = parseFloat(latitudeElement.value);
        let longitude = parseFloat(longitudeElement.value);

        if (!isNaN(latitude) && !isNaN(longitude)) {
            let map = L.map('map').setView([latitude, longitude], 13);

            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                attribution: '© OpenStreetMap contributors'
            }).addTo(map);

            let marker = L.marker([latitude, longitude]).addTo(map);
        }
    }
});
