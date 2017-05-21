var validatorUser;
var validatorUserPass;

$(function() {
    validatorUser = $("form[name='user']").validate({
        rules: {
            username: {
                required: true
            },
            password: {
                required: true
            },
            name: {
                required: true
            },
            surname: {
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
            name: {
                required: "Please provide a name"
            },
            surame: {
                required: "Please provide a surname"
            },
            email: {
                required: "Please provide an email"
            },

        },
        submitHandler: function(form) {
            form.submit();
        }
    });
  validatorUserPass = $("form[name='pass']").validate({
        rules: {
            password: {
                required: true
            }
        },
        messages: {
            password: {
                required: "Please provide a new password"
            }
        },
        submitHandler: function(form) {
            form.submit();
        }
    });
});

function editUser() {
    var id = $("#id").val();
    var username = $("#username").val();
    var password = $("#password").val();
    var name = $("#name").val();
    var surname = $("#surname").val();
    var email = $("#email").val();
    if (validatorUser.form()) {
        $.ajax({
            type: "PUT",
            url: "/user",
            data: {
                username: username,
                password: password,
                name: name,
                surname: surname,
                email: email
            },
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

function changePassword() {
    var id = $("#id").val();
    var newp = $("#new-password").val();
    if (validatorUserPass.form()) {
        $.ajax({
            type: "PUT",
            url: "/pass",
            data: {
                password: newp
            },
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

function deleteUser() {
    var id = $("#id").val();
    $.ajax({
        type: "DELETE",
        url: "/user",
        dataType: 'json',
        success: function(data) {
            window.location = "/login";
        },
      error: function(xhr, status, error) {
        showErrorMessage(xhr.responseText);
      }
    });

}
