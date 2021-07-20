/** For all forms */
$(document).ready(function () {
  $("#backButton").on("click", function () {
    window.location = moduleURL;
  });

  $("#fileImage").change(function () {
    if (!checkFileSize(this)) {
      return;
    }
    showThumbnail(this);
  });
});

function checkFileSize(fileInput) {
  fileSize = fileInput.files[0].size;

  //1mb
  if (fileSize > MAX_FILE_SIZE) {
    fileInput.setCustomValidity(
      "Image must be under " + (MAX_FILE_SIZE / 1000 - 2.4) + "kb!"
    ); //custom message
    fileInput.reportValidity(); // return false, because the image is too big. Image does not get loaded.
    return false;
  } else {
    fileInput.setCustomValidity("");
    return true;
  }
}

function showThumbnail(fileInput) {
  let file = fileInput.files[0];
  let reader = new FileReader();
  reader.onload = function (e) {
    $("#thumbnail").attr("src", e.target.result);
  };

  reader.readAsDataURL(file);
}

function showModalDialog(title, message) {
  $("#modalTitle").text(title);
  $("#modalBody").text(message);
  $("#modalDialog").modal();
}

function checkUniqueness(form, modeleURL, entity) {
  entityId = $("#id").val();
  entityName = $("#name").val();
  entityAlias = $("#alias").val();

  csrfValue = $("input[name= '_csrf']").val();

  url = modeleURL + "/check_uniqueness";

  params = {
    id: entityId,
    name: (entityName = $("#name").val()),
    alias: entityAlias,
    _csrf: csrfValue,
  };

  $.post(url, params, function (res) {
    if (res == "OK") {
      form.submit();
    } else if (res == "Duplicate") {
      showModalDialog(
        entity + " not unique",
        entityName +
          " already exists in our database. Please try another " +
          entity +
          " name."
      );
    } else {
      showModalDialog("Error", "Unknown server response");
    }
  }).fail(function (err) {
    //        message = err.message ? err.message !== undefined : err.responseJSON.error;
    showModalDialog("Error " + err.status, "Server connection error ");
  });

  return false;
}
