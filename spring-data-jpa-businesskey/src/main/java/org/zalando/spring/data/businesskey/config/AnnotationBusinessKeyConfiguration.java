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

import java.lang.annotation.Annotation;

import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import org.springframework.util.Assert;

/**
 * @author  jbellmann
 */
public class AnnotationBusinessKeyConfiguration implements BusinessKeyConfiguration {

    private final AnnotationAttributes attributes;

    public AnnotationBusinessKeyConfiguration(final AnnotationMetadata metadata,
            final Class<? extends Annotation> annotation) {

        Assert.notNull(metadata, "AnnotationMetadata must not be null!");
        Assert.notNull(annotation, "Annotation must not be null!");

        this.attributes = new AnnotationAttributes(metadata.getAnnotationAttributes(annotation.getName()));
    }

    @Override
    public String getBusinessKeyAwareRef() {
        return attributes.getString("businessKeyAwareRef");
    }
}
