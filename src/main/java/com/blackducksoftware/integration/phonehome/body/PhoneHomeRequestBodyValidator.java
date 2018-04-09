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

import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.integration.phonehome.enums.PhoneHomeRequestFieldEnum;
import com.blackducksoftware.integration.phonehome.enums.ProductIdEnum;
import com.blackducksoftware.integration.validator.AbstractValidator;
import com.blackducksoftware.integration.validator.ValidationResult;
import com.blackducksoftware.integration.validator.ValidationResultEnum;
import com.blackducksoftware.integration.validator.ValidationResults;

public class PhoneHomeRequestBodyValidator extends AbstractValidator {
    private String customerId;
    private String hostName;
    private String artifactId;
    private String artifactVersion;
    private ProductIdEnum productId;
    private String productVersion;

    @Override
    public ValidationResults assertValid() {
        final ValidationResults result = new ValidationResults();
        validateCustomerInfo(result);
        validateArtifactInfo(result);
        validateProductInfo(result);
        return result;
    }

    public void validateCustomerInfo(final ValidationResults results) {
        if (StringUtils.isBlank(hostName)) {
            results.addResult(PhoneHomeRequestFieldEnum.HOST_NAME, new ValidationResult(ValidationResultEnum.ERROR, "No host name was found."));
        }
        if (StringUtils.isBlank(customerId)) {
            results.addResult(PhoneHomeRequestFieldEnum.CUSTOMER_ID, new ValidationResult(ValidationResultEnum.ERROR, "No customer id was found."));
        }
    }

    public void validateArtifactInfo(final ValidationResults results) {
        if (StringUtils.isBlank(artifactId)) {
            results.addResult(PhoneHomeRequestFieldEnum.ARTIFACT_ID, new ValidationResult(ValidationResultEnum.ERROR, "No artifact id was found."));
        } else if (StringUtils.isBlank(artifactVersion)) {
            results.addResult(PhoneHomeRequestFieldEnum.ARTIFACT_VERSION, new ValidationResult(ValidationResultEnum.ERROR, String.format("No version of %s was found.", artifactId)));
        }
    }

    public void validateProductInfo(final ValidationResults results) {
        if (productId == null) {
            results.addResult(PhoneHomeRequestFieldEnum.PRODUCT_ID, new ValidationResult(ValidationResultEnum.ERROR, "No product id was found."));
        } else if (StringUtils.isBlank(productVersion)) {
            results.addResult(PhoneHomeRequestFieldEnum.PRODUCT_VERSION, new ValidationResult(ValidationResultEnum.ERROR, String.format("No version of %s was found.", productId)));
        }
    }

    public void setCustomerId(final String customerId) {
        this.customerId = customerId;
    }

    public void setHostName(final String hostName) {
        this.hostName = hostName;
    }

    public void setArtifactId(final String artifactId) {
        this.artifactId = artifactId;
    }

    public void setArtifactVersion(final String artifactVersion) {
        this.artifactVersion = artifactVersion;
    }

    public void setProductId(final ProductIdEnum productId) {
        this.productId = productId;
    }

    public void setProductVersion(final String productVersion) {
        this.productVersion = productVersion;
    }

}
