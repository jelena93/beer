var commentValidator;
$(function() {
    commentValidator = $("form[name='comment']").validate({
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

function addComment(beer) {
    if (commentValidator.form()) {
        var comm = $("#comment").val();
        $.ajax({
            type: "POST",
            url: "/comment",
            data: {
                beer: beer,
                comment: comm
            },
            dataType: 'json',
            success: function(data) {
                $("#comments-count").text(data.length + " comments");
                var commentData = "<ul class='comments'>";
                for (var i = 0; i < data.length; i++) {
                    commentData += '<li id="comment-' + data[i].id + '" class="comment clearfix">';
                    commentData += '<div class="clearfix">';
                    commentData += '<h4 class="pull-left">' + data[i].name + ' ' + data[i].surname + '</h4>';
                    commentData += '<p class="pull-right">' + data[i].date + '</p>';
                    commentData += '</div><p>';
                    commentData += '<em>' + data[i].comment + '</em>';
                    commentData += '</p></li>';
                }
                commentData += '</ul>';
                $("#comment").val('');
                $("#comments").html(commentData);
            },
            error: function(xhr, status, error) {
                showErrorMessage(xhr.responseText);
            }
        });
    }
}

function deleteComment(id, beer) {
    $.ajax({
        type: "DELETE",
        url: "/comment/" + id,
        data: {
            beer: beer
        },
        dataType: 'json',
        success: function(data) {
            $("#comment-" + id).remove();
            $("#comments-count").text(data + " comments");
        },
        error: function(xhr, status, error) {
            showErrorMessage(xhr.responseText);
        }
    });
}
