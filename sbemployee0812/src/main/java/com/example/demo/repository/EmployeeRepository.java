package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.Employee;

import java.time.LocalDateTime;
import java.util.*;

public interface EmployeeRepository extends JpaRepository<Employee,Long> {
	// === Derived Query Methods（Spring 根據方法名稱自動產生 SQL）===
    // WHERE department = ?
    List<Employee> findByDepartment(String department);

    // WHERE name LIKE '%keyword%'（Containing 自動前後加 %）
    List<Employee> findByNameContaining(String keyword);
    
    // WHERE salary > ?
    List<Employee> findBySalaryGreaterThan(Double minSalary);
    
 // SELECT COUNT(*) WHERE department = ?（回傳員工人數）
    long countByDepartment(String dept);
    
    //@Query("SELECT e FROM Employee e WHERE LOWER(e.department) = LOWER(:dept)")
    @Query(value="SELECT * FROM employees WHERE department= :dept", nativeQuery=true)
    List<Employee> findByDepartmentIgnoreCase(@Param("dept") String dept);
    
    // WHERE salary BETWEEN ? AND ?
    List<Employee> findBySalaryBetween(Double min, Double max);
    
    // 計算部門平均薪資（聚合查詢）
    @Query("SELECT AVG(e.salary) FROM Employee e WHERE e.department = :dept")
    Double averageSalaryByDepartment(@Param("dept") String dept);

    // 查詢某日期之後加入的員工（需要 Employee 有 createdAt 欄位）
    @Query("SELECT e FROM Employee e WHERE e.createdAt >= :since ORDER BY e.createdAt DESC")
    List<Employee> findRecentEmployees(@Param("since") LocalDateTime since);


}
