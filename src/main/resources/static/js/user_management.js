document.addEventListener("DOMContentLoaded", function() {
    const showPanelButtons = document.querySelectorAll(".show-panel-btn");

    showPanelButtons.forEach(button => {
        button.addEventListener("click", function(event) {
            event.preventDefault(); // Prevent default button behavior

            const userId = this.getAttribute("data-user-id");
            fetch(`/users/${userId}`)
                .then(response => response.json())
                .then(data => {
                    try{
                        // Populate user information
                        document.getElementById("panelUserName").innerText = data.username;
                        document.getElementById("panelUserEmail").innerText = data.email;
                        document.getElementById("panelUserRole").innerText = data.role.name;
                        document.getElementById("panelUserStatus").innerText = data.enabled ? 'ACTIVE' : 'SUSPENDED';
                        document.getElementById("panelUserId").value = data.id;

                        // Populate campaign statistics
                        document.getElementById("panelTotalCampaigns").innerText = data.totalCampaigns;
                        document.getElementById("panelDraftCampaigns").innerText = data.draftCampaigns;
                        document.getElementById("panelActiveCampaigns").innerText = data.activeCampaigns;
                        document.getElementById("panelInactiveCampaigns").innerText = data.inactiveCampaigns;
                        document.getElementById("panelFrozenCampaigns").innerText = data.frozenCampaigns;
                        document.getElementById("panelCompletedCampaigns").innerText = data.completedCampaigns;

                        // Populate donation information TODO
                        document.getElementById("panelDonationsCollected").innerText = data.totalDonationsCollectedInEuro ? data.totalDonationsCollectedInEuro.toFixed(2) : '0.00';
                        document.getElementById("panelDonationsGiven").innerText = data.totalDonationsGivenInEuro ? data.totalDonationsGivenInEuro.toFixed(2) : '0.00';

                        // Update toggle button
                        let toggleButton = document.getElementById("toggleUserStatusBtn");
                        if (data.enabled) {
                            toggleButton.innerText = "Deactivate User";
                            toggleButton.className = "btn btn-danger w-100 mb-2";
                        } else {
                            toggleButton.innerText = "Activate User";
                            toggleButton.className = "btn btn-success w-100 mb-2";
                        }

                        new bootstrap.Offcanvas(document.getElementById("userDetailsPanel")).show();
                    } catch (e) {
                        console.log ("There was an error with the panel data "+e)
                    }
                })
        });
    });

    const searchInput = document.getElementById('searchInput');
    const searchButton = document.getElementById('searchButton');

    function performSearch() {
        const searchTerm = searchInput.value.trim();
        if (searchTerm) {
            const currentUrl = new URL(window.location.href);
            currentUrl.searchParams.set('search', searchTerm);
            currentUrl.searchParams.set('page', '0');
            window.location.href = currentUrl.toString();
        }
    }

    if (searchButton) {
        searchButton.addEventListener('click', performSearch);
    }

    if (searchInput) {
        searchInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                performSearch();
            }
        });

        // Populate search input with current search term if it exists
        const urlParams = new URLSearchParams(window.location.search);
        const currentSearch = urlParams.get('search');
        if (currentSearch) {
            searchInput.value = currentSearch;
        }
    }
});

function toggleUserStatus() {
    let userId = document.getElementById("panelUserId").value;
    let form = document.getElementById("toggleUserStatusForm");
    let userIdInput = document.getElementById("userIdInput");

    if (!userId) {
        console.error("Error: User ID is missing. Cannot toggle status.");
        return;
    }

    userIdInput.value = userId;
    form.submit();
}


