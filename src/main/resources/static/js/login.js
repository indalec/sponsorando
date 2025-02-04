document.addEventListener("DOMContentLoaded", function () {

    const signupButton = document.getElementById("login");
    if (signupButton) {
        signupButton.style.display = "none";
    }

    const emailInput = document.getElementById("email");
    const emailError = document.getElementById("emailError");
    const loginForm = document.querySelector("form");

    const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

    if (emailInput) {

        emailInput.addEventListener("blur", function () {
            if (!emailPattern.test(emailInput.value)) {
                emailInput.classList.add("is-invalid");
                emailInput.classList.remove("is-valid");
                emailError.style.display = "block";
            } else {
                emailInput.classList.add("is-valid");
                emailInput.classList.remove("is-invalid");
                emailError.style.display = "none";
            }
        });

        if (loginForm) {
            loginForm.addEventListener("submit", function (event) {
                if (!emailPattern.test(emailInput.value)) {
                    emailInput.classList.add("is-invalid");
                    emailError.style.display = "block";
                    event.preventDefault();
                }
            });
        }
    }
});
