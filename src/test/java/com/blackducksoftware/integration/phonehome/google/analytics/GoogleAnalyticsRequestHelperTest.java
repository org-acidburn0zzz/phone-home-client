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
package com.blackducksoftware.integration.phonehome.google.analytics;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.proxy.ProxyInfo;
import com.blackducksoftware.integration.hub.request.Request;
import com.blackducksoftware.integration.hub.request.Response;
import com.blackducksoftware.integration.hub.rest.RestConnection;
import com.blackducksoftware.integration.hub.rest.UnauthenticatedRestConnectionBuilder;
import com.blackducksoftware.integration.log.IntLogger;
import com.blackducksoftware.integration.log.LogLevel;
import com.blackducksoftware.integration.log.PrintStreamIntLogger;
import com.blackducksoftware.integration.phonehome.body.PhoneHomeRequestBodyBuilder;
import com.blackducksoftware.integration.phonehome.enums.ProductIdEnum;

public class GoogleAnalyticsRequestHelperTest {

    @Test
    public void basicRequestTest() throws IOException, IntegrationException {
        final IntLogger intLogger = new PrintStreamIntLogger(System.out, LogLevel.DEBUG);
        final ProxyInfo proxyInfo = new ProxyInfo("", 0, null, "", null, null);
        final String debugUrl = GoogleAnalyticsConstants.BASE_URL + GoogleAnalyticsConstants.DEBUG_ENDPOINT;

        final UnauthenticatedRestConnectionBuilder builder = new UnauthenticatedRestConnectionBuilder();
        builder.setLogger(intLogger);
        builder.setBaseUrl(debugUrl);
        builder.setTimeout(120);
        final RestConnection restConnection = builder.createConnection(proxyInfo);

        final PhoneHomeRequestBodyBuilder bodyBuilder = new PhoneHomeRequestBodyBuilder();
        bodyBuilder.setCustomerId("fake_customer_id");
        bodyBuilder.setHostName("fake_host_name");
        bodyBuilder.setArtifactId("fake_artifact_id");
        bodyBuilder.setArtifactVersion("fake_artifact_version");
        bodyBuilder.setProductId(ProductIdEnum.CODE_CENTER);
        bodyBuilder.setProductVersion("fake_product_version");

        bodyBuilder.addToMetaData("exampleMetaData_1", "data");
        bodyBuilder.addToMetaData("exampleMetaData_2", "otherData");
        bodyBuilder.addToMetaData("exampleMetaData_3", "moreData");

        final GoogleAnalyticsRequestHelper helper = new GoogleAnalyticsRequestHelper(GoogleAnalyticsConstants.TEST_INTEGRATIONS_TRACKING_ID, bodyBuilder.build());

        final Request request = helper.createRequest(debugUrl);
        intLogger.info("Request Body: " + request.getBodyContent().getBodyContentMap());

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
