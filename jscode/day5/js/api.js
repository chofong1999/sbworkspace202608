const dataUrl = "http://localhost:8080/api/employees";

   const onSuccess=(data) => {
        $("#employeeTable").empty();
        $("#employeeTable").show();
        // 第一列：標題
        var firstRow = $("<tr><th>員工編號</th><th>員工姓氏</th><th>員工名字</th><th>電子郵件</th><th>Action</th></tr>");
        $("#employeeTable").append(firstRow);
        // 逐筆資料建立表格列
        $.each(data, function (i) {
            var row = $("<tr></tr>");
            var td0 = $("<td></td>").text(this.id).appendTo(row);
            var td1 = $("<td></td>").text(this.lastName).appendTo(row);
            var td2 = $("<td></td>").text(this.firstName).appendTo(row);
            var td3 = $("<td></td>").text(this.email).appendTo(row);
            var td4=$("<td></td>").appendTo(row);
            var editButton=$("<button onclick='editEmployee(" + this.id + ")'>Edit</button>").appendTo(td4);
            var deleteButton=$("<button onclick='deleteEmployee(" + this.id + ")'>Delete</button>").appendTo(td4);
            $("#employeeTable").append(row);
        });
    }
    const deleteEmployee=(id)=>{
        // 這裡可以實現刪除員工的邏輯，例如發送刪除請求
        var confirmDelete = confirm("確定要刪除這位員工嗎？");
        if (!confirmDelete) {
            return; // 如果使用者取消刪除，則直接返回
        }
        $.ajax({
            method: 'DELETE',
            url: dataUrl + "/" + id,
            dataType: "text",
            success: getAllEmployees
        });
    }
    const editEmployee=(id)=>{
        // 這裡可以實現編輯員工的邏輯，例如跳轉到編輯頁面或顯示編輯表單
        $.ajax({
            method: 'GET',
            url: dataUrl + "/" + id,
            dataType: "json",
            success: function(data) {
                // 在這裡處理取得的員工資料，例如填充到編輯表單中
                console.log(data);
                $("#addEmployeeForm").show();
                $("#employeeId").val(data.id);
                $("#lastName").val(data.lastName);
                $("#firstName").val(data.firstName);
                $("#email").val(data.email);
                $("#submitButton").val("update");
                $("#submitButton").text("update");
            }
        });
    }
  
    const getAllEmployees=()=>{
        $.ajax({
            method: 'GET',
            url: dataUrl,
            dataType: "json",
            success: onSuccess
        });
    };
    const start = () => {
        $("#b1").click(function () {
            getAllEmployees();
        });
        $("#b2").click(function () {
            $("#addEmployeeForm").show();
            $("#submitButton").val("add");
            $("#submitButton").text("add");
        });
        $("#submitButton").click(() => {
            const employeeData = {
                id: $("#employeeId").val(),
                lastName: $("#lastName").val(),
                firstName: $("#firstName").val(),
                email: $("#email").val()
            };
            if ($("#submitButton").val() == "add") {
                $.ajax({
                    method: 'POST',
                    url: dataUrl,
                    contentType: "application/json",
                    dataType: "text",
                    data: JSON.stringify(employeeData),
                    success: getAllEmployees
                });                
            } else if ($("#submitButton").val() == "update") {
                $.ajax({
                    method: 'PUT',
                    url: dataUrl + "/" + employeeData.id,
                    contentType: "application/json",
                    dataType: "text",
                    data: JSON.stringify(employeeData),                    
                    success: getAllEmployees
                });                                             
            }
            $("#addEmployeeForm").hide(); 
        });
        
    };
    $(document).ready(start);