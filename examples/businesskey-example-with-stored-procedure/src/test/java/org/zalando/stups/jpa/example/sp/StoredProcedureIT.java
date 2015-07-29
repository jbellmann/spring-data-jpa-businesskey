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
package org.zalando.stups.jpa.example.sp;

import java.io.IOException;

import javax.sql.DataSource;

import org.assertj.core.api.Assertions;

import org.flywaydb.core.Flyway;

import org.junit.Ignore;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.opentable.db.postgres.embedded.EmbeddedPostgreSQL;

import com.zaxxer.hikari.HikariDataSource;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class StoredProcedureIT {

    @Autowired
    private DataSource dataSource;

    @Test
    public void runStoredProcedure() {

        Flyway fl = new Flyway();
        fl.setDataSource(dataSource);
        fl.setSchemas("example_data");

        int result = fl.migrate();

        // we only have one migration
        Assertions.assertThat(result).isEqualTo(1);

        ExampleSP sp = new ExampleSP(dataSource);
        String number = sp.nextNumber(NumberRangeType.EXAMPLE_ORDER);

        Assertions.assertThat(number).isNotNull();

    }

    @Configuration
    static class TestConfiguration {

        @Bean
        DataSource dataSource() throws IOException {

            HikariDataSource dataSource = new HikariDataSource();
            dataSource.setCatalog("example_data");
            dataSource.setConnectionInitSql("SET search_path to example_data, public;");
            dataSource.setDataSource(embeddedPostgres().getPostgresDatabase());

            return dataSource;
        }

        @Bean
        EmbeddedPostgreSQL embeddedPostgres() throws IOException {
            return EmbeddedPostgreSQL.start();
        }
    }
}
