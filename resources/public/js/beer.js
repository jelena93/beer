function searchBeers(text){
    $.ajax({
        type: "POST",
        url: "/beers",
        data: {text:text},
        dataType: 'json',
        success: function (data) {
          $("#table-beers tbody").html("");
        if(data != null){
                  for(var i=0; i<data.length;i++){
                     $("#table-beers tbody").append("<tr></tr>");
    $("#table-beers tbody").append("<td>"+data[i].id+"</td>");
    $("#table-beers tbody").append("<td>"+data[i].name+"</td>");
    $("#table-beers tbody").append("<td>"+data[i].origin+"</td>");
    $("#table-beers tbody").append("<td>"+data[i].price+"</td>");
    $("#table-beers tbody").append("<td>"+data[i].beer_style+"</td>");
    $("#table-beers tbody").append("<td>"+data[i].alcohol+"</td>");
    $("#table-beers tbody").append("<td>"+data[i].manufacturer+"</td>");
    $("#table-beers tbody").append("<td>"+data[i].country+"</td>");
    $("#table-beers tbody").append("<td>"+data[i].info+"</td>");
    $("#table-beers tbody").append("<tr>");
          }
        }
        },
        error: function (request, status, error) {
          console.log(error);
          console.log(request);
        }
    });
}
function deleteBeer(id){
    $.ajax({
        type: "DELETE",
        url: "/beer",
        data: {id:id},
        dataType: 'json',
        success: function (data) {
          window.location="/beers";
        },
        error: function (request, status, error) {
          console.log(error);
          console.log(request);
        }
    });
}
function getBeer(id){
window.location="/beer/"+id;
}
function likeBeer(id){
    $.ajax({
        type: "POST",
        url: "/beer/like",
        data: {id:id},
        dataType: 'json',
        success: function (data) {
          console.log(data);
        },
        error: function (request, status, error) {
          console.log(error);
          console.log(request);
        }
    });
}
function sendComment(id){
var text = $("#comment").val();
  if(text!=null || text!=""){
      $.ajax({
        type: "POST",
        url: "/beer/comment",
        data: {id:id, text:text},
        dataType: 'json',
        success: function (data) {
          console.log(data);
        },
        error: function (request, status, error) {
          console.log(error);
          console.log(request);
        }
    });
  }
}
