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

import java.io.IOException;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.opentable.db.postgres.embedded.EmbeddedPostgreSQL;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceConfiguration {

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
