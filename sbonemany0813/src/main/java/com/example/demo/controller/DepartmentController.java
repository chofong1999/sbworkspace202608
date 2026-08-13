package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.Department;
import com.example.demo.service.DepartmentService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    // GET /api/departments（含員工清單）
    @GetMapping
    public List<Department> getAll() {
        return departmentService.findAll();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Department> getById(@PathVariable Integer id) {
        return departmentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/name/{name}")
    public ResponseEntity<Department> getByName(@PathVariable String name) {
        return departmentService.findByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PutMapping("/{id}")
    public ResponseEntity<Department> updateById(@PathVariable Integer id,@RequestBody Department d) {
    	Department updated=departmentService.update(id, d);
    	if(updated !=null) {
    		return ResponseEntity.ok(updated);
    	}else {
    		return ResponseEntity.notFound().build();
    	}
    }
}
