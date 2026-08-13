package com.example.demo.model;


import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.Data;

@Entity                              // 告訴 JPA 這個類別對應一張資料庫表格
@Table(name = "employees")           // 指定表格名稱為 employees（省略則預設用類別名）
@Data
public class Employee {

    @Id                              // 標記這個欄位是主鍵（Primary Key）
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主鍵由 MySQL 自動遞增（AUTO_INCREMENT）
    private Long id;

    @Column(nullable = false)        // 此欄位不允許 null，等同 SQL 的 NOT NULL
    private String name;

    @Column(nullable = false, unique = true) // 不允許 null，且值必須唯一（等同 UNIQUE KEY）
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)  // 多對一：多個 Employee 對應一個 Department
    @JoinColumn(name = "dept_id")        // 資料庫中的外鍵欄位名稱
//    @JsonIgnoreProperties("employees")
    private Department department;

    private Double salary;
    
    @CreationTimestamp                 // Hibernate 在第一次 save() 時自動填入當下時間
    @Column(updatable = false)         // 設定為不可修改（時間一旦設定就不變）
    private LocalDateTime createdAt;

    // ★ 必須有無參數建構子：JPA 透過反射（Reflection）建立物件時需要它
    public Employee() {}

	public Employee(String name, String email, Department department, Double salary) {
		
		this.name = name;
		this.email = email;
		this.department = department;
		this.salary = salary;
	}
   
    
    // Getter / Setter（JPA 透過這些方法存取欄位值）
//    public Long getId() { return id; }
//    public void setId(Long id) { this.id = id; }
//    public String getName() { return name; }
//    public void setName(String name) { this.name = name; }
//    public String getEmail() { return email; }
//    public void setEmail(String email) { this.email = email; }
//    public String getDepartment() { return department; }
//    public void setDepartment(String department) { this.department = department; }
//    public Double getSalary() { return salary; }
//    public void setSalary(Double salary) { this.salary = salary; }
}
