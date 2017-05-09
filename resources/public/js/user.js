function searchUsers(text){
    $.ajax({
        type: "POST",
        url: "/user/search",
        data: {text:text},
        dataType: 'json',
        success: function (data) {
          $("#table-users tbody").html("");
        if(data != null){
                  for(var i=0; i<data.length;i++){
                     $("#table-users tbody").append("<tr></tr>");
    $("#table-users tbody").append("<td>"+data[i].id+"</td>");
    $("#table-users tbody").append("<td>"+data[i].username+"</td>");
    $("#table-users tbody").append("<td>"+data[i].first_name+"</td>");
    $("#table-users tbody").append("<td>"+data[i].last_name+"</td>");
    $("#table-users tbody").append("<td>"+data[i].email+"</td>");
    $("#table-users tbody").append("<tr>");
          }
        }
        },
        error: function (request, status, error) {
          console.log(error);
          console.log(request);
        }
    });

}
