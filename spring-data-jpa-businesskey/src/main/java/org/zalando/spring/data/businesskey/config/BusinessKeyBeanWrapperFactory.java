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

import java.lang.reflect.Field;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.util.ReflectionUtils;
import org.zalando.spring.data.businesskey.annotation.BusinessKey;

/**
 * @author  jbellmann
 */
public class BusinessKeyBeanWrapperFactory {

    BusinessKeyBeanWrapper getBeanWrapperFor(final Object source) {

        if (source == null) {
            return null;
        }

        AnnotationBusinessKeyMetadata metadata = AnnotationBusinessKeyMetadata.getMetadata(source.getClass());

        if (metadata.isKeyable()) {
            return new ReflectionKeyableBeanWrapper(source);
        }

        return null;
    }

    static class ReflectionKeyableBeanWrapper implements BusinessKeyBeanWrapper {

        private final Object source;
        private final AnnotationBusinessKeyMetadata metadata;

        ReflectionKeyableBeanWrapper(final Object source) {
            this.source = source;
            this.metadata = AnnotationBusinessKeyMetadata.getMetadata(source.getClass());
        }

        public String getBusinessKeySelector() {
            BusinessKey keyAnnotation = this.metadata.getKeyField().getAnnotation(BusinessKey.class);
            return (String) AnnotationUtils.getValue(keyAnnotation, "value");
        }

        public void setBusinessKey(final Object key) {
            setField(metadata.getKeyField(), key);
        }

        private void setField(final Field keyField, final Object key) {
            if (keyField != null) {
                ReflectionUtils.setField(keyField, this.source, key);
            }
        }
    }

}
