$( document ).ready(function() {
if($("#success-message").text().length > 0){
  $("#success").show();
}
  if($("#error-message").text().length > 0){
  $("#error").show();
}
});
function showSuccessMesage(text){
  if(text!=null){
  $("#success-message").html(text);
  $("#error").hide();
  $("#success").show();
  }

}
function showErrorMessage(text){
  if(text!=null){
  $("#error-message").html(text);
  $("#error").show();
  $("#success").hide();
}
}
