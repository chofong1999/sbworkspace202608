package com.example.demo.repository;

import com.example.demo.model.Employee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.lang.Double;
import java.lang.Long;
import java.lang.String;
import java.util.List;
import org.springframework.aot.generate.Generated;
import org.springframework.data.jpa.repository.aot.AotRepositoryFragmentSupport;
import org.springframework.data.jpa.repository.query.QueryEnhancerSelector;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;

/**
 * AOT generated JPA repository implementation for {@link EmployeeRepository}.
 */
@Generated
public class EmployeeRepositoryImpl__AotRepository extends AotRepositoryFragmentSupport {
  private final RepositoryFactoryBeanSupport.FragmentCreationContext context;

  private final EntityManager entityManager;

  public EmployeeRepositoryImpl__AotRepository(EntityManager entityManager,
      RepositoryFactoryBeanSupport.FragmentCreationContext context) {
    super(QueryEnhancerSelector.DEFAULT_SELECTOR, context);
    this.entityManager = entityManager;
    this.context = context;
  }

  /**
   * AOT generated implementation of {@link EmployeeRepository#countByDepartment(java.lang.String)}.
   */
  public long countByDepartment(String dept) {
    String queryString = "SELECT COUNT(e) FROM Employee e WHERE e.department = :dept";
    Query query = this.entityManager.createQuery(queryString);
    query.setParameter("dept", dept);

    return (Long) convertOne(query.getSingleResultOrNull(), false, Long.class);
  }

  /**
   * AOT generated implementation of {@link EmployeeRepository#findByDepartment(java.lang.String)}.
   */
  public List<Employee> findByDepartment(String department) {
    String queryString = "SELECT e FROM Employee e WHERE e.department = :department";
    Query query = this.entityManager.createQuery(queryString);
    query.setParameter("department", department);

    return (List<Employee>) query.getResultList();
  }

  /**
   * AOT generated implementation of {@link EmployeeRepository#findByNameContaining(java.lang.String)}.
   */
  public List<Employee> findByNameContaining(String keyword) {
    String queryString = "SELECT e FROM Employee e WHERE e.name LIKE :keyword ESCAPE '\\'";
    Query query = this.entityManager.createQuery(queryString);
    query.setParameter("keyword", "%%%s%%".formatted(keyword));

    return (List<Employee>) query.getResultList();
  }

  /**
   * AOT generated implementation of {@link EmployeeRepository#findBySalaryGreaterThan(java.lang.Double)}.
   */
  public List<Employee> findBySalaryGreaterThan(Double minSalary) {
    String queryString = "SELECT e FROM Employee e WHERE e.salary > :minSalary";
    Query query = this.entityManager.createQuery(queryString);
    query.setParameter("minSalary", minSalary);

    return (List<Employee>) query.getResultList();
  }
}
