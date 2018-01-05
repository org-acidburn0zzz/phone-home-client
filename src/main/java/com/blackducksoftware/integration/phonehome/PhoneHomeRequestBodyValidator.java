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
package com.blackducksoftware.integration.phonehome;

import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.integration.phonehome.enums.PhoneHomeRequestFieldEnum;
import com.blackducksoftware.integration.validator.AbstractValidator;
import com.blackducksoftware.integration.validator.ValidationResult;
import com.blackducksoftware.integration.validator.ValidationResultEnum;
import com.blackducksoftware.integration.validator.ValidationResults;

public class PhoneHomeRequestBodyValidator extends AbstractValidator {
    private String registrationId;
    private String hostName;
    private String blackDuckName;
    private String blackDuckVersion;
    private String thirdPartyName;
    private String thirdPartyVersion;
    private String pluginVersion;
    private String source;

    @Override
    public ValidationResults assertValid() {
        final ValidationResults result = new ValidationResults();
        validateHubServerIdentifier(result);
        validateBlackDuckProductInfo(result);
        validateThirdPartyProductInfo(result);
        validateIntegrationInfo(result);
        validateSource(result);
        return result;
    }

    public void validateHubServerIdentifier(final ValidationResults result) {
        if (StringUtils.isBlank(registrationId) && StringUtils.isBlank(hostName)) {
            result.addResult(PhoneHomeRequestFieldEnum.REGID, new ValidationResult(ValidationResultEnum.ERROR, "No Hub server identifier was found."));
        }
    }

    public void validateBlackDuckProductInfo(final ValidationResults result) {
        if (blackDuckName == null || StringUtils.isBlank(blackDuckName)) {
            result.addResult(PhoneHomeRequestFieldEnum.BLACKDUCKNAME, new ValidationResult(ValidationResultEnum.ERROR, "No Black Duck product name was found."));
        } else if (StringUtils.isBlank(blackDuckVersion)) {
            result.addResult(PhoneHomeRequestFieldEnum.BLACKDUCKVERSION, new ValidationResult(ValidationResultEnum.ERROR, String.format("No version of %s was found.", blackDuckName)));
        }
    }

    public void validateThirdPartyProductInfo(final ValidationResults result) {
        if (thirdPartyName == null || StringUtils.isBlank(thirdPartyName)) {
            result.addResult(PhoneHomeRequestFieldEnum.THIRDPARTYNAME, new ValidationResult(ValidationResultEnum.ERROR, "No third party name was found."));
        } else if (StringUtils.isBlank(thirdPartyVersion)) {
            result.addResult(PhoneHomeRequestFieldEnum.THIRDPARTYVERSION, new ValidationResult(ValidationResultEnum.ERROR, String.format("No version of %s was found.", thirdPartyName)));
        }
    }

    public void validateIntegrationInfo(final ValidationResults result) {
        if (StringUtils.isBlank(pluginVersion)) {
            result.addResult(PhoneHomeRequestFieldEnum.PLUGINVERSION, new ValidationResult(ValidationResultEnum.ERROR, "No plugin version was found."));
        }
    }

    public void validateSource(final ValidationResults result) {
        if (source == null || StringUtils.isBlank(source)) {
            result.addResult(PhoneHomeRequestFieldEnum.PLUGINVERSION, new ValidationResult(ValidationResultEnum.ERROR, "No source was found."));
        }
    }

    public void setRegistrationId(final String registrationId) {
        this.registrationId = registrationId;
    }

    public void setHostName(final String hostName) {
        this.hostName = hostName;
    }

    public void setBlackDuckName(final String blackDuckName) {
        this.blackDuckName = blackDuckName;
    }

    public void setBlackDuckVersion(final String blackDuckVersion) {
        this.blackDuckVersion = blackDuckVersion;
    }

    public void setThirdPartyName(final String thirdPartyName) {
        this.thirdPartyName = thirdPartyName;
    }

    public void setThirdPartyVersion(final String thirdPartyVersion) {
        this.thirdPartyVersion = thirdPartyVersion;
    }

    public void setPluginVersion(final String pluginVersion) {
        this.pluginVersion = pluginVersion;
    }

    public void setSource(final String source) {
        this.source = source;
    }

}
