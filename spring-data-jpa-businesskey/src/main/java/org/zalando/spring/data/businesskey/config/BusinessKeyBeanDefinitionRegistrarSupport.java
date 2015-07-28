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

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.rootBeanDefinition;

import java.lang.annotation.Annotation;

import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.target.LazyInitTargetSource;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @author  jbellmann
 */
public abstract class BusinessKeyBeanDefinitionRegistrarSupport implements ImportBeanDefinitionRegistrar {

// private static final String BUSINESSKEY_AWARE = "businessKeyAware";
    private static final String KEYGENERATOR = "keyGenerator";

    @Override
    public void registerBeanDefinitions(final AnnotationMetadata annotationMetadata,
            final BeanDefinitionRegistry beanDefinitionRegistry) {

        Assert.notNull(annotationMetadata, "AnnotationMetadata must not be null");
        Assert.notNull(beanDefinitionRegistry, "BeanDefinitionRegistry must not be null");

        AbstractBeanDefinition khbd = getKeyHandlerBeanDefinitionBuilder(beanDefinitionRegistry,
                getConfiguration(annotationMetadata));

        registerBusinessKeyListenerBeanDefinition(khbd, beanDefinitionRegistry);

    }

    protected abstract void registerBusinessKeyListenerBeanDefinition(final AbstractBeanDefinition khbd,
            final BeanDefinitionRegistry beanDefinitionRegistry);

    protected AbstractBeanDefinition getKeyHandlerBeanDefinitionBuilder(
            final BeanDefinitionRegistry beanDefinitionRegistry, final BusinessKeyConfiguration configuration) {

        Assert.notNull(beanDefinitionRegistry, "BeanDefinitionRegistry must not be null");
        Assert.notNull(configuration, "BusinessKeyConfiguration must not be null");

        AbstractBeanDefinition khbd = getBusinessKeyBeanDefinitionBuilder(configuration).getBeanDefinition();

        beanDefinitionRegistry.registerBeanDefinition(getBusinessKeyHandlerBeanName(), khbd);

        return khbd;
    }

    protected BeanDefinitionBuilder getBusinessKeyBeanDefinitionBuilder(final BusinessKeyConfiguration configuration) {

        Assert.notNull(configuration, "BusinessKeyConfiguration must not be null");

        return configureDefaultBusinessKeyHandlerAttributes(configuration,
                BeanDefinitionBuilder.rootBeanDefinition(BusinessKeyHandler.class));
    }

    private BeanDefinitionBuilder configureDefaultBusinessKeyHandlerAttributes(
            final BusinessKeyConfiguration configuration, final BeanDefinitionBuilder builder) {

        if (StringUtils.hasText(configuration.getBusinessKeyAwareRef())) {
            builder.addPropertyValue(KEYGENERATOR,
                createLazyInitTargetSourceBeanDefinition(configuration.getBusinessKeyAwareRef()));
        } else {
            builder.setAutowireMode(AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE);
        }

        builder.setRole(AbstractBeanDefinition.ROLE_INFRASTRUCTURE);

        return builder;
    }

    private BeanDefinition createLazyInitTargetSourceBeanDefinition(final String businessKeyAwareRef) {

        BeanDefinitionBuilder targetSourceBuilder = rootBeanDefinition(LazyInitTargetSource.class);
        targetSourceBuilder.addPropertyValue("targetBeanName", businessKeyAwareRef);

        BeanDefinitionBuilder builder = rootBeanDefinition(ProxyFactoryBean.class);
        builder.addPropertyValue("targetSource", targetSourceBuilder.getBeanDefinition());

        return builder.getBeanDefinition();
    }

    private BusinessKeyConfiguration getConfiguration(final AnnotationMetadata annotationMetadata) {
        return new AnnotationBusinessKeyConfiguration(annotationMetadata, getAnnotation());
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
     * Return the annotation type to lookup configuration values from.
     *
     * @return  must not be {@literal null}.
     */
    protected abstract Class<? extends Annotation> getAnnotation();

    /**
     * @return
     */
    protected abstract String getBusinessKeyHandlerBeanName();

}
