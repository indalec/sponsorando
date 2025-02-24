document.addEventListener("DOMContentLoaded", function () {

// Hide the respective button based on the current page
    const loginButtons = document.querySelectorAll("#login, #login-expanded");
    loginButtons.forEach(button => {
        if (button) {
            button.style.display = "none";
        }
    });


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

    // Display login error if wrong credentials OR disabled account
    const urlParams = new URLSearchParams(window.location.search);
    const loginError = urlParams.get("error");
    const disabled = urlParams.get("disabled"); // NEW: Check for 'disabled' parameter

    if ((loginError || disabled) && loginForm) {  // Modified condition
        const errorDiv = document.createElement("div");
        errorDiv.className = "alert alert-danger text-center";

        if (loginError) {
            errorDiv.innerText = "Invalid email or password. Please try again.";
        } else if (disabled) {
            errorDiv.innerText = "This account is disabled. Please contact support.";
        }

        loginForm.parentNode.insertBefore(errorDiv, loginForm);
    }

// Email format validation
    const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]+$/;

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
