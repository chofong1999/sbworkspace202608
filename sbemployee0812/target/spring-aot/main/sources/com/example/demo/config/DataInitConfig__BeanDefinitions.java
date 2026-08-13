package com.example.demo.config;

import com.example.demo.repository.EmployeeRepository;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link DataInitConfig}.
 */
@Generated
public class DataInitConfig__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'dataInitConfig'.
   */
  private static BeanInstanceSupplier<DataInitConfig> getDataInitConfigInstanceSupplier() {
    return BeanInstanceSupplier.<DataInitConfig>forConstructor(EmployeeRepository.class)
            .withGenerator((registeredBean, args) -> new DataInitConfig(args.get(0)));
  }

  /**
   * Get the bean definition for 'dataInitConfig'.
   */
  public static BeanDefinition getDataInitConfigBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(DataInitConfig.class);
    beanDefinition.setInstanceSupplier(getDataInitConfigInstanceSupplier());
    return beanDefinition;
  }
}
