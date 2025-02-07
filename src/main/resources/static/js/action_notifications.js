function confirmDelete(object) {
    return confirm("Are you sure you want to delete this " + object + "?");
}

function showSuccessDiv(elementId) {
    const successDiv = document.querySelector(elementId);

    if (successDiv) {
        setTimeout(() => {
            successDiv.style.display = "none";
        }, 5000);
    }
}

showSuccessDiv("#successDivDelete");
showSuccessDiv("#successDivAdd");
showSuccessDiv("#successDivEdit");

document.addEventListener('DOMContentLoaded', function() {
    var errorMessage = document.getElementById('errorMessage');
    if (errorMessage) {
        var bsAlert = new bootstrap.Alert(errorMessage);
        setTimeout(function() {
            bsAlert.close();
        }, 10000);
    }
});
