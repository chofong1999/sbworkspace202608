package com.example.demo.service;

import com.example.demo.model.Department;
import com.example.demo.repository.DepartmentRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    // 查詢所有部門（含員工，已透過 JOIN FETCH 避免 N+1）
    public List<Department> findAll() {
        return departmentRepository.findAllWithEmployees();
    }

    public Optional<Department> findById(Integer id) {
        return departmentRepository.findById(id);
    }
    
    public Optional<Department> findByName(String n) {
        return departmentRepository.findByName(n);
    }

    public Department create(Department department) {
        return departmentRepository.save(department);
    }

    public boolean delete(Integer id) {
        if (departmentRepository.existsById(id)) {
            departmentRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public Department update(int id,Department updated) {
    	Optional<Department> found=departmentRepository.findById(id);
    	if(found.isPresent()) {
    		Department dt=found.get();
    		dt.setName(updated.getName());
    		departmentRepository.save(dt);
    		return dt;
    	}
        return null;
    }
}
