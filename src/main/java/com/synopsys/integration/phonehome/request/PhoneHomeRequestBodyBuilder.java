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
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static com.synopsys.integration.phonehome.request.PhoneHomeRequestBody.MAX_META_DATA_CHARACTERS;
import static com.synopsys.integration.phonehome.request.PhoneHomeRequestBody.UNKNOWN_FIELD_VALUE;

public class PhoneHomeRequestBodyBuilder {
    private final String customerId;
    private final String hostName;
    private final NameVersion artifactInfo;
    private final UniquePhoneHomeProduct product;
    private final String productVersion;
    private final List<String> artifactModules = new ArrayList<>();
    private final Map<String, String> metaData = new HashMap<>();

    public static PhoneHomeRequestBodyBuilder createForBlackDuck(String integrationRepoName, String registrationId, String blackDuckServerUrl, String integrationVersion, String blackDuckVersion) {
        return createForProduct(UniquePhoneHomeProduct.BLACK_DUCK, integrationRepoName, registrationId, blackDuckServerUrl, integrationVersion, blackDuckVersion);
    }

    public static PhoneHomeRequestBodyBuilder createForCoverity(String integrationRepoName, String customerName, String cimServerUrl, String integrationVersion, String cimVersion) {
        return createForProduct(UniquePhoneHomeProduct.COVERITY, integrationRepoName, customerName, cimServerUrl, integrationVersion, cimVersion);
    }

    public static PhoneHomeRequestBodyBuilder createForPolaris(String integrationRepoName, String registrationId, String polarisServerUrl, String integrationVersion, String polarisVersion) {
        return createForProduct(UniquePhoneHomeProduct.POLARIS, integrationRepoName, registrationId, polarisServerUrl, integrationVersion, polarisVersion);
    }

    public static PhoneHomeRequestBodyBuilder createForProduct(UniquePhoneHomeProduct product, String artifactId, String customerId, String hostName, String artifactVersion, String productVersion) {
        artifactVersion = StringUtils.defaultIfEmpty(artifactVersion, UNKNOWN_FIELD_VALUE);
        productVersion = StringUtils.defaultIfEmpty(productVersion, UNKNOWN_FIELD_VALUE);

        NameVersion artifactInfo = new NameVersion(artifactId, artifactVersion);
        return new PhoneHomeRequestBodyBuilder(customerId, hostName, artifactInfo, product, productVersion);
    }

    private static String getVersionWithDefault(String version) {
        return StringUtils.defaultIfEmpty(version, UNKNOWN_FIELD_VALUE);
    }

    public PhoneHomeRequestBodyBuilder(String customerId, String hostName, NameVersion artifactInfo, UniquePhoneHomeProduct product, String productVersion) {
        if (null == product || null == artifactInfo || StringUtils.isAnyBlank(customerId, hostName, artifactInfo.getName(), artifactInfo.getVersion(), product.getName(), productVersion)) {
            throw new IllegalArgumentException("The fields: customerId, hostName, artifactInfo, and product (with a non-blank name), and productVersion are all required.");
        }

        this.customerId = customerId;
        this.hostName = hostName;
        this.artifactInfo = artifactInfo;
        this.product = product;
        this.productVersion = productVersion;
    }

    public PhoneHomeRequestBody build() {
        return new PhoneHomeRequestBody(this);
    }

    /**
     * metaData map cannot exceed {@value PhoneHomeRequestBody#MAX_META_DATA_CHARACTERS}
     *
     * @return true if the data was successfully added, false if the new data would make the map exceed it's size limit
     */
    public boolean addToMetaData(String key, String value) {
        if (charactersInMetaDataMap(key, value) < MAX_META_DATA_CHARACTERS) {
            metaData.put(key, value);
            return true;
        }
        return false;
    }

    /**
     * metaData map cannot exceed {@value PhoneHomeRequestBody#MAX_META_DATA_CHARACTERS}
     *
     * @return true if the all the data was successfully added,
     * false if one or more of the entries entries would make the map exceed it's size limit
     */
    public boolean addAllToMetaData(Map<String, String> metadataMap) {
        return metadataMap.entrySet().stream()
                .allMatch(entry -> addToMetaData(entry.getKey(), entry.getValue()));
    }

    public void addArtifactModule(String artifactModule) {
        artifactModules.add(artifactModule);
    }

    public void addArtifactModules(String... artifactModules) {
        this.artifactModules.addAll(Arrays.asList(artifactModules));
    }

    private int charactersInMetaDataMap(String key, String value) {
        final int mapEntryWrappingCharacters = 6;
        String mapAsString = getMetaData().toString();
        return mapEntryWrappingCharacters + mapAsString.length() + key.length() + value.length();
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getHostName() {
        return hostName;
    }

    public NameVersion getArtifactInfo() {
        return artifactInfo;
    }

    public UniquePhoneHomeProduct getProduct() {
        return product;
    }

    public String getProductVersion() {
        return productVersion;
    }

    public List<String> getArtifactModules() {
        return artifactModules;
    }

    public Map<String, String> getMetaData() {
        return metaData;
    }

}
