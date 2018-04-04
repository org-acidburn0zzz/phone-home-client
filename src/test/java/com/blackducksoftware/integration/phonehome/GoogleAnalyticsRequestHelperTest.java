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
package com.blackducksoftware.integration.phonehome;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URL;

import org.junit.Test;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.proxy.ProxyInfo;
import com.blackducksoftware.integration.hub.request.Request;
import com.blackducksoftware.integration.hub.request.Response;
import com.blackducksoftware.integration.hub.rest.RestConnection;
import com.blackducksoftware.integration.hub.rest.UnauthenticatedRestConnection;
import com.blackducksoftware.integration.log.IntLogger;
import com.blackducksoftware.integration.log.LogLevel;
import com.blackducksoftware.integration.log.PrintStreamIntLogger;
import com.blackducksoftware.integration.phonehome.enums.BlackDuckName;
import com.blackducksoftware.integration.phonehome.enums.PhoneHomeSource;
import com.blackducksoftware.integration.phonehome.enums.ThirdPartyName;
import com.blackducksoftware.integration.phonehome.google.analytics.GoogleAnalyticsConstants;
import com.blackducksoftware.integration.phonehome.google.analytics.GoogleAnalyticsRequestHelper;

public class GoogleAnalyticsRequestHelperTest {

    @Test
    public void basicRequestTest() throws IOException, IntegrationException {
        final IntLogger intLogger = new PrintStreamIntLogger(System.out, LogLevel.DEBUG);
        final ProxyInfo proxyInfo = new ProxyInfo("", 0, null, "", null, null);
        final RestConnection restConnection = new UnauthenticatedRestConnection(intLogger, new URL(GoogleAnalyticsConstants.BASE_URL + GoogleAnalyticsConstants.DEBUG_ENDPOINT), 120, proxyInfo);

        final String debugUrl = GoogleAnalyticsConstants.BASE_URL + GoogleAnalyticsConstants.DEBUG_ENDPOINT;
        final GoogleAnalyticsRequestHelper helper = new GoogleAnalyticsRequestHelper(debugUrl, GoogleAnalyticsConstants.TEST_INTEGRATIONS_TRACKING_ID, "ph_client_test_reg_id", "ph_client_test_host_name", PhoneHomeSource.INTEGRATIONS,
                BlackDuckName.HUB, "ph_client_test_black_duck_version", ThirdPartyName.ALERT, "ph_client_test_third_party_version", "ph_client_test_plugin_version");

        final Request request = helper.createRequest();

        int responseCode = -1;
        String responseContent = "null";
        try (final Response response = restConnection.executeRequest(request)) {
            responseCode = response.getStatusCode();
            responseContent = response.getContentString();
        }

        intLogger.info("Response Code: " + responseCode);
        intLogger.info("Response String:\n" + responseContent);
        assertEquals(200, responseCode);
    }

}
