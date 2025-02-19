document.addEventListener("DOMContentLoaded", fetchUsers);

function openUserDetails(event, element) {
    event.stopPropagation();
    let userId = element.closest("tr").getAttribute("data-user-id");

    fetch(`/users/${userId}`)
        .then(response => response.json())
        .then(data => {
            // Populate user information
            document.getElementById("panelUserName").innerText = data.username;
            document.getElementById("panelUserEmail").innerText = data.email;
            document.getElementById("panelUserRole").innerText = data.role; // Assuming role is an object with a 'name' property
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
        });
}

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

function deleteUser() {
    let userId = document.getElementById("panelUserId").value;
    if (confirm("Are you sure you want to delete this user?")) {
        fetch(`/users/${userId}`, { method: 'DELETE' })
            .then(response => location.reload());
    }
}

function fetchUsers() {
    fetch("/users/api")
        .then(response => response.json())
        .then(users => {
            let tbody = document.querySelector("#userTable tbody");
            tbody.innerHTML = "";
            users.forEach(user => {
                let row = `
                <tr data-user-id="${user.id}" onclick="openUserDetails(event, this)">
                    <td>${user.id}</td>
                    <td>${user.name}</td>
                    <td>${user.email}</td>
                    <td>${user.role}</td>
                    <td><span class="badge ${user.enabled ? 'bg-success' : 'bg-danger'}">${user.enabled ? 'ACTIVE' : 'SUSPENDED'}</span></td>
                    <td>
                        <button class="btn btn-outline-primary btn-sm" onclick="openUserDetails(event, this)">Manage</button>
                    </td>
                </tr>
            `;
                tbody.innerHTML += row;
            });
        });
}
