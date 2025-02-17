document.addEventListener("DOMContentLoaded", function() {
    const searchInput = document.getElementById("searchQuery");
    const sortSelect = document.getElementById("sortByUrgency");
    const campaignCountLabel = document.getElementById("campaignCountLabel");
    let currentPage = 0;

    function performSearch() {
        let searchQuery = searchInput.value;
        let sortBy = sortSelect.value;

        let url = `/discover-campaigns?searchQuery=${encodeURIComponent(searchQuery)}&sortBy=${encodeURIComponent(sortBy)}&page=${currentPage}`;

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


                let pagination = doc.querySelector(".pagination");
                if (pagination) {
                    document.querySelector(".pagination").innerHTML = pagination.innerHTML;
                }
            })
            .catch(error => {
                console.error("Error:", error);
            });
    }


    searchInput.addEventListener("input", function() {
        currentPage = 0;
        performSearch();
    });

    sortSelect.addEventListener("change", function() {
        currentPage = 0;
        performSearch();
    });

    document.addEventListener("click", function(event) {
        if (event.target && event.target.matches('.page-link')) {
            const pageLink = event.target;
            const page = parseInt(pageLink.getAttribute('data-page'));
            if (!isNaN(page)) {
                currentPage = page;
                performSearch();
            }
        }
    });
});
