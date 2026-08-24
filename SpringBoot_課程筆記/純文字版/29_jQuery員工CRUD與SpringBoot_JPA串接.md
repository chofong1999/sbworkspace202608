# jQuery員工CRUD與Spring Boot JPA串接

[返回JavaScript課程功能快速索引](00_JavaScript功能快速索引.md)

本章把第26、27章的前後端分離擴充成完整員工CRUD。前端以Live Server執行HTML與jQuery，後端以Spring Boot、Spring Data JPA及MySQL保存資料。

課堂來源：

```text
前端：week7/day5/jquery_employee.html、week7/day5/js/api.js
後端：sbemployeetest
```

## 1. 本章功能快速索引

| 功能 | 主要寫法 |
|---|---|
| 比較值與型別 | `===` |
| 只比較轉型後的值 | `==` |
| 建立員工資料表 | `@Entity`、`JpaRepository<Employee, Integer>` |
| 初始化測試資料 | `CommandLineRunner` |
| 允許Live Server呼叫API | `@CrossOrigin("*")` |
| 取得全部／單筆 | `GET /api/employees`、`GET /api/employees/{id}` |
| 新增／修改／刪除 | `POST`、`PUT`、`DELETE` |
| 動態建立表格列 | `$.each()`、`$('<tr>')`、`.appendTo()` |
| 傳送JSON | `JSON.stringify()`、`contentType: "application/json"` |
| 共用新增與編輯表單 | 按鈕的`.val("add")`／`.val("update")` |

## 2. 前置條件

- 已完成第13章的Spring Data JPA與MySQL。
- 已完成第26、27章，知道jQuery AJAX、JSON與CORS。
- MySQL已啟動，並存在`mydb`資料庫。
- 前端使用VS Code Live Server；後端使用Eclipse或Maven啟動。

## 3. `==`與`===`

課堂先以`equals.html`比較字串`"10"`與數字`10`：

```javascript
let a = "10";
let b = 10;

console.log(a === b); // false
console.log(a == b);  // true
console.log(typeof a); // string
console.log(typeof b); // number
```

- `===`是嚴格相等：值與型別都必須相同。
- `==`會先進行型別轉換，再比較值。

表單欄位的`.val()`通常得到String，因此判斷模式時雖然`==`可運作，沒有轉型需求時仍優先使用`===`。

## 4. 建立後端專案

`pom.xml`需要：

- Spring Web
- Spring Data JPA
- MySQL Driver
- Lombok
- Spring Boot DevTools（開發時使用）

課堂專案使用Spring Boot 3.3.2與Java 17。JDK 21可以執行以Java 17為目標版本的專案。

`application.properties`：

```properties
spring.application.name=sbngemployee0820
spring.datasource.url=jdbc:mysql://localhost/mydb
spring.datasource.username=root
spring.datasource.password=1234
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.hbm2ddl.auto=create-drop
spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
```

`create-drop`會在啟動時建立資料表、停止時刪除資料表，適合課堂實驗，不適合保存正式資料。

## 5. Employee與Repository

建立`src/main/java/demo/example/model/Employee.java`：

```java
package demo.example.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "employees")
@Data
public class Employee {
    @Id
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;

    public Employee() {
    }

    public Employee(Integer id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
}
```

建立`EmployeeRepository.java`：

```java
package demo.example.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository
        extends JpaRepository<Employee, Integer> {
}
```

## 6. REST Controller

建立`src/main/java/demo/example/controller/EmployeeController.java`：

```java
package demo.example.controller;

import java.util.Comparator;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import demo.example.model.Employee;
import demo.example.model.EmployeeRepository;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/employees")
public class EmployeeController implements CommandLineRunner {
    private final EmployeeRepository dao;

    public EmployeeController(EmployeeRepository dao) {
        this.dao = dao;
    }

    @GetMapping
    public List<Employee> getAllEmployees() {
        return dao.findAll();
    }

    @GetMapping("/{id}")
    public Employee findEmployee(@PathVariable Integer id) {
        return dao.findById(id).orElseThrow();
    }

    @PostMapping
    public ResponseEntity<String> saveEmployee(@RequestBody Employee emp) {
        int nextId = dao.findAll().stream()
                .max(Comparator.comparing(Employee::getId))
                .orElseThrow()
                .getId() + 1;
        emp.setId(nextId);
        dao.save(emp);
        return ResponseEntity.ok("added");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateEmployee(
            @PathVariable Integer id,
            @RequestBody Employee emp) {
        Employee current = dao.findById(id).orElseThrow();
        current.setLastName(emp.getLastName());
        current.setFirstName(emp.getFirstName());
        current.setEmail(emp.getEmail());
        dao.save(current);
        return ResponseEntity.ok("updated");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Integer id) {
        Employee current = dao.findById(id).orElseThrow();
        dao.delete(current);
        return ResponseEntity.ok("deleted");
    }

    @Override
    public void run(String... args) {
        if (dao.count() == 0) {
            dao.save(new Employee(100, "Mary", "Wu", "mary@demo.com"));
            dao.save(new Employee(101, "Tony", "Lee", "tony@demo.com"));
            dao.save(new Employee(102, "Janet", "Chen", "janet@demo.com"));
        }
    }
}
```

端點：

| Method | URL | 用途 |
|---|---|---|
| GET | `/api/employees` | 全部員工 |
| GET | `/api/employees/{id}` | 單筆員工 |
| POST | `/api/employees` | 新增員工 |
| PUT | `/api/employees/{id}` | 更新員工 |
| DELETE | `/api/employees/{id}` | 刪除員工 |

上例沿用課堂的手動編號方式。若資料表可能為空，`max(...).orElseThrow()`會失敗；一般專案宜讓資料庫自動編號，或另外定義空表時的初始值。

