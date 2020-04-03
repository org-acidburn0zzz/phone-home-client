package com.synopsys.integration.phonehome;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.synopsys.integration.log.BufferedIntLogger;
import com.synopsys.integration.log.LogLevel;
import com.synopsys.integration.log.PrintStreamIntLogger;
import com.synopsys.integration.phonehome.enums.ProductIdEnum;
import com.synopsys.integration.phonehome.exception.PhoneHomeException;
import com.synopsys.integration.phonehome.google.analytics.GoogleAnalyticsConstants;
import com.synopsys.integration.phonehome.request.PhoneHomeRequestBody;

public class PhoneHomeClientUnitTest {
    private static final RequestConfig DEFAULT_REQUEST_CONFIG = PhoneHomeClient.createInitialRequestConfigBuilder(5).build();

    private Map<String, String> defaultEnvironmentVariables;
    private PhoneHomeClient defaultClient;

    @BeforeEach
    public void init() {
        final PrintStreamIntLogger logger = new PrintStreamIntLogger(System.out, LogLevel.TRACE);
        logger.info("\n");
        logger.info("Test Class: PhoneHomeClientUnitTest");
        defaultEnvironmentVariables = new HashMap<>();
        defaultEnvironmentVariables.put(PhoneHomeClient.PHONE_HOME_URL_OVERRIDE_VARIABLE, GoogleAnalyticsConstants.BASE_URL + GoogleAnalyticsConstants.DEBUG_ENDPOINT);
        defaultClient = new PhoneHomeClient(logger);
    }

    @Test
    public void callHomeIntegrationsTest() throws Exception {
        final PhoneHomeRequestBody.Builder phoneHomeRequestBuilder = new PhoneHomeRequestBody.Builder();
        phoneHomeRequestBuilder.setCustomerId("customerId");
        phoneHomeRequestBuilder.setHostName("hostName");
        phoneHomeRequestBuilder.setArtifactId("artifactId");
        phoneHomeRequestBuilder.setArtifactVersion("artifactVersion");
        phoneHomeRequestBuilder.setProductId(ProductIdEnum.CODE_CENTER);
        phoneHomeRequestBuilder.setProductVersion("productVersion");
        final PhoneHomeRequestBody phoneHomeRequest = phoneHomeRequestBuilder.build();

        defaultClient.postPhoneHomeRequest(phoneHomeRequest, defaultEnvironmentVariables);
    }

    @Test
    public void callHomeSkip() throws Exception {
        final BufferedIntLogger logger = new BufferedIntLogger();
        final PhoneHomeClient clientWithTrackableLogger = new PhoneHomeClient(logger, DEFAULT_REQUEST_CONFIG);

        final PhoneHomeRequestBody.Builder phoneHomeRequestBuilder = new PhoneHomeRequestBody.Builder();
        phoneHomeRequestBuilder.setCustomerId("customerId");
        phoneHomeRequestBuilder.setHostName("hostName");
        phoneHomeRequestBuilder.setArtifactId("artifactId");
        phoneHomeRequestBuilder.setArtifactVersion("artifactVersion");
        phoneHomeRequestBuilder.setProductId(ProductIdEnum.CODE_CENTER);
        phoneHomeRequestBuilder.setProductVersion("productVersion");
        final PhoneHomeRequestBody phoneHomeRequest = phoneHomeRequestBuilder.build();

        defaultEnvironmentVariables.put(PhoneHomeClient.SKIP_PHONE_HOME_VARIABLE, "true");

        clientWithTrackableLogger.postPhoneHomeRequest(phoneHomeRequest, defaultEnvironmentVariables);
        assertTrue(logger.getOutputString(LogLevel.DEBUG).contains("Skipping phone home"));

        defaultEnvironmentVariables.put(PhoneHomeClient.SKIP_PHONE_HOME_VARIABLE, "false");

        clientWithTrackableLogger.postPhoneHomeRequest(phoneHomeRequest, defaultEnvironmentVariables);
        assertTrue(logger.getOutputString(LogLevel.DEBUG).contains("Phoning home to "));
    }

    @Test
    public void testPhoneHomeException() {
        final PhoneHomeException emptyException = new PhoneHomeException();
        final PhoneHomeException exceptionException = new PhoneHomeException(emptyException);
        final PhoneHomeException messageException = new PhoneHomeException("message");
        final PhoneHomeException exceptionAndMessageException = new PhoneHomeException("message", emptyException);
        for (final PhoneHomeException phoneHomeException : new PhoneHomeException[] { emptyException, exceptionException, messageException, exceptionAndMessageException }) {
            try {
                throw phoneHomeException;
            } catch (final PhoneHomeException e) {
                // Do nothing
            }
        }
    }

