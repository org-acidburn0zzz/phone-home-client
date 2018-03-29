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

public class PhoneHomeClientTest {

    @Test
    public void test() throws IOException, IntegrationException {
        final IntLogger intLogger = new PrintStreamIntLogger(System.out, LogLevel.DEBUG);
        final ProxyInfo proxyInfo = new ProxyInfo("", 0, null, "", null, null);
        final PhoneHomeClient phClient = new PhoneHomeClient(intLogger, 120, proxyInfo, true);
        final Request request = phClient.createGoogleAnalyticsRequest("UA-116285836-2", "example_reg_key", "exampleHostName", "Hub", "4_5_0", "SonarQube", "6_7_1", "example_plugin_version", "example_source");
        final RestConnection restConnection = new UnauthenticatedRestConnection(intLogger, new URL("https://www.google-analytics.com/collect"), 120, proxyInfo);

        for (int i = 0; i < 10; i++) {
            request.getBodyContent().getBodyContentMap().put("uid", "example_reg_key_" + i);
            @SuppressWarnings("resource")
            final Response response = restConnection.executeRequest(request);
            intLogger.info("Response Code: " + response.getStatusCode());
            intLogger.info("Response String: " + response.getContentString());
            response.close();
        }
    }

}
