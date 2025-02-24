document.addEventListener("DOMContentLoaded", function () {
    const signupButtons = document.querySelectorAll("#signup, #signup-expanded");

    signupButtons.forEach(button => {
        if (button) {
            button.style.display = "none";
        }
    });

    // Form Validation Logic on Submit

    const form = document.querySelector('.needs-validation'); // Select the form
    form.addEventListener('submit', function (event) {

        if (!form.checkValidity()) {
            event.preventDefault()
            event.stopPropagation()
        }

        form.classList.add('was-validated'); // Add class to show feedback
    });
});