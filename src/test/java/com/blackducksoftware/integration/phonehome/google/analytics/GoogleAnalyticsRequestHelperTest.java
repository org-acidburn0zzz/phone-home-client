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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Before;
import org.junit.Test;

import com.blackducksoftware.integration.log.LogLevel;
import com.blackducksoftware.integration.log.PrintStreamIntLogger;
import com.blackducksoftware.integration.phonehome.PhoneHomeRequestBody;
import com.blackducksoftware.integration.phonehome.enums.ProductIdEnum;
import com.google.gson.Gson;

public class GoogleAnalyticsRequestHelperTest {
    private static final PrintStreamIntLogger logger = new PrintStreamIntLogger(System.out, LogLevel.TRACE);

    @Before
    public void init() {
        logger.info("\n");
        logger.info("Test Class: GoogleAnalyticsRequestHelperTest");
    }

    @Test
    public void basicRequestTest() throws IOException {
        final String debugUrl = GoogleAnalyticsConstants.BASE_URL + GoogleAnalyticsConstants.DEBUG_ENDPOINT;

        final PhoneHomeRequestBody.Builder bodyBuilder = new PhoneHomeRequestBody.Builder();
        bodyBuilder.setCustomerId("fake_customer_id");
        bodyBuilder.setHostName("fake_host_name");
        bodyBuilder.setArtifactId("fake_artifact_id");
        bodyBuilder.setArtifactVersion("fake_artifact_version");
        bodyBuilder.setProductId(ProductIdEnum.CODE_CENTER);
        bodyBuilder.setProductVersion("fake_product_version");

        bodyBuilder.addToMetaData("exampleMetaData_1", "data");
        bodyBuilder.addToMetaData("exampleMetaData_2", "other Data");
        bodyBuilder.addToMetaData("exampleMetaData_3", "special chars: !@#$%^&*()<>?,.;`~\\|{{}[]]-=_+");
        bodyBuilder.addToMetaData("example meta data 4", "string \" with \"quotes\" \"    \" ");

        final GoogleAnalyticsRequestHelper helper = new GoogleAnalyticsRequestHelper(new Gson(), GoogleAnalyticsConstants.TEST_INTEGRATIONS_TRACKING_ID, bodyBuilder.build());

        final HttpPost request = helper.createRequest(debugUrl);

        final HttpClient client = HttpClientBuilder.create().build();

        int responseCode = -1;
        final HttpResponse response = client.execute(request);
        responseCode = response.getStatusLine().getStatusCode();
        logger.info("Response Code: " + responseCode);

        String nextLine;
        final BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

        logger.info("Response String:");
        while ((nextLine = reader.readLine()) != null) {
            logger.info(nextLine);
        }

        assertEquals(200, responseCode);
    }

}
