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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.data.util.ReflectionUtils;
import org.springframework.data.util.ReflectionUtils.AnnotationFieldFilter;
import org.springframework.util.Assert;
import org.zalando.spring.data.businesskey.annotation.BusinessKey;

/**
 * @author  jbellmann
 */
final class AnnotationBusinessKeyMetadata {

    private static final AnnotationFieldFilter BUSINESSKEY_FILTER = new AnnotationFieldFilter(BusinessKey.class);

    private static final Map<Class<?>, AnnotationBusinessKeyMetadata> METADATACACHE =
        new ConcurrentHashMap<Class<?>, AnnotationBusinessKeyMetadata>();

    private final Field businessKeyField;

    private AnnotationBusinessKeyMetadata(final Class<?> type) {

        Assert.notNull(type, "Given Type must not be null");
        businessKeyField = ReflectionUtils.findField(type, BUSINESSKEY_FILTER);
    }

    public static AnnotationBusinessKeyMetadata getMetadata(final Class<?> type) {

        if (METADATACACHE.containsKey(type)) {
            return METADATACACHE.get(type);
        }

        AnnotationBusinessKeyMetadata metadata = new AnnotationBusinessKeyMetadata(type);
        METADATACACHE.put(type, metadata);
        return metadata;
    }

    public boolean isKeyable() {
        if (businessKeyField == null) {
            return false;
        }

        return true;
    }

    public Field getKeyField() {
        return businessKeyField;
    }

}
