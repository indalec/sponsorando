document.addEventListener("DOMContentLoaded", function () {
    // Hide login and signup buttons dynamically
    setTimeout(function () {
        const authButtons = document.querySelectorAll("#login, #login-expanded, #signup, #signup-expanded");

        authButtons.forEach(button => {
            if (button) {
                button.style.display = "none";
            }
        });

        console.log("Auth button(s) hidden:", authButtons);
    }, 100);

    // Ensure form, input, and error elements exist before using them
    const loginForm = document.querySelector("form");
    const emailInput = document.getElementById("email");
    let emailError = document.getElementById("emailError");

    // Create email error message dynamically if it doesn’t exist
    if (!emailError && emailInput) {
        emailError = document.createElement("div");
        emailError.id = "emailError";
        emailError.className = "invalid-feedback";
        emailError.style.display = "none";
        emailError.innerText = "Invalid email format";
        emailInput.parentNode.appendChild(emailError);
    }

    // Display login error if wrong credentials
    const urlParams = new URLSearchParams(window.location.search);
    const loginError = urlParams.get("error");

    if (loginError && loginForm) {
        const errorDiv = document.createElement("div");
        errorDiv.className = "alert alert-danger text-center";
        errorDiv.innerText = "Invalid email or password. Please try again.";
        loginForm.parentNode.insertBefore(errorDiv, loginForm);
    }

    // Email format validation
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
