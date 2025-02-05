document.addEventListener("DOMContentLoaded", function () {
    setTimeout(function () {
        const signupButtons = document.querySelectorAll("#signup, #signup-expanded");

        signupButtons.forEach(button => {
            if (button) {
                button.style.display = "none";
            }
        });

        console.log("Signup button(s) hidden:", signupButtons);
    }, 100);
});
