package com.example.demo;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link Sbemployee0812Application}.
 */
@Generated
public class Sbemployee0812Application__BeanDefinitions {
  /**
   * Get the bean definition for 'sbemployee0812Application'.
   */
  public static BeanDefinition getSbemployeeApplicationBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(Sbemployee0812Application.class);
    beanDefinition.setInstanceSupplier(Sbemployee0812Application::new);
    return beanDefinition;
  }
}
