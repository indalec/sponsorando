    document.getElementById('contactForm').addEventListener('submit', function(e) {
      e.preventDefault();

      const inputs = document.querySelectorAll('.form-control');
      inputs.forEach(input => input.classList.remove('is-invalid'));

      let isValid = true;
      const fullName = document.getElementById('fullName');
      const email = document.getElementById('email');
      const message = document.getElementById('message');

      if (!fullName.value.trim()) {
        fullName.classList.add('is-invalid');
        isValid = false;
      }

      if (!email.value.trim() || !email.checkValidity()) {
        email.classList.add('is-invalid');
        isValid = false;
      }

      if (!message.value.trim()) {
        message.classList.add('is-invalid');
        isValid = false;
      }

      if (isValid) {
        const successMessage = document.getElementById('successMessage');
        successMessage.classList.remove('d-none');

        this.reset();

        setTimeout(() => {
          successMessage.classList.add('d-none');
        }, 5000);
      }
    });