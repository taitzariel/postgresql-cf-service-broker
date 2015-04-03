/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mendix.servicebroker.postgresql.config;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudfoundry.community.servicebroker.config.BrokerApiVersionConfig;
import org.cloudfoundry.community.servicebroker.model.Catalog;
import org.cloudfoundry.community.servicebroker.model.Plan;
import org.cloudfoundry.community.servicebroker.model.ServiceDefinition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Configuration
@ComponentScan(basePackages = "com.mendix.servicebroker", excludeFilters = { @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = BrokerApiVersionConfig.class) })
public class BrokerConfiguration {


    @Value("${MASTER_JDBC_URL}")
    private String jdbcUrl;

    @Bean
    public Connection jdbc() {
        try {
			return DriverManager.getConnection(this.jdbcUrl);
		} catch (SQLException e) {
			return null;
		}
    }


    @Bean
    public Catalog catalog() throws JsonParseException, JsonMappingException, IOException {
        ServiceDefinition serviceDefinition = new ServiceDefinition("pg", "postgresql",
                "Postgresql on shared instance.", true, getPlans(), getTags(), getServiceDefinitionMetadata(),
                null, null);
        return new Catalog(Arrays.asList(serviceDefinition));
    }

    private List<String> getTags() {
        return Arrays.asList("s3", "object-storage");
    }

    private Map<String, Object> getServiceDefinitionMetadata() {
        Map<String, Object> sdMetadata = new HashMap<String, Object>();
        sdMetadata.put("displayName", "Postgresql");
        sdMetadata.put("imageUrl", "http://a1.awsstatic.com/images/logos/aws_logo.png");
        sdMetadata.put("longDescription", "Postgresql Service");
        sdMetadata.put("providerDisplayName", "Amazon");
        sdMetadata.put("documentationUrl", "http://mendix.com/postgresql");
        sdMetadata.put("supportUrl", "http://aws.amazon.com/s3");
        return sdMetadata;
    }

    private List<Plan> getPlans() {
        Plan basic = new Plan("pg-basic-plan", "Basic PG Plan",
                "A PG plan providing a single database on a shared instance with limited storage.", getBasicPlanMetadata());
        return Arrays.asList(basic);
    }

    private Map<String, Object> getBasicPlanMetadata() {
        Map<String, Object> planMetadata = new HashMap<String, Object>();
        planMetadata.put("bullets", getBasicPlanBullets());
        return planMetadata;
    }

    private List<String> getBasicPlanBullets() {
        return Arrays.asList("Single PG database", "Limited storage", "Shared instance");
    }
}
