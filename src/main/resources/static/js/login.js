document.addEventListener("DOMContentLoaded", function () {
    setTimeout(function () {
        const loginButtons = document.querySelectorAll("#login, #login-expanded");

        loginButtons.forEach(button => {
            if (button) {
                button.style.display = "none";
            }
        });

        console.log("Login button(s) hidden:", loginButtons);
    }, 100);
});
