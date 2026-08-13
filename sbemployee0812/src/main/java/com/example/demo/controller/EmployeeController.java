package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Employee;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.service.EmployeeService;

import java.util.*;
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
	@Autowired
	EmployeeRepository repo;
	@Autowired
	EmployeeService service;
	@GetMapping("/department/{department}")
	public ResponseEntity<List<Employee>> findByDepartment(@PathVariable("department")String department){
		  List<Employee> data= repo.findByDepartment(department);
		  if(data!=null && data.size()>0) {
			  return ResponseEntity.ok(data);
		  }else {
			  return ResponseEntity.notFound().build();
		  }
		
	}
	@GetMapping("/name/{name}")
	public ResponseEntity<List<Employee>> findByNameContain(@PathVariable("name")String name){
		  List<Employee> data= repo.findByNameContaining(name);
		  if(data!=null && data.size()>0) {
			  return ResponseEntity.ok(data);
		  }else {
			  return ResponseEntity.notFound().build();
		  }
		
	}
	@GetMapping("/count/{department}")
	public ResponseEntity<Map<String,Long>> countByDepartment(@PathVariable("department")String department){
          long count=repo.countByDepartment(department);
          Map<String,Long> data=new HashMap<>();
          data.put(department, count);
		  if(data!=null && data.size()>0) {
			  return ResponseEntity.ok(data);
		  }else {
			  return ResponseEntity.notFound().build();
		  }
		
	}
	
	@GetMapping("/ignore/{department}")
	public ResponseEntity<List<Employee>> ignoreCaseDepartment(@PathVariable("department")String department){
		  List<Employee> data= repo.findByDepartmentIgnoreCase(department);
		  if(data!=null && data.size()>0) {
			  return ResponseEntity.ok(data);
		  }else {
			  return ResponseEntity.notFound().build();
		  }
		
	}
	
	@GetMapping("/average/{department}")
	public ResponseEntity<Map<String,Double>> averageByDepartment(@PathVariable("department")String department){
          Double avg=repo.averageSalaryByDepartment(department);
          Map<String,Double> data=new HashMap<>();
          data.put(department, avg);
		  if(data!=null && data.size()>0) {
			  return ResponseEntity.ok(data);
		  }else {
			  return ResponseEntity.notFound().build();
		  }
		
	}
	@GetMapping("/page")
    public List<Employee> getPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
		List<Employee> data=service.findPaged(page, size, sortBy).getContent();
        //return service.findPaged(page, size, sortBy);
		return data;
    }
    
}
