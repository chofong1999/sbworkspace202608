package com.example.demo.controller;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link EmployeeController}.
 */
@Generated
public class EmployeeController__BeanDefinitions {
  /**
   * Get the bean definition for 'employeeController'.
   */
  public static BeanDefinition getEmployeeControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(EmployeeController.class);
    InstanceSupplier<EmployeeController> instanceSupplier = InstanceSupplier.using(EmployeeController::new);
    instanceSupplier = instanceSupplier.andThen(EmployeeController__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
