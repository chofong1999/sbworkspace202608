package com.example.demo.config;

import com.example.demo.model.Employee;
import com.example.demo.repository.EmployeeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitConfig implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;

    public DataInitConfig(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (employeeRepository.count() == 0) {
            employeeRepository.save(new Employee("王小明", "wang@example.com", "Engineering", 80000.0));
            employeeRepository.save(new Employee("李小華", "lee@example.com", "Marketing", 65000.0));
            employeeRepository.save(new Employee("張大偉", "chang@example.com", "Engineering", 92000.0));
            employeeRepository.save(new Employee("陳美玲", "chen@example.com", "HR", 58000.0));
            employeeRepository.save(new Employee("林志豪", "lin@example.com", "Finance", 75000.0));
            System.out.println("=== 已自動建立 5 筆 Employee 資料 ===");
        }
    }
}

