package com.example.demo.service;



import com.example.demo.model.Employee;
import com.example.demo.repository.EmployeeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    // ── Day 1 原有方法（findAll、findById、create、update、delete）保持不變 ──

    // === Day 2 新增查詢方法 ===

    // 依部門名稱查詢
    public List<Employee> findByDepartment(String department) {
        return employeeRepository.findByDepartment(department);
    }

    // 姓名關鍵字搜尋
    public List<Employee> searchByName(String keyword) {
        return employeeRepository.findByNameContaining(keyword);
    }

    // 薪資區間查詢
    public List<Employee> findBySalaryRange(Double min, Double max) {
        return employeeRepository.findBySalaryBetween(min, max);
    }

    // 某部門的平均薪資
    public Double getAverageSalary(String department) {
        return employeeRepository.averageSalaryByDepartment(department);
    }

    // 最近 N 天加入的員工
    public List<Employee> getRecentEmployees(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return employeeRepository.findRecentEmployees(since);
    }

    // 分頁查詢（page 從 0 開始，size 為每頁筆數，sortBy 為排序欄位）
    public Page<Employee> findPaged(int page, int size, String sortBy) {
        return employeeRepository.findAll(
            PageRequest.of(page, size, Sort.by(sortBy).ascending())
        );
    }
   // 因為原程式為提供 Service update create 方法故新增
   public Optional<Employee> update(Long id, Employee updated) {
    	    Optional<Employee> existingOpt = employeeRepository.findById(id);
		if (existingOpt.isPresent()) {
			Employee existing = existingOpt.get();
			existing.setName(updated.getName());
			existing.setEmail(updated.getEmail());
			existing.setDepartment(updated.getDepartment());
			existing.setSalary(updated.getSalary());
			return Optional.of(employeeRepository.save(existing));
		} else {
			return Optional.empty(); // 或者拋出例外
		}
    }
    public Employee create(Employee emp) {
    	    Employee e1=new Employee(emp.getName(), emp.getEmail(), emp.getDepartment(), emp.getSalary());
		return employeeRepository.save(e1);
	}
}
