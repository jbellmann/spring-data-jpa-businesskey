/**
 * Copyright (C) 2015 Zalando SE (http://tech.zalando.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zalando.spring.data.businesskey.config;

import static org.springframework.data.jpa.util.BeanDefinitionUtils.getBeanDefinition;
import static org.springframework.data.jpa.util.BeanDefinitionUtils.getEntityManagerFactoryBeanNames;

import static org.springframework.util.StringUtils.addStringToArray;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * @author  jbellmann
 */
public class BusinessKeyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    public static final String BEAN_CONFIGURER_ASPECT_BEAN_NAME =
        "org.springframework.context.config.internalBeanConfigurerAspect";

    public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory) throws BeansException {

        try {
            getBeanDefinition(BEAN_CONFIGURER_ASPECT_BEAN_NAME, beanFactory);
        } catch (NoSuchBeanDefinitionException o_O) {
            throw new IllegalStateException(
                "Invalid auditing setup! Make sure you've used @EnableJpaAuditing or <jpa:auditing /> correctly!", o_O);
        }

        for (String beanName : getEntityManagerFactoryBeanNames(beanFactory)) {
            BeanDefinition definition = getBeanDefinition(beanName, beanFactory);
            definition.setDependsOn(addStringToArray(definition.getDependsOn(), BEAN_CONFIGURER_ASPECT_BEAN_NAME));
        }

// for (String beanName : beanNamesForTypeIncludingAncestors(beanFactory, BusinessKeyGenerator.class, true, false)) {
// BeanDefinition definition = beanFactory.getBeanDefinition(beanName);
// definition.setLazyInit(true);
// }
    }

}
