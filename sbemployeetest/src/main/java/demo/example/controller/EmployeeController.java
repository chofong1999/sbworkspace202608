package demo.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
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

import demo.example.model.*;
import java.util.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/employees")
public class EmployeeController implements CommandLineRunner {
	@Autowired
	EmployeeRepository dao;

	@GetMapping
	public List<Employee> getAllEmployees() {
		return dao.findAll();
	}
	
	@GetMapping("/{id}")
	public Employee findEmployees(@PathVariable("id") Integer id) {
		return dao.findById(id).get();
	}

	@PostMapping
	public ResponseEntity<String> saveEmployee(@RequestBody Employee emp) {
		int v = dao.findAll().stream().max(Comparator.comparing(e -> e.getId())).get().getId() + 1;
		emp.setId(v);
		System.out.println("save:" + emp.toString());
		dao.save(emp);
		return ResponseEntity.ok("added");
	}

	@PutMapping("/{id}")
	public ResponseEntity<String> updateEmployee(@PathVariable("id") Integer id, @RequestBody Employee emp) {
		Employee e = dao.findAll().stream().filter(e1 -> e1.getId() == id).findAny().orElse(null);
		System.out.println("update:" + e.toString());
		e.setLastName(emp.getLastName());
		e.setFirstName(emp.getFirstName());
		e.setEmail(emp.getEmail());
		dao.save(e);
		return ResponseEntity.ok("updated");
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteEmployee(@PathVariable("id") Integer id) {
		Employee e = dao.findAll().stream().filter(e1 -> e1.getId() == id).findAny().orElse(null);
		System.out.println("delete:" + e);
		if (e != null ) {
			dao.delete(e);
			return ResponseEntity.ok("deleted");
		}else {
			return ResponseEntity.ok("delete failed");
		}
	}

	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		if (dao.count() == 0) {
			dao.save(new Employee(100, "Mary", "Wu", "mary@demo.com"));
			dao.save(new Employee(101, "Tony", "Lee", "tony@demo.com"));
			dao.save(new Employee(102, "Janet", "Chen", "janet@demo.com"));
		}
	}

}
