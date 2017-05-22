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
            picture: {
                required: true
            },
            url: {
                required: true,
                url: true
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
            picture: {
                required: "Please provide a picture"
            },
            url: {
                required: "Please provide a picture"
            }
        },
        submitHandler: function(form) {
            form.submit();
        }
    });
});

$(document).ready(function() {
    $("input[name='optradio']").change(function() {
        var option = $('input[name=optradio]:checked').val();
        if (option == "upload") {
            $("#pic-url").prop("disabled", true);
            $("#pic-upload").prop("disabled", false);
            $("#pic-upload").show();
            $("#pic-url").hide();
        } else {
            $("#pic-url").prop("disabled", false);
            $("#pic-upload").prop("disabled", true);
            $("#pic-upload").hide();
            $("#pic-url").show();
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
        params["picture_url"] = $('#pic-url').val();
    } else {
        params["picture"] = $('#pic-upload').val();
    }
    if (validatorBeer.form()) {
        $.ajax({
            type: "PUT",
            url: "/beer",
            data: params,
            dataType: 'json',
            success: function(data) {
                showSuccessMessage(data);
            },
            error: function(xhr, status, error) {
                showErrorMessage(xhr.responseText);
            }
        });
    }

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
