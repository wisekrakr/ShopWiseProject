let extraImagesCount = 0;

$(document).ready(function () {
  $("input[name='extraFileImage']").each(function (index) {
    extraImagesCount++;
    $(this).change(function () {
      if (!checkFileSize(this)) {
        return;
      }
      showExtraThumbnail(this, index);
    });
  });

  $("a[name='linkRemoveExtraImage']").each(function (index) {
    $(this).click(function () {
      removeExtraImage(index);
    });
  });
});

function showExtraThumbnail(fileInput, index) {
  let file = fileInput.files[0];

  fileName = file.name;

  imageNameHiddenField = $("#imageName" + index);
  if (imageNameHiddenField.length) {
    imageNameHiddenField.val(fileName);
  }

  let reader = new FileReader();
  reader.onload = function (e) {
    $("#extraThumbnail" + index).attr("src", e.target.result);
  };

  reader.readAsDataURL(file);

  if (index >= extraImagesCount - 1) {
    addExtraImageSection(index + 1);
  }
}

function addExtraImageSection(index) {
  htmlExtraThumbnail = `<div class="col border m-3 p-2" id="divExtraImage${index}">
  
    <div id="extraFileImageHeader${index}">
        <label>Add an extra image for this product #${index + 1}</label>
    </div>
  
    <div>
      <input
        type="file"
        onchange="showExtraThumbnail(this, ${index})"
        name="extraFileImage"
        accept="image/png, image/jpg"
      />
    </div>
    <div>
      <img
        src="${defaultImageUrl}"
        alt="Extra Product Image #${index + 1}"
        class="thumbnail-product img-fluid mt-4"
        id="extraThumbnail${index}"
      />
    </div>
  </div>`;

  htmlRemoveLink = `<a class="btn-wise-xs fas fa-times-circle fa-2x float-right"
    href="javascript:removeExtraImage(${index - 1})"
    title="Remove this image"><a/>`;

  $("#divProductImages").append(htmlExtraThumbnail);
  $("#extraFileImageHeader" + (index - 1)).append(htmlRemoveLink);

  extraImagesCount++;
}

function removeExtraImage(index) {
  $("#divExtraImage" + index).remove();
}
