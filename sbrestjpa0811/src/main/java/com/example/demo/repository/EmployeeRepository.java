package com.example.demo.repository;

import com.example.demo.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository  // 標記為 Spring 元件，讓 Spring 管理它的生命週期
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    // 繼承 JpaRepository 後，以下方法全部自動可用，不需要額外撰寫：
    //
    // ✅ save(employee)      → INSERT（id 為 null）或 UPDATE（id 有值）
    // ✅ findById(id)        → SELECT WHERE id = ?，回傳 Optional<Employee>
    // ✅ findAll()           → SELECT * 查詢全部資料
    // ✅ deleteById(id)      → DELETE WHERE id = ?
    // ✅ existsById(id)      → 確認某個 id 是否存在，回傳 boolean
    // ✅ count()             → SELECT COUNT(*)，回傳總筆數
}
