/**
 * phone-home-client
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.phonehome.request;

import com.synopsys.integration.phonehome.UniquePhoneHomeProduct;
import com.synopsys.integration.util.NameVersion;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PhoneHomeRequestBody {
    public static final int MAX_META_DATA_CHARACTERS = 1536;
    public static final String UNKNOWN_FIELD_VALUE = "<unknown>";

    public static final PhoneHomeRequestBody DO_NOT_PHONE_HOME = null;

    private final String customerId;
    private final String hostName;
    private final NameVersion artifactInfo;
    private final UniquePhoneHomeProduct product;
    private final String productVersion;
    private final List<String> artifactModules;
    private final Map<String, String> metaData;

    public PhoneHomeRequestBody(String customerId, String hostName, NameVersion artifactInfo, UniquePhoneHomeProduct product, String productVersion, List<String> artifactModules, Map<String, String> metaData) {
        this.customerId = customerId;
        this.hostName = hostName;
        this.artifactInfo = artifactInfo;
        this.product = product;
        this.productVersion = productVersion;
        this.artifactModules = Collections.unmodifiableList(artifactModules);
        this.metaData = Collections.unmodifiableMap(metaData);
    }

    PhoneHomeRequestBody(PhoneHomeRequestBodyBuilder phoneHomeRequestBodyBuilder) {
        this.customerId = phoneHomeRequestBodyBuilder.getCustomerId();
        this.hostName = phoneHomeRequestBodyBuilder.getHostName();
        this.artifactInfo = phoneHomeRequestBodyBuilder.getArtifactInfo();
        this.product = phoneHomeRequestBodyBuilder.getProduct();
        this.productVersion = phoneHomeRequestBodyBuilder.getProductVersion();
        this.artifactModules = Collections.unmodifiableList(phoneHomeRequestBodyBuilder.getArtifactModules());
        this.metaData = Collections.unmodifiableMap(phoneHomeRequestBodyBuilder.getMetaData());
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getHostName() {
        return hostName;
    }

    public String getArtifactId() {
        return artifactInfo.getName();
    }

    public String getArtifactVersion() {
        return artifactInfo.getVersion();
    }

    public String getProductName() {
        return product.getName();
    }

    public String getProductVersion() {
        return productVersion;
    }

    public Map<String, String> getMetaData() {
        return metaData;
    }

    public List<String> getArtifactModules() {
        return artifactModules;
    }

}
