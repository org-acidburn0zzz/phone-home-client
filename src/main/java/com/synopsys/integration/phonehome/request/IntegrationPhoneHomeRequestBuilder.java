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

import java.util.List;
import java.util.Map;

public abstract class IntegrationPhoneHomeRequestBuilder<SUPER_CLASS extends IntegrationPhoneHomeRequestBuilder> {
    protected final PhoneHomeRequestBody.Builder actualBuilder;

    public IntegrationPhoneHomeRequestBuilder(final PhoneHomeRequestBody.Builder actualBuilder) {
        this.actualBuilder = actualBuilder;
    }

    public abstract SUPER_CLASS getThis();

    public PhoneHomeRequestBody build() throws IllegalStateException {
        return actualBuilder.build();
    }

    public String getIntegrationRepoName() {
        return actualBuilder.getArtifactId();
    }

    public SUPER_CLASS setIntegrationRepoName(final String artifactId) {
        actualBuilder.setArtifactId(artifactId);
        return getThis();
    }

    public String getIntegrationVersion() {
        return actualBuilder.getArtifactVersion();
    }

    public SUPER_CLASS setIntegrationVersion(final String artifactVersion) {
        actualBuilder.setArtifactVersion(artifactVersion);
        return getThis();
    }

    public List<String> getIntegrationModules() {
        return actualBuilder.getArtifactModules();
    }

    public SUPER_CLASS setIntegrationModules(final String... artifactModules) {
        actualBuilder.setArtifactModules(artifactModules);
        return getThis();
    }

    public SUPER_CLASS setIntegrationModules(final List<String> artifactModules) {
        actualBuilder.setArtifactModules(artifactModules);
        return getThis();
    }

    public Map<String, String> getMetaData() {
        return actualBuilder.getMetaData();
    }

    /**
     * metaData map cannot exceed {@value PhoneHomeRequestBody#MAX_META_DATA_CHARACTERS}
     * @return true if the data was successfully added, false if the new data would make the map exceed it's size limit
     */
    public boolean addToMetaData(final String key, final String value) {
        return actualBuilder.addToMetaData(key, value);
    }

    /**
     * metaData map cannot exceed {@value PhoneHomeRequestBody#MAX_META_DATA_CHARACTERS}
     * @return true if the all the data was successfully added,
     * false if one or more of the entries entries would make the map exceed it's size limit
     */
    public boolean addAllToMetaData(final Map<String, String> metadataMap) {
        return actualBuilder.addAllToMetaData(metadataMap);
    }

}
