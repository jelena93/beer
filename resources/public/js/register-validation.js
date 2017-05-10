$(function() {
    $("form[name='register']").validate({
        rules: {
            username: {
                required: true,
                minlength: 5
            },
            password: {
                required: true,
                minlength: 5
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
                required: "Please provide a username",
                minlength: "Your username must be at least 5 characters long"
            },
            password: {
                required: "Please provide a password",
                minlength: "Your password must be at least 5 characters long"
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
