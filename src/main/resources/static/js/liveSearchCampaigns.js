document.getElementById("searchQuery").addEventListener("input", function () {
    let searchQuery = this.value;
    let currentPage = 0;

    let url = `/discover_campaigns?searchQuery=${encodeURIComponent(searchQuery)}&page=${currentPage}`;

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
        })
        .catch(error => {
            console.error("Error:", error);
        });
});