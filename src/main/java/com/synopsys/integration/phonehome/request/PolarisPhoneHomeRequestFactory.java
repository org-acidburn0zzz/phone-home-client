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

import java.util.Optional;
import java.util.function.Supplier;

public class PolarisPhoneHomeRequestFactory extends PhoneHomeRequestFactory {
    public PolarisPhoneHomeRequestFactory(String integrationRepoName) {
        super(integrationRepoName, UniquePhoneHomeProduct.POLARIS);
    }

    @Override
    public PhoneHomeRequestBodyBuilder create(String registrationId, String polarisServerUrl, Supplier<Optional<String>> integrationVersionSupplier, Supplier<Optional<String>> polarisVersionSupplier) {
        return createBase(registrationId, polarisServerUrl, integrationVersionSupplier, polarisVersionSupplier);
    }

    @Override
    public PhoneHomeRequestBodyBuilder create(String registrationId, String polarisServerUrl, String integrationVersion, String polarisVersion) {
        return createBase(registrationId, polarisServerUrl, integrationVersion, polarisVersion);
    }

}
