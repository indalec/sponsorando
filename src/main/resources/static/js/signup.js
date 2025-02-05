document.addEventListener("DOMContentLoaded", function () {

    const signupButtons = document.querySelectorAll("#signup, #signup-expanded");

    signupButtons.forEach(button => {
        if (button) {
            button.style.display = "none";
        }
    });
});
