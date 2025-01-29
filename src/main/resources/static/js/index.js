document.addEventListener("DOMContentLoaded", function () {
  var toastEl = document.getElementById('successToast');

  if (toastEl) {
     var toast = new bootstrap.Toast(toastEl);
     toast.show();
  }

});


