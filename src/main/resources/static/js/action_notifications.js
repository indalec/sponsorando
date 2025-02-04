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