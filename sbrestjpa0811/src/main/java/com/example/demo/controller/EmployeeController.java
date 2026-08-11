package com.example.demo.controller;

import com.example.demo.model.Employee;
import com.example.demo.service.EmployeeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController               // = @Controller + @ResponseBody，回傳值自動序列化為 JSON
@RequestMapping("/api/employees") // 所有方法的 URL 前綴都是 /api/employees
public class EmployeeController {
	
	@Autowired
	EmployeeService employeeService;
	
	 @GetMapping
	 public List<Employee> getAll() {
	        return employeeService.findAll();
	 }
	 
	 // ──────────────────────────────────────────────
	 // GET /api/employees/{id} → 查詢單筆員工
	 // ──────────────────────────────────────────────
	 @GetMapping("/{id}")
	 public ResponseEntity<Employee> getById(@PathVariable Long id) {
	        // Optional.map() → 有資料回傳 200 OK
	        // orElse()       → 沒資料回傳 404 Not Found
	        return employeeService.findById(id)
	                .map(ResponseEntity::ok)
	                .orElse(ResponseEntity.notFound().build());
	 }
	  @PostMapping
	    public ResponseEntity<Employee> create(@RequestBody Employee employee) {
	        Employee saved = employeeService.create(employee);
	        // 201 Created + Location header 指向新資源的 URL
	        URI location = URI.create("/api/employees/" + saved.getId());
	        return ResponseEntity.created(location).body(saved);
	    }
	  @PutMapping("/{id}")
	    public ResponseEntity<Employee> update(
	            @PathVariable Long id,
	            @RequestBody Employee updatedEmployee) {
	        return employeeService.update(id, updatedEmployee)
	                .map(ResponseEntity::ok)           // 更新成功 → 200 OK + 最新資料
	                .orElse(ResponseEntity.notFound().build()); // 找不到 → 404
	    }
	  @DeleteMapping("/{id}")
	    public ResponseEntity<Void> delete(@PathVariable Long id) {
	        if (employeeService.delete(id)) {
	            return ResponseEntity.noContent().build(); // 刪除成功 → 204 No Content
	        }
	        return ResponseEntity.notFound().build();      // 找不到 → 404
	    }
}
