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
package org.zalando.stups.jpa.example.entities;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;

import org.springframework.data.jpa.domain.AbstractPersistable;
import org.zalando.spring.data.businesskey.annotation.BusinessKey;
import org.zalando.spring.data.businesskey.jpa.BusinessKeyEntityListener;

@Entity
@EntityListeners({ BusinessKeyEntityListener.class })
public class Product extends AbstractPersistable<Long> {

    private static final long serialVersionUID = 1L;

    @BusinessKey("test-selector")
    private String businessKey;

    private String name;

    public Product() {
        //
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(final String businessKey) {
        this.businessKey = businessKey;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

}
