var validatorUser;
$(function() {
    validatorUser = $("form[name='user']").validate({
        rules: {
            username: {
                required: true
            },
            password: {
                required: true
            },
            first_name: {
                required: true
            },
            last_name: {
                required: true
            },
            email: {
                required: true,
                email: true
            }
        },
        messages: {
            username: {
                required: "Please provide a username"
            },
            password: {
                required: "Please provide a password"
            },
            first_name: {
                required: "Please provide a first name"
            },
            last_name: {
                required: "Please provide a last name"
            },
            email: {
                required: "Please provide an email"
            },

        },
        submitHandler: function(form) {
            form.submit();
        }
    });
});



function searchUsers(text) {
    $.ajax({
        type: "POST",
        url: "/users",
        data: {
            text: text
        },
        dataType: 'json',
        success: function(data) {
            $("#table-users tbody").html("");
            if (data != null) {
                for (var i = 0; i < data.length; i++) {
                    $("#table-users tbody").append("<tr></tr>");
                    $("#table-users tbody").append("<td>" + data[i].id + "</td>");
                    $("#table-users tbody").append("<td>" + data[i].username + "</td>");
                    $("#table-users tbody").append("<td>" + data[i].first_name + "</td>");
                    $("#table-users tbody").append("<td>" + data[i].last_name + "</td>");
                    $("#table-users tbody").append("<td>" + data[i].email + "</td>");
                    $("#table-users tbody").append("<tr>");
                }
            }
        },
        error: function(request, status, error) {
          showErrorMessage(error);
        }
    });

}

function editUser() {
    var id = $("#id").val();
    var username = $("#username").val();
    var password = $("#password").val();
    var first_name = $("#first_name").val();
    var last_name = $("#last_name").val();
    var email = $("#email").val();
    if (validatorUser.form()) {
        $.ajax({
            type: "PUT",
            url: "/user/" + id,
            data: {
                username: username,
                password: password,
                first_name: first_name,
                last_name: last_name,
                email: email
            },
            dataType: 'json',
            success: function(data) {
                showSuccessMessage(data);
            },
            error: function(request, status, error) {
                showErrorMessage(error);
            }
        });
    }

}

function deleteUser() {
    var id = $("#id").val();
    var username = $("#username").val();
    var password = $("#password").val();
    var first_name = $("#first_name").val();
    var last_name = $("#last_name").val();
    var email = $("#email").val();
    $.ajax({
        type: "DELETE",
        url: "/user/" + id,
        dataType: 'json',
        success: function(data) {
            window.location = "/login";
        },
        error: function(request, status, error) {
          showErrorMessage(error);
        }
    });

}
