$(function() {
    var loading = $('#loadbar').hide();
    $(document)
        .ajaxStart(function() {
            loading.show();
        }).ajaxStop(function() {
            loading.hide();
        });

    $("label.btn").on('click', function() {
        var choice = $(this).find('input:radio').val();
        $('#loadbar').show();
        $('#quiz').fadeOut();
        setTimeout(function() {
            $("#answer").html($(this).checking(choice));
            $('#quiz').show();
            $('#loadbar').fadeOut();
            sendAnswer();
        }, 1500);
    });

    $ans = 3;

    $.fn.checking = function(ck) {
        if (ck != $ans)
            return 'INCORRECT';
        else
            return 'CORRECT';
    };
});

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
                if (data.id != null) {
                    window.location = "/result?bs=" + data.id + "&origin=" + data.origin + "&price=" + data.price;
                } else {
                    $("#question-text").text(data.text);
                    var questions = '';
                    for (var i = 0; i < data.suggestedAnswers.length; i++) {
                        questions += '<label class="element-animation'+(i+1)+' btn btn-lg btn-primary btn-block"><span class="btn-label"><i class="glyphicon glyphicon-chevron-right"></i></span><input type="radio" name="answer" value="' + data.suggestedAnswers[i] + '">' + data.suggestedAnswers[i] + '</label>';
                    }
                    $("#quiz").html(questions);
                }
            },
            error: function(request, status, error) {
                showErrorMessage(error);
            }
        });

    }

}
