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
import com.synopsys.integration.util.NameVersion;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.function.Supplier;

import static com.synopsys.integration.phonehome.request.PhoneHomeRequestBody.UNKNOWN_FIELD_VALUE;

public abstract class PhoneHomeRequestFactory {
    protected String artifactId;
    protected ProductIdEnum productId;

    public static Supplier<Optional<String>> useKnownVersion(String version) {
        return () -> Optional.of(version);
    }

    public PhoneHomeRequestFactory(String artifactId, ProductIdEnum productId) {
        this.artifactId = artifactId;
        this.productId = productId;
    }

    public abstract PhoneHomeRequestBodyBuilder create(String customerId, String hostName, Supplier<Optional<String>> artifactVersionSupplier, Supplier<Optional<String>> productVersionSupplier);

    public abstract PhoneHomeRequestBodyBuilder create(String customerId, String hostName, String artifactVersion, String productVersion);

    protected PhoneHomeRequestBodyBuilder createBase(String customerId, String hostName, Supplier<Optional<String>> artifactVersionSupplier, Supplier<Optional<String>> productVersionSupplier) {
        NameVersion artifactInfo = new NameVersion(artifactId, getVersionWithDefault(artifactVersionSupplier));
        NameVersion productInfo = new NameVersion(productId.name(), getVersionWithDefault(productVersionSupplier));

        return new PhoneHomeRequestBodyBuilder(customerId, hostName, artifactInfo, productInfo);
    }

    protected PhoneHomeRequestBodyBuilder createBase(String customerId, String hostName, String artifactVersion, String productVersion) {
        NameVersion artifactInfo = new NameVersion(artifactId, getVersionWithDefault(artifactVersion));
        NameVersion productInfo = new NameVersion(productId.name(), getVersionWithDefault(productVersion));

        return new PhoneHomeRequestBodyBuilder(customerId, hostName, artifactInfo, productInfo);
    }

    public String getVersionWithDefault(Supplier<Optional<String>> version) {
        return version.get().orElse(UNKNOWN_FIELD_VALUE);
    }

    public String getVersionWithDefault(String version) {
        return StringUtils.defaultIfEmpty(version, UNKNOWN_FIELD_VALUE);
    }

}
