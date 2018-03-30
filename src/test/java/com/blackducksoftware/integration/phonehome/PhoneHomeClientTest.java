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
import java.util.UUID;

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
        final RestConnection restConnection = new UnauthenticatedRestConnection(intLogger, new URL("https://www.google-analytics.com/collect"), 120, proxyInfo);

        final Request request = phClient.createGoogleAnalyticsRequest("UA-116285836-2", "int_hub_xx", "external_host", "Hub", "4.5.0", "Bamboo", "6.0.1", "3.2.0", "INTEGRATIONS");
        request.getBodyContent().getBodyContentMap().put("cid", UUID.randomUUID().toString());
        @SuppressWarnings("resource")
        final Response response = restConnection.executeRequest(request);
        intLogger.info("Response Code: " + response.getStatusCode());
        intLogger.info("Response String: " + response.getContentString());
        response.close();
    }

}
