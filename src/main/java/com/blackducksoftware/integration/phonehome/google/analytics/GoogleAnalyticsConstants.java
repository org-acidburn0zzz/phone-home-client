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
package com.blackducksoftware.integration.phonehome.google.analytics;

public class GoogleAnalyticsConstants {
    // Google Tracking ID
    public static final String PRODUCTION_INTEGRATIONS_TRACKING_ID = "UA-116682967-1";
    public static final String TEST_INTEGRATIONS_TRACKING_ID = "UA-116682967-2";

    // Api Path(s)
    public static final String BASE_URL = "https://www.google-analytics.com";
    public static final String COLLECT_ENDPOINT = "/collect";
    public static final String DEBUG_ENDPOINT = "/debug" + COLLECT_ENDPOINT;

    // Payload Data - Required
    public static final String API_VERSION_KEY = "v";
    public static final String HIT_TYPE_KEY = "t";
    public static final String CLIENT_ID_KEY = "cid";
    public static final String USER_ID_KEY = "uid";
    public static final String TRACKING_ID_KEY = "tid";
    public static final String DOCUMENT_PATH_KEY = "dp";

    // Payload Data - Custom Dimensions
    public static final String UNIQUE_ID = "cd1";
    public static final String ARTIFACT_ID = "cd2";
    public static final String ARTIFACT_VERSION = "cd3";
    public static final String PRODUCT_ID = "cd4";
    public static final String PRODUCT_VERSION = "cd5";
    public static final String META_DATA = "cd6";

}
