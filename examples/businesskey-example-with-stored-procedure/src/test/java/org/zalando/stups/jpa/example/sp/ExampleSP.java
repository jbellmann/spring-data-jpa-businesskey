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

import java.sql.Types;

import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;

import com.google.common.collect.Maps;

/**
 * @author  jbellmann
 */
public class ExampleSP extends StoredProcedure {

    private static final Logger LOG = LoggerFactory.getLogger(ExampleSP.class);

    static final String STORED_PROCEDURE_NAME = "get_next_number";
    static final String TYPE_NAME = "number_range_type";
    static final String IN_PARAMETER_NAME = "p_type";
    static final String OUT_PARAMETER_NAME = "number";

    public ExampleSP(final DataSource dataSource) {
        LOG.info("inside NextNumberStoredProcedure datasource: {}", dataSource);

        setDataSource(dataSource);
        setFunction(true);
        setSql(STORED_PROCEDURE_NAME);
        declareParameter(new SqlParameter(IN_PARAMETER_NAME, Types.OTHER));
        declareParameter(new SqlOutParameter(OUT_PARAMETER_NAME, Types.VARCHAR));
        compile();
    }

    public String nextNumber(final NumberRangeType numberRangeType) {

        LOG.info("inside nextNumber: {}", numberRangeType);

        final Map<String, Object> input = Maps.newHashMap();
        input.put(IN_PARAMETER_NAME, numberRangeType);

        final Map<String, Object> resultMap = execute(input);
        return (String) resultMap.get(OUT_PARAMETER_NAME);
    }

}
