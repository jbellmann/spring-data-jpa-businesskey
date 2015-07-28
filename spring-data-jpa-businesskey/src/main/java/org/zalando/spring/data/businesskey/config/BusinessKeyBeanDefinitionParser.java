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

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;

import org.w3c.dom.Element;

public class BusinessKeyBeanDefinitionParser implements BeanDefinitionParser {

    static final String KEY_ENTITY_LISTENER_CLASS_NAME = "de.zalando.data.jpa.domain.support.BusinessKeyEntityListener";
    private static final String KEY_BFPP_CLASS_NAME =
        "de.zalando.data.jpa.domain.support.BusinessKeyBeanFactoryPostProcessor";

    private final BeanDefinitionParser keyHandlerParser = new BusinessKeyHandlerBeanDefinitionParser();

    public BeanDefinition parse(final Element element, final ParserContext parser) {

        new SpringConfiguredBeanDefinitionParser().parse(element, parser);

        BeanDefinition keyHandlerDefinition = keyHandlerParser.parse(element, parser);

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(KEY_ENTITY_LISTENER_CLASS_NAME);

        // Achtung member-access, member muss genauso heissen
        builder.addPropertyValue("keyHandler", keyHandlerDefinition);
        builder.setScope("prototype");

        registerInfrastructureBeanWithId(builder.getRawBeanDefinition(), KEY_ENTITY_LISTENER_CLASS_NAME, parser,
            element);

        RootBeanDefinition def = new RootBeanDefinition(KEY_BFPP_CLASS_NAME);
        registerInfrastructureBeanWithId(def, KEY_BFPP_CLASS_NAME, parser, element);
        return null;
    }

    private void registerInfrastructureBeanWithId(final AbstractBeanDefinition def, final String id,
            final ParserContext context, final Element element) {

        def.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        def.setSource(context.extractSource(element));
        context.registerBeanComponent(new BeanComponentDefinition(def, id));
    }

    /**
     * Copied code of SpringConfiguredBeanDefinitionParser until this class gets public.
     *
     * @author  Juergen Hoeller
     * @see     http://jira.springframework.org/browse/SPR-7340
     */
    private static class SpringConfiguredBeanDefinitionParser implements BeanDefinitionParser {

        /**
         * The bean name of the internally managed bean configurer aspect.
         */
        private static final String BEAN_CONFIGURER_ASPECT_BEAN_NAME =
            "org.springframework.context.config.internalBeanConfigurerAspect";

        private static final String BEAN_CONFIGURER_ASPECT_CLASS_NAME =
            "org.springframework.beans.factory.aspectj.AnnotationBeanConfigurerAspect";

        public BeanDefinition parse(final Element element, final ParserContext parserContext) {

            if (!parserContext.getRegistry().containsBeanDefinition(BEAN_CONFIGURER_ASPECT_BEAN_NAME)) {
                RootBeanDefinition def = new RootBeanDefinition();
                def.setBeanClassName(BEAN_CONFIGURER_ASPECT_CLASS_NAME);
                def.setFactoryMethodName("aspectOf");

                def.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
                def.setSource(parserContext.extractSource(element));
                parserContext.registerBeanComponent(new BeanComponentDefinition(def, BEAN_CONFIGURER_ASPECT_BEAN_NAME));
            }

            return null;
        }
    }
}
