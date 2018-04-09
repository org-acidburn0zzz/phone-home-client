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

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;

import com.blackducksoftware.integration.builder.AbstractBuilder;
import com.blackducksoftware.integration.phonehome.enums.ProductIdEnum;

public class PhoneHomeRequestBodyBuilder extends AbstractBuilder<PhoneHomeRequestBody> {
    public static final String EMPTY_FIELD_STRING = "null";

    private String customerId;
    private String hostName;
    private String artifactId;
    private String artifactVersion;
    private ProductIdEnum productId;
    private String productVersion;
    private final Map<String, String> metaData = new HashMap<>();

    @Override
    public PhoneHomeRequestBody buildObject() {
        return new PhoneHomeRequestBody(this);
    }

    @Override
    public PhoneHomeRequestBodyValidator createValidator() {
        final PhoneHomeRequestBodyValidator phoneHomeRequestValidator = new PhoneHomeRequestBodyValidator();
        phoneHomeRequestValidator.setCustomerId(customerId);
        phoneHomeRequestValidator.setHostName(hostName);
        phoneHomeRequestValidator.setArtifactId(artifactId);
        phoneHomeRequestValidator.setArtifactVersion(artifactVersion);
        phoneHomeRequestValidator.setProductId(productId);
        phoneHomeRequestValidator.setProductVersion(productVersion);
        return phoneHomeRequestValidator;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(final String customerId) {
        this.customerId = customerId != null ? customerId : EMPTY_FIELD_STRING;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(final String hostName) {
        this.hostName = hostName != null ? hostName : EMPTY_FIELD_STRING;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(final String artifactId) {
        this.artifactId = artifactId != null ? artifactId : EMPTY_FIELD_STRING;
    }

    public String getArtifactVersion() {
        return artifactVersion;
    }

    public void setArtifactVersion(final String artifactVersion) {
        this.artifactVersion = artifactVersion != null ? artifactVersion : EMPTY_FIELD_STRING;
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
        this.productVersion = productVersion != null ? productVersion : EMPTY_FIELD_STRING;
    }

    public Map<String, String> getMetaData() {
        return metaData;
    }

    public void addToMetaData(final String key, final String value) {
        metaData.put(key, value != null ? value : EMPTY_FIELD_STRING);
    }

    public String md5Hash(final String string) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        final MessageDigest md = MessageDigest.getInstance(MessageDigestAlgorithms.MD5);
        final byte[] hashedBytes = md.digest(string.getBytes("UTF-8"));
        return DigestUtils.md5Hex(hashedBytes);
    }

}
