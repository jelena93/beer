function sendAnswer(){
  var answer = $("input[name='answer']:checked").val();
  if(answer==null){
    $("#error").show();
    $("#error-message").html("<strong>Please select an answer</strong>");
  }else{
    $("#error").hide();
    $.ajax({
        type: "POST",
        url: "/questions",
        data: {answer:answer},
        dataType: 'json',
        success: function (data) {
          $("#question-text").text(data.text);
          $("#question-suggestedAnswers").html("");
          for (var i=0; i<data.suggestedAnswers.length;i++){
          $("#question-suggestedAnswers").append('<input type="radio" name="answer" value="'+data.suggestedAnswers[i]+'">'+
                                               data.suggestedAnswers[i]+'<br>');
          }
        },
        error: function (request, status, error) {
          console.log(error);
          console.log(request);
        }
    });

  }


}