## 7. 建立前端HTML

建立`C:\jscode\day5\jquery_employee.html`：

```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Employee CRUD</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.4/jquery.min.js"></script>
    <script src="./js/api.js"></script>
</head>
<body>
    <button id="b1">All Employees</button>
    <button id="b2">Add Employee</button>

    <form id="addEmployeeForm" style="display:none">
        <input id="employeeId" placeholder="Employee ID">
        <input id="firstName" placeholder="First Name" required>
        <input id="lastName" placeholder="Last Name" required>
        <input id="email" type="email" placeholder="Email" required>
        <button type="button" id="submitButton" value="add">Submit</button>
    </form>

    <table id="employeeTable" border="1" style="display:none" width="40%"></table>
</body>
</html>
```

`api.js`放在HTML旁的`js/`資料夾。jQuery必須先載入，否則`api.js`使用`$`時會出現`$ is not defined`。

## 8. 取得資料並建立表格

```javascript
const dataUrl = "http://localhost:8080/api/employees";

const onSuccess = (data) => {
    $("#employeeTable").empty().show();
    $("#employeeTable").append(
        "<tr><th>員工編號</th><th>姓氏</th><th>名字</th>" +
        "<th>Email</th><th>Action</th></tr>"
    );

    $.each(data, function () {
        const row = $("<tr></tr>");
        $("<td></td>").text(this.id).appendTo(row);
        $("<td></td>").text(this.lastName).appendTo(row);
        $("<td></td>").text(this.firstName).appendTo(row);
        $("<td></td>").text(this.email).appendTo(row);

        const actions = $("<td></td>").appendTo(row);
        $("<button>Edit</button>")
            .click(() => editEmployee(this.id))
            .appendTo(actions);
        $("<button>Delete</button>")
            .click(() => deleteEmployee(this.id))
            .appendTo(actions);

        $("#employeeTable").append(row);
    });
};

const getAllEmployees = () => {
    $.ajax({
        method: "GET",
        url: dataUrl,
        dataType: "json",
        success: onSuccess
    });
};
```

## 9. 新增與修改共用表單

```javascript
const editEmployee = (id) => {
    $.ajax({
        method: "GET",
        url: `${dataUrl}/${id}`,
        dataType: "json",
        success: function (employee) {
            $("#addEmployeeForm").show();
            $("#employeeId").val(employee.id);
            $("#lastName").val(employee.lastName);
            $("#firstName").val(employee.firstName);
            $("#email").val(employee.email);
            $("#submitButton").val("update").text("update");
        }
    });
};

const sendForm = () => {
    const employee = {
        id: $("#employeeId").val(),
        lastName: $("#lastName").val(),
        firstName: $("#firstName").val(),
        email: $("#email").val()
    };

    const updating = $("#submitButton").val() === "update";

    $.ajax({
        method: updating ? "PUT" : "POST",
        url: updating ? `${dataUrl}/${employee.id}` : dataUrl,
        contentType: "application/json",
        dataType: "text",
        data: JSON.stringify(employee),
        success: getAllEmployees
    });

    $("#addEmployeeForm").hide();
};
```

這裡的`.val("update")`保存操作模式，`.text("update")`只改變按鈕顯示文字；兩者用途不同。

## 10. 刪除與事件註冊

```javascript
const deleteEmployee = (id) => {
    if (!confirm("確定要刪除這位員工嗎？")) {
        return;
    }

    $.ajax({
        method: "DELETE",
        url: `${dataUrl}/${id}`,
        dataType: "text",
        success: getAllEmployees
    });
};

const start = () => {
    $("#b1").click(getAllEmployees);

    $("#b2").click(function () {
        $("#addEmployeeForm").show();
        $("#submitButton").val("add").text("add");
    });

    $("#submitButton").click(sendForm);
};

$(document).ready(start);
```

每次新增、修改或刪除成功後都呼叫`getAllEmployees`，重新向後端查詢，避免畫面與資料庫不同步。

## 11. 啟動與驗證

1. 啟動MySQL並確認`mydb`存在。
2. 以Spring Boot App啟動`sbemployeetest`。
3. 開啟`http://localhost:8080/api/employees`，預期先看到三筆種子資料。
4. 以Live Server開啟`jquery_employee.html`。
5. 按`All Employees`，應建立員工表格。
6. 按`Add Employee`新增一筆；成功後表格應重新載入。
7. 按`Edit`修改姓名或Email；成功後表格應顯示新值。
8. 按`Delete`並確認；成功後該列應消失。

## 12. 課堂版本的限制

- `findById(id).get()`在ID不存在時會拋出例外；正式API應回傳404。
- `Integer`物件不應使用`==`判斷同一數值；優先使用`findById()`或`.equals()`。
- 新增時以`findAll().max()`計算ID，空表會失敗，也可能遇到並行新增衝突。
- 刪除失敗仍回傳200與`delete failed`，不能只靠狀態碼判斷成功。
- `type="button"`不會觸發HTML form submit驗證流程，因此`required`不一定阻止AJAX送出；需要自行驗證或改用`submit`事件。
- `@CrossOrigin("*")`適合課堂測試，正式環境應限制來源。
- 本機`api.js`與老師最新版目前有排版及callback寫法差異，但CRUD流程相同；判讀課程最新版時以老師來源為準。

## 13. 檢查表

- [ ] 能說明`==`與`===`的差異。
- [ ] 能建立Employee Entity與JpaRepository。
- [ ] 能完成五個CRUD端點。
- [ ] 能以jQuery動態建立員工表格。
- [ ] 能讓新增與修改共用同一張表單。
- [ ] 能在成功後重新讀取資料。
- [ ] 知道課堂版ID、404、表單驗證及CORS的限制。
