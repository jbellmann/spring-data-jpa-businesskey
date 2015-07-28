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
package org.zalando.spring.data.businesskey.jpa.config;

import static org.springframework.data.jpa.domain.support.AuditingBeanFactoryPostProcessor.BEAN_CONFIGURER_ASPECT_BEAN_NAME;

import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.target.LazyInitTargetSource;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.aspectj.AnnotationBeanConfigurerAspect;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;

import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;

import org.springframework.core.type.AnnotationMetadata;

import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import org.zalando.spring.data.businesskey.config.BusinessKeyHandler;

/**
 * I was confused by the Regstrar for Auditing.
 *
 * @author  jbellmann
 */
public class JpaBusinessKeyRegistrar implements ImportBeanDefinitionRegistrar {

    static final String KEY_ENTITY_LISTENER_CLASS_NAME =
        "org.zalando.spring.data.businesskey.jpa.BusinessKeyEntityListener";
    private static final String KEY_BFPP_CLASS_NAME =
        "org.zalando.spring.data.businesskey.config.BusinessKeyBeanFactoryPostProcessor";

    private static final String BEAN_CONFIGURER_ASPECT_CLASS_NAME =
        "org.springframework.beans.factory.aspectj.AnnotationBeanConfigurerAspect";

    @Override
    public void registerBeanDefinitions(final AnnotationMetadata annotationMetadata,
            final BeanDefinitionRegistry beanDefinitionRegistry) {

        Assert.notNull(annotationMetadata, "AnnotationMetadata must not be null!");
        Assert.notNull(beanDefinitionRegistry, "BeanDefinitionRegistry must not be null!");

        registerBeanConfigurerAspectIfNecessary(beanDefinitionRegistry);

        BeanDefinition keyHanderBeanDefinition = getKeyHandlerBeanDefinition(beanDefinitionRegistry);

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(KEY_ENTITY_LISTENER_CLASS_NAME);

        // Achtung member-access, member muss genauso heissen
        builder.addPropertyValue("keyHandler", keyHanderBeanDefinition);
        builder.setScope("prototype");

        registerInfrastructureBeanWithId(builder.getRawBeanDefinition(), KEY_ENTITY_LISTENER_CLASS_NAME,
            beanDefinitionRegistry);

        RootBeanDefinition def = new RootBeanDefinition(KEY_BFPP_CLASS_NAME);

        registerInfrastructureBeanWithId(def, KEY_BFPP_CLASS_NAME, beanDefinitionRegistry);
    }

    protected BeanDefinition getKeyHandlerBeanDefinition(final BeanDefinitionRegistry registry) {

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(BusinessKeyHandler.class);

// String keyGeneratorRef = "simpleBusinessKeyGenerator";
        String keyGeneratorRef = "";
        if (StringUtils.hasText(keyGeneratorRef)) {
            builder.addPropertyValue("keyGenerator", createLazyInitTargetSourceBeanDefinition(keyGeneratorRef));
        } else {
            builder.setAutowireMode(AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE);
        }

        builder.setRole(AbstractBeanDefinition.ROLE_INFRASTRUCTURE);

        AbstractBeanDefinition abd = builder.getBeanDefinition();

        registry.registerBeanDefinition("keyHandler", abd);

        return abd;
    }

    private BeanDefinition createLazyInitTargetSourceBeanDefinition(final String keyGeneratorRef) {

        BeanDefinitionBuilder targetSourceBuilder = BeanDefinitionBuilder.rootBeanDefinition(
                LazyInitTargetSource.class);
        targetSourceBuilder.addPropertyValue("targetBeanName", keyGeneratorRef);

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(ProxyFactoryBean.class);
        builder.addPropertyValue("targetSource", targetSourceBuilder.getBeanDefinition());

        return builder.getBeanDefinition();
    }

    /**
     * Registers the given {@link AbstractBeanDefinition} as infrastructure bean under the given id.
     *
     * @param  definition  must not be {@literal null}.
     * @param  id          must not be {@literal null} or empty.
     * @param  registry    must not be {@literal null}.
     */
    protected void registerInfrastructureBeanWithId(final AbstractBeanDefinition definition, final String id,
            final BeanDefinitionRegistry registry) {

        definition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        registry.registerBeanDefinition(id, definition);
    }

    /**
     * @param  registry,  the {@link BeanDefinitionRegistry} to be used to register the
     *                    {@link AnnotationBeanConfigurerAspect}.
     */
    private void registerBeanConfigurerAspectIfNecessary(final BeanDefinitionRegistry registry) {

        if (registry.containsBeanDefinition(BEAN_CONFIGURER_ASPECT_CLASS_NAME)) {
            return;
        }

        if (!ClassUtils.isPresent(BEAN_CONFIGURER_ASPECT_CLASS_NAME, getClass().getClassLoader())) {
            throw new BeanDefinitionStoreException(BEAN_CONFIGURER_ASPECT_CLASS_NAME + " not found. \n"
                    + "Could not configure Spring Data JPA auditing-feature because"
                    + " spring-aspects.jar is not on the classpath!\n"
                    + "If you want to use auditing please add spring-aspects.jar to the classpath.");
        }

        RootBeanDefinition def = new RootBeanDefinition();
        def.setBeanClassName(BEAN_CONFIGURER_ASPECT_CLASS_NAME);
        def.setFactoryMethodName("aspectOf");
        def.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);

        registry.registerBeanDefinition(BEAN_CONFIGURER_ASPECT_BEAN_NAME,
            new BeanComponentDefinition(def, BEAN_CONFIGURER_ASPECT_BEAN_NAME).getBeanDefinition());
    }
}
