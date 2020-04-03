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

import com.synopsys.integration.phonehome.enums.ProductIdEnum;

public class PolarisPhoneHomeRequestBuilder extends IntegrationPhoneHomeRequestBuilder<PolarisPhoneHomeRequestBuilder> {
    public PolarisPhoneHomeRequestBuilder(final PhoneHomeRequestBody.Builder actualBuilder) {
        super(actualBuilder);
        this.actualBuilder.setProductId(ProductIdEnum.POLARIS);
    }

    @Override
    public PolarisPhoneHomeRequestBuilder getThis() {
        return this;
    }

    public String getRegistrationId() {
        return actualBuilder.getCustomerId();
    }

    public PolarisPhoneHomeRequestBuilder setRegistrationId(final String registrationId) {
        actualBuilder.setCustomerId(registrationId);
        return this;
    }

    public String getPolarisServerUrl() {
        return actualBuilder.getHostName();
    }

    public PolarisPhoneHomeRequestBuilder setPolarisServerUrl(final String hostName) {
        actualBuilder.setHostName(hostName);
        return this;
    }

    public String getPolarisVersion() {
        return actualBuilder.getProductVersion();
    }

    public PolarisPhoneHomeRequestBuilder setPolarisVersion(final String productVersion) {
        actualBuilder.setProductVersion(productVersion);
        return this;
    }

}
