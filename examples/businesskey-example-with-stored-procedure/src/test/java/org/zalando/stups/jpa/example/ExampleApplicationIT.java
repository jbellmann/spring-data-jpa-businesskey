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
package org.zalando.stups.jpa.example;

import javax.transaction.Transactional;

import org.assertj.core.api.Assertions;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;

import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.zalando.stups.jpa.example.entities.Product;
import org.zalando.stups.jpa.example.entities.ProductRespository;

// @Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {ExampleApplication.class})
@WebIntegrationTest(randomPort = true, value = {"debug=false"})
@Transactional
public class ExampleApplicationIT {

    @Autowired
    private ProductRespository productRepository;

    @Test
    public void testStartUp() {
        Product product = new Product();
        product = productRepository.save(product);

        Product fromDb = productRepository.findOne(product.getId());

        // make sure businessKey was set
        Assertions.assertThat(fromDb.getBusinessKey()).isNotNull();
        Assertions.assertThat(fromDb.getBusinessKey()).startsWith("EO");
    }
}
