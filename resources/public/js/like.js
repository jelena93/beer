function like(id) {
    $.ajax({
        type: "POST",
        url: "/like",
        data: {
            beer: id
        },
        dataType: 'json',
        success: function(data) {
            $("#likes-count").text(data + " likes");
            $("#button-like").html('<input type="button" value="Dislike" onclick="dislike(' + id + ')"/>');
        },
        error: function(xhr, status, error) {
            showErrorMessage(xhr.responseText);
        }
    });
}

function dislike(id) {
    $.ajax({
        type: "DELETE",
        url: "/like",
        data: {
            beer: id
        },
        dataType: 'json',
        success: function(data) {
            $("#likes-count").text(data + " likes");
            $("#button-like").html('<input type="button" value="Like" onclick="like(' + id + ')"/>');
        },
        error: function(xhr, status, error) {
            showErrorMessage(xhr.responseText);
        }
    });
}
