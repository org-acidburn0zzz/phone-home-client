/*
 * Copyright (C) 2018 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.phonehome.enums;

import com.blackducksoftware.integration.validator.FieldEnum;

public enum PhoneHomeRequestFieldEnum implements FieldEnum {
    UNIQUE_ID("uniqueId"),
    ARTIFACT_ID("artifactId"),
    ARTIFACT_VERSION("artifactVersion"),
    PRODUCT_ID("productId"),
    PRODUCT_VERSION("productVersion"),
    META_DATA("metaData");

    private String key;

    private PhoneHomeRequestFieldEnum(final String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return key;
    }

}
