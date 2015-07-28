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
package org.zalando.spring.data.businesskey.jpa;

import javax.persistence.Entity;
import javax.persistence.PrePersist;

import org.springframework.beans.factory.annotation.Configurable;
import org.zalando.spring.data.businesskey.config.BusinessKeyHandler;

/**
 * EntityListener that injects a newly generated BusinessKey into marked {@link Entity}.
 *
 * @param   <T>
 *
 * @author  Joerg Bellmann
 */
@Configurable
public class BusinessKeyEntityListener {

    private BusinessKeyHandler keyHandler;

    public BusinessKeyEntityListener() {
        //
    }

    public void setKeyHandler(final BusinessKeyHandler keyHandler) {
        this.keyHandler = keyHandler;
    }

    @PrePersist
    public void touchForCreate(final Object target) {
        if (keyHandler != null) {
            keyHandler.markCreated(target);
        }
    }

}
