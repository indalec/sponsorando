<!-- Password matching validation -->

    function validatePasswords() {
    let password = document.getElementById("password").value;
    let confirmPassword = document.getElementById("confirmPassword").value;
    let passwordError = document.getElementById("passwordError");

    if (password && password !== confirmPassword) {
    passwordError.textContent = "Passwords do not match!";
    passwordError.style.display = "block";
    return false;
}

    passwordError.style.display = "none";
    return true;
}

