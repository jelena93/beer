var validatorStyle;
$(function() {
    validatorStyle = $("form[name='style']").validate({
        rules: {
            description: {
                required: true
            }
        },
        messages: {
            description: {
                required: "Please provide a description"
            }

        }
    });
});

function searchStyles(text) {
    $.ajax({
        type: "GET",
        url: "/styles",
        data: {
            text: text
        },
        dataType: 'json',
        contentType:"application/json; charset=utf-8",
        success: function(data) {
            var tableData = "";
            if (data != null) {
                for (var i = 0; i < data.length; i++) {
                  tableData +="<tr id='"+data[i].id+"' onclick='getStyle(this.id)' class='clickable-row'>";
                  tableData +="<td>" + data[i].id + "</td>";
                  tableData +="<td>" + data[i].name + "</td>";
                  tableData +="<tr>";
                }
            }
             $("#table-style tbody").html(tableData);
        },
 error: function(xhr, status, error) {
                showErrorMessage(xhr.responseText);
            }
    });
}

function getStyle(id) {
    window.location = "/style/" + id;
}
function editStyle(id) {
  if(validatorStyle.form()){
    var description = $("#description").val();
      $.ajax({
        type: "PUT",
        url: "/style/"+id,
        data: {
           description:description
        },
        dataType: 'json',
        success: function(data) {
          showSuccessMessage(data);
        },
       error: function(xhr, status, error) {
                showErrorMessage(xhr.responseText);
            }
    });

  }

}
function deleteBeerFromStyle(id){
      $.ajax({
        type: "DELETE",
        url: "/beer",
        data: {
            id: id
        },
        dataType: 'json',
        success: function(data) {
          $("#"+id).remove();
          showSuccessMessage(data);
        },
         error: function(xhr, status, error) {
                showErrorMessage(xhr.responseText);
            }
    });
}
