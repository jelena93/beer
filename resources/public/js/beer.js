var validatorBeer;
$(function() {
    validatorBeer = $("form[name='beer_add']").validate({
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
            picture_url: {
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
            picture: {
                required: "Please provide a picture"
            },
            picture_url: {
                required: "Please provide a picture"
            }
        },
        submitHandler: function(form) {
            form.submit();
        }
    });
});

$(function() {
    $("form[name='comment']").validate({
        rules: {
            comment: {
                required: true
            }
        },
        messages: {
            comment: {
                required: "Please provide a comment"
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
        type: "POST",
        url: "/beers",
        data: {
            text: text
        },
        dataType: 'json',
        success: function(data) {
            $("#table-beers tbody").html("");
            if (data != null) {
                for (var i = 0; i < data.length; i++) {
                    $("#table-beers tbody").append("<tr></tr>");
                    $("#table-beers tbody").append("<td>" + data[i].id + "</td>");
                    $("#table-beers tbody").append("<td>" + data[i].name + "</td>");
                    $("#table-beers tbody").append("<td>" + data[i].beer_style + "</td>");
                    $("#table-beers tbody").append("<td>" + data[i].alcohol + "</td>");
                    $("#table-beers tbody").append("<td>" + data[i].manufacturer + "</td>");
                    $("#table-beers tbody").append("<td>" + data[i].country + "</td>");
                    $("#table-beers tbody").append("<tr>");
                }
            }
        },
        error: function(request, status, error) {
            showErrorMessage(error);
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
        error: function(request, status, error) {
            showErrorMessage(error);
        }
    });
}

function getBeer(id) {
    window.location = "/beer/" + id;
}

function likeBeer(id) {
    $.ajax({
        type: "POST",
        url: "/beer/like",
        data: {
            id: id
        },
        dataType: 'json',
        success: function(data) {
            console.log(data);
            $("#likes-count").text(data + " likes");
            $("#button-like").html('<input type="button" value="Dislike" onclick="dislikeBeer(' + id + ')"/>');

        },
        error: function(request, status, error) {
            showErrorMessage(error);
        }
    });
}

function dislikeBeer(id) {
    $.ajax({
        type: "DELETE",
        url: "/beer/like",
        data: {
            id: id
        },
        dataType: 'json',
        success: function(data) {
            console.log(data);
            $("#likes-count").text(data + " likes");
            $("#button-like").html('<input type="button" value="Like" onclick="likeBeer(' + id + ')"/>');
        },
        error: function(request, status, error) {
            showErrorMessage(error);
        }
    });
}

function deleteComment(id, beer) {
    $.ajax({
        type: "DELETE",
        url: "/beer/comment/" + id,
        data: {
            beer: beer
        },
        dataType: 'json',
        success: function(data) {
            $("#comment-" + id).remove();
            $("#comments-count").text(data + " comments");
        },
        error: function(request, status, error) {
            showErrorMessage(error);
        }
    });
}
