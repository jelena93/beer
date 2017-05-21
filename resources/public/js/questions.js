$(function() {
    var loading = $('#loadbar').hide();
    $(document)
        .ajaxStart(function() {
            loading.show();
        }).ajaxStop(function() {
            loading.hide();
        });
});

function sendAnswer(label){
  var choice = $(label).find('input:radio').val();
  $('#loadbar').show();
  $('#quiz').fadeOut();
  setTimeout(function() {
    $('#quiz').show();
    $('#loadbar').fadeOut();
    send();
  }, 500);
}

function send() {
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
                    window.location = "/result?style=" + data.id + "&origin=" + data.origin + "&price=" + data.price;
                } else {
                    $("#question-text").text(data.text);
                    var questions = '';
                    for (var i = 0; i < data.suggestedAnswers.length; i++) {
                        questions += '<label id="label-quiz" class="element-animation'+(i+1)+' btn btn-lg btn-primary btn-block" onclick="sendAnswer(this)"><span class="btn-label"><i class="glyphicon glyphicon-chevron-right"></i></span><input type="radio" name="answer" value="' + data.suggestedAnswers[i] + '">' + data.suggestedAnswers[i] + '</label>';
                    }
                    $("#quiz").html(questions);
                }
            },
             error: function(xhr, status, error) {
                showErrorMessage(xhr.responseText);
            }
        });

    }

}
