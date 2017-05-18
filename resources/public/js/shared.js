$( document ).ready(function() {
if($("#success-message").text().length > 0){
  $("#success").show();
}
  if($("#error-message").text().length > 0){
  $("#error").show();
}
});
function showSuccessMessage(text){
  if(text!=null){

    $("#message").html('<div class="alert alert-success alert-dismissable"><a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>'+text+'</div>');
  }

}
function showErrorMessage(text){
  if(text!=null){
    $("#message").html('<div class="alert alert-danger alert-dismissable"><a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>'+text+'</div>');
}
}
