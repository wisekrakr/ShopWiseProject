/** These functions are for list pages, i.e. users or categories. */
// Delete an item within a list. A modal will pop up to confirm deletion.
$(document).ready(function () {
  // delete an item
  $(".link-delete").on("click", function (e) {
    e.preventDefault();

    $("#deleteButton").attr("href", $(this).attr("href"));

    // confirmation modal
    $("#confirmText").text(
      "Are you sure you want to delete: " + $(this).attr("nameOfItem") + "?"
    );
    $("#modalConfirm").modal();
  });
});

//   Clear the search bar.
function clearSearchQuery(moduleURL) {
  window.location = "/ShopWiseAdmin" + moduleURL;
}
