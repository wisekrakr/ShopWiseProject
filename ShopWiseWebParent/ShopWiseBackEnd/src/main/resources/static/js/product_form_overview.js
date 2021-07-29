dropdownBrands = $("#brand");
dropdownCategories = $("#category");

$(document).ready(function () {
  $("#description").richText();
  $("#summary").placeholderTypewriter({
    text: [
      "Write a small summary here...",
      "Keep it short...",
      "Keep it simple... ",
      "You can write a full description below... And make it as elaborate as you'd like",
    ],
  });

  dropdownBrands.change(function () {
    dropdownCategories.empty();
    getCategories();
  });

  getCategoriesForNewForm();
});

function getCategories() {
  brandId = dropdownBrands.val();
  url = brandModuleURL + "/" + brandId + "/categories";

  $.get(url, function (resJson) {
    $.each(resJson, function (index, category) {
      $("<option>")
        .val(category.id)
        .text(category.name)
        .appendTo(dropdownCategories);
    });
  });
}

function getCategoriesForNewForm() {
  catIdField = $("#categoryId");
  edit = false;

  if (catIdField.length) {
    edit = true;
  }

  if (!edit) getCategories();
}