    @Test
    public void testPostBadPhoneHomeRequestBuilding() throws Exception {
        final PhoneHomeRequestBody.Builder phoneHomeRequestBuilder = new PhoneHomeRequestBody.Builder();
        try {
            phoneHomeRequestBuilder.build();
            fail("Illegal state exception not thrown");
        } catch (final IllegalStateException e) {
            // Do nothing
        }
        try {
            phoneHomeRequestBuilder.setArtifactVersion("artifactVersion");
            phoneHomeRequestBuilder.setProductId(ProductIdEnum.CODE_CENTER);
            phoneHomeRequestBuilder.build();
            fail("Illegal state exception not thrown");
        } catch (final IllegalStateException e) {
            // Do nothing
        }
    }

    @Test
    public void validatePhoneHomeRequestBuilding() throws Exception {
        final PhoneHomeRequestBody.Builder phoneHomeRequestBuilder = new PhoneHomeRequestBody.Builder();
        phoneHomeRequestBuilder.setCustomerId("customerId");
        phoneHomeRequestBuilder.setHostName("hostName");
        phoneHomeRequestBuilder.setArtifactId("artifactId");
        phoneHomeRequestBuilder.setArtifactVersion("artifactVersion");
        phoneHomeRequestBuilder.setProductId(ProductIdEnum.CODE_CENTER);
        phoneHomeRequestBuilder.setProductVersion("productVersion");
        phoneHomeRequestBuilder.addToMetaData("example_meta_data", "data");

        final Map<String, String> builderMetaData = new HashMap<>(phoneHomeRequestBuilder.getMetaData());

        final PhoneHomeRequestBody phoneHomeRequest = phoneHomeRequestBuilder.build();

        assertEquals(phoneHomeRequestBuilder.getCustomerId(), phoneHomeRequest.getCustomerId());
        assertEquals(phoneHomeRequestBuilder.getArtifactId(), phoneHomeRequest.getArtifactId());
        assertEquals(phoneHomeRequestBuilder.getArtifactVersion(), phoneHomeRequest.getArtifactVersion());
        assertEquals(phoneHomeRequestBuilder.getProductId(), phoneHomeRequest.getProductId());
        assertEquals(phoneHomeRequestBuilder.getProductVersion(), phoneHomeRequest.getProductVersion());
        assertEquals(builderMetaData, phoneHomeRequest.getMetaData());
    }

    @Test
    public void validateBadPhoneHomeBackend() {
        PhoneHomeClient phClient = new PhoneHomeClient(null);
        try {
            phClient.postPhoneHomeRequest(null, Collections.emptyMap());
            fail("Phone home exception not thrown");
        } catch (final PhoneHomeException e) {
            // Do nothing
        }

        phClient = new PhoneHomeClient(null, (RequestConfig) null);
        try {
            phClient.postPhoneHomeRequest(null, Collections.emptyMap());
            fail("Phone home exception not thrown");
        } catch (final PhoneHomeException e) {
            // Do nothing
        }

        phClient = new PhoneHomeClient(null, (HttpClientBuilder) null);
        try {
            phClient.postPhoneHomeRequest(null, Collections.emptyMap());
            fail("Phone home exception not thrown");
        } catch (final PhoneHomeException e) {
            // Do nothing
        }

        phClient = new PhoneHomeClient(null, (RequestConfig) null, (Gson) null);
        try {
            phClient.postPhoneHomeRequest(null, Collections.emptyMap());
            fail("Phone home exception not thrown");
        } catch (final PhoneHomeException e) {
            // Do nothing
        }

        phClient = new PhoneHomeClient(null, (HttpClientBuilder) null, (Gson) null);
        try {
            phClient.postPhoneHomeRequest(null, Collections.emptyMap());
            fail("Phone home exception not thrown");
        } catch (final PhoneHomeException e) {
            // Do nothing
        }
    }

    @Test
    public void createInitialRequestConfigBuilderWithProxyHostTest() {
        final HttpHost proxyHost = new HttpHost("localhost:8081");
        PhoneHomeClient.createInitialRequestConfigBuilder(5, proxyHost);
    }

}
