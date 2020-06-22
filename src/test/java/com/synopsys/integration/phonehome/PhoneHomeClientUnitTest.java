package com.synopsys.integration.phonehome;

import com.google.gson.Gson;
import com.synopsys.integration.log.BufferedIntLogger;
import com.synopsys.integration.log.LogLevel;
import com.synopsys.integration.log.PrintStreamIntLogger;
import com.synopsys.integration.phonehome.exception.PhoneHomeException;
import com.synopsys.integration.phonehome.google.analytics.GoogleAnalyticsConstants;
import com.synopsys.integration.phonehome.request.PhoneHomeRequestBody;
import com.synopsys.integration.phonehome.request.PhoneHomeRequestBodyBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class PhoneHomeClientUnitTest {
    private Map<String, String> defaultEnvironmentVariables;
    private PhoneHomeClient defaultClient;

    @BeforeEach
    public void init() {
        PrintStreamIntLogger logger = new PrintStreamIntLogger(System.out, LogLevel.TRACE);
        logger.info("\n");
        logger.info("Test Class: PhoneHomeClientUnitTest");
        defaultEnvironmentVariables = new HashMap<>();
        defaultEnvironmentVariables.put(PhoneHomeClient.PHONE_HOME_URL_OVERRIDE_VARIABLE, GoogleAnalyticsConstants.BASE_URL + GoogleAnalyticsConstants.DEBUG_ENDPOINT);
        defaultClient = new PhoneHomeClient(logger, HttpClientBuilder.create(), new Gson());
    }

    @Test
    public void callHomeIntegrationsTest() throws Exception {
        PhoneHomeRequestBody phoneHomeRequest = PhoneHomeRequestBodyBuilder
                .createForProduct(UniquePhoneHomeProduct.CODE_CENTER, "artifactId", "customerId", "hostName", "artifactVersion", "productVersion")
                .build();

        defaultClient.postPhoneHomeRequest(phoneHomeRequest, defaultEnvironmentVariables);
    }

    @Test
    public void callHomeSkip() throws Exception {
        BufferedIntLogger logger = new BufferedIntLogger();
        PhoneHomeClient clientWithTrackableLogger = new PhoneHomeClient(logger, HttpClientBuilder.create(), new Gson());

        PhoneHomeRequestBody phoneHomeRequest = PhoneHomeRequestBodyBuilder
                .createForProduct(UniquePhoneHomeProduct.CODE_CENTER, "artifactId", "customerId", "hostName", "artifactVersion", "productVersion")
                .build();

        defaultEnvironmentVariables.put(PhoneHomeClient.SKIP_PHONE_HOME_VARIABLE, "true");

        clientWithTrackableLogger.postPhoneHomeRequest(phoneHomeRequest, defaultEnvironmentVariables);
        assertTrue(logger.getOutputString(LogLevel.DEBUG).contains("Skipping phone home"));

        defaultEnvironmentVariables.put(PhoneHomeClient.SKIP_PHONE_HOME_VARIABLE, "false");

        clientWithTrackableLogger.postPhoneHomeRequest(phoneHomeRequest, defaultEnvironmentVariables);
        assertTrue(logger.getOutputString(LogLevel.DEBUG).contains("Phoning home to "));
    }

    @Test
    public void testPhoneHomeException() {
        PhoneHomeException emptyException = new PhoneHomeException();
        PhoneHomeException exceptionException = new PhoneHomeException(emptyException);
        PhoneHomeException messageException = new PhoneHomeException("message");
        PhoneHomeException exceptionAndMessageException = new PhoneHomeException("message", emptyException);
        for (PhoneHomeException phoneHomeException : new PhoneHomeException[]{emptyException, exceptionException, messageException, exceptionAndMessageException}) {
            try {
                throw phoneHomeException;
            } catch (PhoneHomeException e) {
                // Do nothing
            }
        }
    }

    @Test
    public void validateBadPhoneHomeBackend() {
        PhoneHomeClient phClient = new PhoneHomeClient(null, (HttpClientBuilder) null, (Gson) null);
        try {
            phClient.postPhoneHomeRequest(null, Collections.emptyMap());
            fail("Phone home exception not thrown");
        } catch (PhoneHomeException e) {
            // Do nothing
        }
    }

}
