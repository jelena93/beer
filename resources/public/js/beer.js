var validatorBeer;
$(function() {
    validatorBeer = $("form[name='beer']").validate({
        rules: {
            name: {
                required: true
            },
            alcohol: {
                required: true,
                number: true
            },
            manufacturer: {
                required: true
            },
            country: {
                required: true
            },
            info: {
                required: true
            },
            file: {
                required: true
            },
            url: {
                required: true
            }
        },
        messages: {
            name: {
                required: "Please provide a name"
            },
            alcohol: {
                required: "Please provide percent of alcohol"
            },
            manufacturer: {
                required: "Please provide a manufacturer"
            },
            country: {
                required: "Please provide a country"
            },
            info: {
                required: "Please provide an info"
            },
            file: {
                required: "Please provide a file picture"
            },
            url: {
                required: "Please provide a url picture"
            }
        },
        submitHandler: function(form) {
            $('[name="optradio"]').prop("disabled", true);
            form.submit();
        }
    });
});

function validation() {
    if (validatorBeer.form()) {
        if ($("#pic-url").length>0) {
            imageExists($("#pic-url").val(), function(exists) {
                if (exists) {
                    validatorBeer.showErrors({
                        "url": null
                    });
                     $("form[name='beer']").submit();
                } else {
                    validatorBeer.showErrors({
                        "url": "Please add a valid url image"
                    });
                }
            });
        } else {
             $("form[name='beer']").submit();

        }

    }

}

function imageExists(url, callback) {
    console.log(url);
    var img = new Image();
    img.onload = function() {
        callback(true);
    };
    img.onerror = function() {
        callback(false);
    };
    img.src = url;
}


$(document).ready(function() {
    $("input[name='optradio']").change(function() {
        var option = $('input[name=optradio]:checked').val();
        if (option == "upload") {
            $("#input-picture").html('<input type="file" id="pic-upload" name="file" accept="image/*" />');
        } else if (img != undefined) {
            var img = $("#beer-img").attr('src');
            $("#input-picture").html('<input type="text" id="pic-url" name="url" placeholder="Image url" value="' + img + '"/>');
        } else {
            $("#input-picture").html('<input type="text" id="pic-url" name="url" placeholder="Image url"/>');
        }
    });
});

function searchBeers(text) {
    $.ajax({
        type: "GET",
        url: "/beers",
        data: {
            text: text
        },
        dataType: 'json',
        success: function(data) {
            var tableData = "";
            if (data != null) {
                for (var i = 0; i < data.length; i++) {
                    tableData += "<tr>";
                    tableData += "<tr id='" + data[i].id + "' onclick='getBeer(this.id)' class='clickable-row'>";
                    tableData += "<td>" + data[i].id + "</td>";
                    tableData += "<td>" + data[i].name + "</td>";
                    tableData += "<td>" + data[i].sname + "</td>";
                    tableData += "<td>" + data[i].alcohol + "</td>";
                    tableData += "<td>" + data[i].manufacturer + "</td>";
                    tableData += "<td>" + data[i].country + "</td>";
                    tableData += "<tr>";
                }
            }
            $("#table-beers tbody").html(tableData);
        },
        error: function(xhr, status, error) {
            showErrorMessage(xhr.responseText);
        }
    });
}

function editBeer() {
    if (validatorBeer.form()) {
        validateImage();
    }

}

function validateImage() {
    if ($("#pic-url") != undefined) {
        imageExists($("#pic-url").val(), function(exists) {
            if (exists) {
                validatorBeer.showErrors({
                    "url": null
                });
                sendEditRequest();
            } else {
                validatorBeer.showErrors({
                    "url": "Please add a valid url image"
                });
            }
        });
    } else {
         $("form[name='beer']").submit();


    }

}

function sendEditRequest() {
    var params = {};
    params["id"] = $("#id").val();
    params["name"] = $("#name").val();
    params["origin"] = $('input[name=origin]:checked').val();
    params["price"] = $('input[name=price]:checked').val();
    params["style"] = $("#style").val();
    params["alcohol"] = $("#alcohol").val();
    params["manufacturer"] = $("#manufacturer").val();
    params["country"] = $("#country").val();
    params["info"] = $("#info").val();

    if ($('#pic-url').val() != "") {
        params["file"] = $('#pic-url').val();
    } else {
        params["url"] = $('#pic-upload').val();
    }

    $.ajax({
        type: "PUT",
        url: "/beer",
        data: params,
        dataType: 'json',
        success: function(data) {
            $("#beer-picture").html('<img src="' + data.beer.picture + '" class="img-responsive zoom-img"/>');
            showSuccessMessage(data.message);
        },
        error: function(xhr, status, error) {
            showErrorMessage(xhr.responseText);
        }
    });

}

function deleteBeer(id) {
    $.ajax({
        type: "DELETE",
        url: "/beer",
        data: {
            id: id
        },
        dataType: 'json',
        success: function(data) {
            window.location = "/beers";
        },
        error: function(xhr, status, error) {
            showErrorMessage(xhr.responseText);
        }
    });
}

function getBeer(id) {
    window.location = "/beer/" + id;
}
