/**
 * phone-home-client
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
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
package com.synopsys.integration.phonehome;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.phonehome.enums.ProductIdEnum;

public class PhoneHomeRequestBody {
    public static final int MAX_META_DATA_CHARACTERS = 1536;
    public static final PhoneHomeRequestBody DO_NOT_PHONE_HOME = null;

    private final String customerId;
    private final String hostName;
    private final String artifactId;
    private final String artifactVersion;
    private final ProductIdEnum productId;
    private final String productVersion;
    private final Map<String, String> metaData;

    private PhoneHomeRequestBody(final PhoneHomeRequestBody.Builder builder) {
        customerId = builder.getCustomerId();
        hostName = builder.getHostName();
        artifactId = builder.getArtifactId();
        artifactVersion = builder.getArtifactVersion();
        productId = builder.getProductId();
        productVersion = builder.getProductVersion();
        metaData = Collections.unmodifiableMap(builder.getMetaData());
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getHostName() {
        return hostName;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getArtifactVersion() {
        return artifactVersion;
    }

    public ProductIdEnum getProductId() {
        return productId;
    }

    public String getProductVersion() {
        return productVersion;
    }

    public Map<String, String> getMetaData() {
        return metaData;
    }

    public static class Builder {
        public static final String UNKNOWN_ID = "<unknown>";

        private String customerId;
        private String hostName;
        private String artifactId;
        private String artifactVersion;
        private ProductIdEnum productId;
        private String productVersion;
        private final Map<String, String> metaData = new HashMap<>();

        // PhoneHomeRequestBody only has a private constructor to force creation through the builder.
        public PhoneHomeRequestBody build() throws IllegalStateException {
            validateRequiredParam(customerId, "customerId");
            validateRequiredParam(hostName, "hostName");
            validateRequiredParam(artifactId, "artifactId");
            validateRequiredParam(artifactVersion, "artifactVersion");
            if (productId == null) {
                throw new IllegalStateException("Required parameter 'productId' is not set");
            }
            validateRequiredParam(productVersion, "productVersion");
            return new PhoneHomeRequestBody(this);
        }

        public String getCustomerId() {
            return customerId;
        }

        public void setCustomerId(final String customerId) {
            this.customerId = customerId;
        }

        public String getHostName() {
            return hostName;
        }

        public void setHostName(final String hostName) {
            this.hostName = hostName;
        }

        public String getArtifactId() {
            return artifactId;
        }

        public void setArtifactId(final String artifactId) {
            this.artifactId = artifactId;
        }

        public String getArtifactVersion() {
            return artifactVersion;
        }

        public void setArtifactVersion(final String artifactVersion) {
            this.artifactVersion = artifactVersion;
        }

        public ProductIdEnum getProductId() {
            return productId;
        }

        public void setProductId(final ProductIdEnum productId) {
            this.productId = productId;
        }

        public String getProductVersion() {
            return productVersion;
        }

        public void setProductVersion(final String productVersion) {
            this.productVersion = productVersion;
        }

        public Map<String, String> getMetaData() {
            return Collections.unmodifiableMap(new HashMap<>(metaData));
        }

        /**
         * metaData map cannot exceed {@value com.synopsys.integration.phonehome.PhoneHomeRequestBody#MAX_META_DATA_CHARACTERS}
         * @return true if the data was successfully added, false if the new data would make the map exceed it's size limit
         */
        public boolean addToMetaData(final String key, final String value) {
            if (charactersInMetaDataMap(key, value) < MAX_META_DATA_CHARACTERS) {
                metaData.put(key, value);
                return true;
            }
            return false;
        }

        /**
         * metaData map cannot exceed {@value com.synopsys.integration.phonehome.PhoneHomeRequestBody#MAX_META_DATA_CHARACTERS}
         * @return true if the all the data was successfully added,
         * false if one or more of the entries entries would make the map exceed it's size limit
         */
        public boolean addAllToMetaData(final Map<String, String> metadataMap) {
            return metadataMap.entrySet().stream()
                           .allMatch(entry -> addToMetaData(entry.getKey(), entry.getValue()));
        }

        public String md5Hash(final String string) throws NoSuchAlgorithmException, UnsupportedEncodingException {
            final MessageDigest md = MessageDigest.getInstance(MessageDigestAlgorithms.MD5);
            final byte[] hashedBytes = md.digest(string.getBytes("UTF-8"));
            return DigestUtils.md5Hex(hashedBytes);
        }

        private void validateRequiredParam(final String param, final String paramName) throws IllegalStateException {
            if (StringUtils.isBlank(param)) {
                throw new IllegalStateException(String.format("Required parameter '%s' is not set", paramName));
            }
        }

        private int charactersInMetaDataMap(final String key, final String value) {
            final int mapEntryWrappingCharacters = 6;
            final String mapAsString = getMetaData().toString();
            return mapEntryWrappingCharacters + mapAsString.length() + key.length() + value.length();
        }

    }

}
