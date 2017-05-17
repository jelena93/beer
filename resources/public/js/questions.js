$(document).ready(function() {
    $('#question-suggestedAnswers').each(function() {
        $('input[type=radio]', this).get(0).checked = true;
    });
});

function sendAnswer() {
    var answer = $("input[name='answer']:checked").val();
    if (answer == null) {
        $("#error").show();
        $("#error-message").html("<strong>Please select an answer</strong>");
    } else {
        $("#error").hide();
        $.ajax({
            type: "POST",
            url: "/questions",
            data: {
                answer: answer
            },
            dataType: 'json',
            success: function(data) {
              console.log(data);
                if (data.bs !=null) {
                    window.location = "/bs/" + data.bs;
                } else {
                    $("#question-text").text(data.text);
                    $("#question-suggestedAnswers").html("");
                    for (var i = 0; i < data.suggestedAnswers.length; i++) {
                        if (i == 0) {
                            $("#question-suggestedAnswers").append('<input type="radio" name="answer" checked value="' + data.suggestedAnswers[i] + '">' +
                                data.suggestedAnswers[i] + '<br>');
                        } else {
                            $("#question-suggestedAnswers").append('<input type="radio" name="answer" value="' + data.suggestedAnswers[i] + '">' +
                                data.suggestedAnswers[i] + '<br>');
                        }

                    }
                }
            },
            error: function(request, status, error) {
                console.log(error);
                console.log(request);
            }
        });

    }


}
