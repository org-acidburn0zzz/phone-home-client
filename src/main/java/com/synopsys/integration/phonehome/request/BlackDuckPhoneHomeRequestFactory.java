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

import java.util.Optional;
import java.util.function.Supplier;

public class BlackDuckPhoneHomeRequestFactory extends PhoneHomeRequestFactory {
    public BlackDuckPhoneHomeRequestFactory(String integrationRepoName) {
        super(integrationRepoName, ProductIdEnum.BLACK_DUCK);
    }

    @Override
    public PhoneHomeRequestBodyBuilder create(String registrationId, String blackDuckServerUrl, Supplier<Optional<String>> integrationVersionSupplier, Supplier<Optional<String>> blackDuckVersionSupplier) {
        return createBase(registrationId, blackDuckServerUrl, integrationVersionSupplier, blackDuckVersionSupplier);
    }

    @Override
    public PhoneHomeRequestBodyBuilder create(String registrationId, String blackDuckServerUrl, String integrationVersion, String blackDuckVersion) {
        return createBase(registrationId, blackDuckServerUrl, integrationVersion, blackDuckVersion);
    }

}
