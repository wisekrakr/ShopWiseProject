/** Logout -- In Header ->> Header is everywhere, therefore so will this function. */
$(document).ready(function () {
  $("#logoutLink").on("click", function (e) {
    e.preventDefault();
    document.logoutForm.submit();
  });
});
