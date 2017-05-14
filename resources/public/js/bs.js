var validatorBs;
$(function() {
    validatorBs = $("form[name='bs']").validate({
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

function searchBeerStyles(text) {
    $.ajax({
        type: "POST",
        url: "/bs",
        data: {
            text: text
        },
        dataType: 'json',
        success: function(data) {
            $("#table-bs tbody").html("");
            if (data != null) {
                for (var i = 0; i < data.length; i++) {
                    $("#table-bs tbody").append("<tr></tr>");
                    $("#table-bs tbody").append("<td>" + data[i].id + "</td>");
                    $("#table-bs tbody").append("<td>" + data[i].name + "</td>");
                    $("#table-bs tbody").append("<tr>");
                }
            }
        },
        error: function(request, status, error) {
            console.log(error);
            console.log(request);
        }
    });
}

function getBeerStyle(id) {
    window.location = "/bs/" + id;
}
function editBs(id) {
  if(validatorBs.form()){
var description = $("#description").val();
      $.ajax({
        type: "PUT",
        url: "/bs/"+id,
        data: {
           description:description
        },
        dataType: 'json',
        success: function(data) {
          $("#success-message").html(data);
          $("#success").show();
        },
        error: function(request, status, error) {
            console.log(error);
            console.log(request);
        }
    });

  }

}
function deleteBeerFromBs(id){
      $.ajax({
        type: "DELETE",
        url: "/beer",
        data: {
            id: id
        },
        dataType: 'json',
        success: function(data) {
          $("#"+id).remove();
          showSuccessMesage(data);
        },
        error: function(request, status, error) {
            console.log(error);
            console.log(request);
        }
    });
}
