package com.synopsys.integration.phonehome.google.analytics;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.synopsys.integration.log.LogLevel;
import com.synopsys.integration.log.PrintStreamIntLogger;
import com.synopsys.integration.phonehome.PhoneHomeRequestBody;
import com.synopsys.integration.phonehome.enums.ProductIdEnum;

public class GoogleAnalyticsRequestHelperTest {
    private static final PrintStreamIntLogger logger = new PrintStreamIntLogger(System.out, LogLevel.TRACE);

    @BeforeEach
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

        final GoogleAnalyticsRequestHelper helper = new GoogleAnalyticsRequestHelper(new Gson());

        final HttpPost request = helper.createRequest(bodyBuilder.build(), debugUrl, GoogleAnalyticsConstants.TEST_INTEGRATIONS_TRACKING_ID);
        final BufferedReader requestReader = new BufferedReader(new InputStreamReader(request.getEntity().getContent()));

        String nextRequestLine;
        while ((nextRequestLine = requestReader.readLine()) != null) {
            logger.info(nextRequestLine);
        }

        final HttpClient client = HttpClientBuilder.create().build();

        final HttpResponse response = client.execute(request);
        int responseCode = response.getStatusLine().getStatusCode();
        logger.info("Response Code: " + responseCode);

        String nextLine;
        final BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

        logger.info("Response String:");
        while ((nextLine = reader.readLine()) != null) {
            logger.info(nextLine);
        }

        assertEquals(200, responseCode);
    }

    @Test
    public void batchRequestTest() throws IOException {
        final String debugUrl = GoogleAnalyticsConstants.BASE_URL + GoogleAnalyticsConstants.DEBUG_ENDPOINT;

        final PhoneHomeRequestBody.Builder bodyBuilder = new PhoneHomeRequestBody.Builder();
        bodyBuilder.setCustomerId("fake_customer_id");
        bodyBuilder.setHostName("fake_host_name");
        bodyBuilder.setArtifactId("fake_artifact_id");
        bodyBuilder.setArtifactVersion("fake_artifact_version");
        bodyBuilder.setProductId(ProductIdEnum.CODE_CENTER);
        bodyBuilder.setProductVersion("fake_product_version");
        bodyBuilder.setArtifactModules("fake_module_1", "fake_module_2", "fake_module_3");

        bodyBuilder.addToMetaData("exampleMetaData_1", "data");
        bodyBuilder.addToMetaData("exampleMetaData_2", "other Data");

        final GoogleAnalyticsRequestHelper helper = new GoogleAnalyticsRequestHelper(new Gson());

        final HttpPost request = helper.createRequest(bodyBuilder.build(), debugUrl, GoogleAnalyticsConstants.TEST_INTEGRATIONS_TRACKING_ID);
        final BufferedReader requestReader = new BufferedReader(new InputStreamReader(request.getEntity().getContent()));

        String nextRequestLine;
        while ((nextRequestLine = requestReader.readLine()) != null) {
            logger.info(nextRequestLine);
        }

        final HttpClient client = HttpClientBuilder.create().build();

        final HttpResponse response = client.execute(request);
        int responseCode = response.getStatusLine().getStatusCode();
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
