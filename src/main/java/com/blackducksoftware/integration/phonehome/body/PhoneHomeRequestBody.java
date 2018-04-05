/**
 * phone-home-client
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.integration.phonehome.body;

import java.util.Collections;
import java.util.Map;

public class PhoneHomeRequestBody {
    public static final PhoneHomeRequestBody DO_NOT_PHONE_HOME = null;

    private final String uniqueId;
    private final String artifactId;
    private final String artifactVersion;
    private final String productId;
    private final String productVersion;
    private final Map<String, String> metaData;

    protected PhoneHomeRequestBody(final PhoneHomeRequestBodyBuilder builder) {
        this.uniqueId = builder.generateUniqueId();
        this.artifactId = builder.getArtifactId();
        this.artifactVersion = builder.getArtifactVersion();
        this.productId = builder.getProductId();
        this.productVersion = builder.getProductVersion();
        this.metaData = Collections.unmodifiableMap(builder.getMetaData());
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getArtifactVersion() {
        return artifactVersion;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductVersion() {
        return productVersion;
    }

    public Map<String, String> getMetaData() {
        return metaData;
    }

}
