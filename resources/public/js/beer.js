var validatorBeer;
$.validator.addMethod("url2", function(value, element) {
    return this.optional(element) || /^(https?|ftp):\/\/(((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:)*@)?(((\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5]))|((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)*(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?)(:\d*)?)(\/((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)+(\/(([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)*)*)?)?(\?((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|[\uE000-\uF8FF]|\/|\?)*)?(#((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|\/|\?)*)?$/i.test(value);
}, $.validator.messages.url);
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
                required: true,
                url2: true
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
                required: "Please provide a valid url"
            }
        },
        submitHandler: function(form) {
            $('[name="optradio"]').prop("disabled", true);
            form.submit();
        }
    });
});

$(document).ready(function() {
    test();
    $("input[name='optradio']").change(function() {
        test();
    });
});

function test() {
    var option = $('input[name=optradio]:checked').val();
    var img = $("#beer-img").attr('src');
    if (option == "upload") {
        $("#input-picture").html('<input type="file" id="pic-upload" name="file" accept="image/*" />');
    } else if (img != undefined) {
        if (img.indexOf("http") < 0) {
            img = location.protocol + '//' + location.host + img;
        }
        $("#input-picture").html('<input type="text" id="pic-url" name="url" placeholder="Image url" value="' + img + '"/>');
    } else {
        $("#input-picture").html('<input type="text" id="pic-url" name="url" placeholder="Image url"/>');
    }
}

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
        sendEditRequest();
    }

}

function sendEditRequest() {
    var params = new FormData();
    params.append("id", $("#id").val());
    params.append("name", $("#name").val());
    params.append("origin", $('input[name=origin]:checked').val());
    params.append("price", $('input[name=price]:checked').val());
    params.append("style", $("#style").val());
    params.append("alcohol", $("#alcohol").val());
    params.append("manufacturer", $("#manufacturer").val());
    params.append("country", $("#country").val());
    params.append("info", $("#info").val());
    if ($('#pic-url').length > 0) {
        params.append("url", $('#pic-url').val());
    } else {
        params.append('file', $('#pic-upload')[0].files[0]);
    }
    $.ajax({
        type: "PUT",
        url: "/beer",
        data: params,
        dataType: 'json',
        processData: false,
        contentType: false,
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
