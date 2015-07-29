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
package org.zalando.stups.jpa.example.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import org.zalando.spring.data.businesskey.BusinessKeyGenerator;

import org.zalando.stups.jpa.example.sp.ExampleSP;
import org.zalando.stups.jpa.example.sp.NumberRangeType;

@Configuration
public class BusinessKeyGeneratorConfig {

    @Autowired
    private DataSource dataSource;

    @Bean
    @Primary
    public BusinessKeyGenerator storedProcedureBusinessKeyGenerator() {
        return new BusinessKeyGenerator() {

            private final ExampleSP sp = new ExampleSP(dataSource);

            @Override
            public Object getBusinessKeyForSelector(final String businessKeyGeneratorSelector) {

                // we do not really care about businessKeyGeneratorSelector
                return sp.nextNumber(NumberRangeType.EXAMPLE_ORDER);
            }
        };
    }

}
