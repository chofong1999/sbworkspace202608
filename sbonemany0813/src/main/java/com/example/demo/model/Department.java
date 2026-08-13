package com.example.demo.model;



import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "departments")
@Data
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    // 一對多關聯
    // mappedBy = "department"：指向 Employee.java 中 @ManyToOne 欄位的「屬性名稱」
    // cascade = PERSIST：儲存部門時，連同員工一起儲存（Day 1 的 create 仍可用）
    // fetch = LAZY：不自動載入員工清單，需要時才載入（避免效能問題）
    @OneToMany(mappedBy = "department", cascade = CascadeType.PERSIST,
               targetEntity=Employee.class ,fetch = FetchType.LAZY)
    @JsonIgnoreProperties("department")
    private List<Employee> employees = new ArrayList<>();

    public Department() {}
    public Department(String name) { this.name = name; }
	
    @Override
	public String toString() {
		return "Department [id=" + id + ", name=" + name + "]";
	}

//    public Long getId() { return id; }
//    public void setId(Long id) { this.id = id; }
//    public String getName() { return name; }
//    public void setName(String name) { this.name = name; }
//    public List<Employee> getEmployees() { return employees; }
//    public void setEmployees(List<Employee> employees) { this.employees = employees; }
    
}
