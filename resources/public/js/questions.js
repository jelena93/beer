function sendAnswer(){
  var answer = $("input[name='answer']:checked").val();
    $.ajax({
        type: "POST",
        url: "/questions",
        data: {answer:answer},
        dataType: 'json',
        success: function (data) {
                console.log("success "+data);
        },
        error: function (request, status, error) {
          console.log(error);
          console.log(request);
        }
    });



}
