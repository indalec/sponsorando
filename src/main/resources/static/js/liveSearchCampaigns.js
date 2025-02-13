document.addEventListener("DOMContentLoaded", function() {
    const searchInput = document.getElementById("searchQuery");
    const sortSelect = document.getElementById("sortByUrgency");
    const campaignCountLabel = document.getElementById("campaignCountLabel");

    function performSearch() {
        let searchQuery = searchInput.value;
        let sortBy = sortSelect.value;
        let currentPage = 0;

        let url = `/discover_campaigns?searchQuery=${encodeURIComponent(searchQuery)}&sortBy=${encodeURIComponent(sortBy)}&page=${currentPage}`;

        fetch(url, {
            method: "GET",
            headers: {
                "Accept": "text/html",
            }
        })
            .then(response => response.text())
            .then(data => {
                let parser = new DOMParser();
                let doc = parser.parseFromString(data, "text/html");
                document.getElementById("campaignsContainer").innerHTML = doc.getElementById("campaignsContainer").innerHTML;

                let newLabel = doc.getElementById("campaignCountLabel");
                if (newLabel) {
                    campaignCountLabel.textContent = newLabel.textContent;
                }
            })
            .catch(error => {
                console.error("Error:", error);
            });
    }

    searchInput.addEventListener("input", performSearch);
    sortSelect.addEventListener("change", performSearch);
});