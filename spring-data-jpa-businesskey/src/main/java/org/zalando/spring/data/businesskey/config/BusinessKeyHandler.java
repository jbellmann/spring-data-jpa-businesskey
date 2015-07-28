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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.zalando.spring.data.businesskey.BusinessKeyGenerator;

/**
 * @author  Joerg Bellmann
 */
public class BusinessKeyHandler implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessKeyHandler.class);

    private final BusinessKeyBeanWrapperFactory factory = new BusinessKeyBeanWrapperFactory();

    private BusinessKeyGenerator keyGenerator;

    public void setKeyGenerator(final BusinessKeyGenerator keygenerator) {
        Assert.notNull(keygenerator);
        this.keyGenerator = keygenerator;
    }

    public void afterPropertiesSet() throws Exception {
        if (keyGenerator == null) {
            LOGGER.warn("No KeyGenerator set! Keying will not be applied!");
        }
    }

    public void markCreated(final Object target) {
        touchCreated(target);
    }

    private void touchCreated(final Object target) {

        BusinessKeyBeanWrapper wrapper = factory.getBeanWrapperFor(target);
        if (wrapper != null) {
            Object key = keyGenerator.getBusinessKeyForSelector(wrapper.getBusinessKeySelector());
            wrapper.setBusinessKey(key);
        }
    }
}
