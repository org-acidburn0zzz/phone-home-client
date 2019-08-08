package com.synopsys.integration.phonehome.google.analytics;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;
import com.synopsys.integration.phonehome.PhoneHomeRequestBody;

public class GoogleAnalyticsRequestTransformer {
    private final List<NameValuePair> parameters = new ArrayList<>();
    private final PhoneHomeRequestBody phoneHomeRequestBody;
    private final String trackingId;
    private final Gson gson;

    public GoogleAnalyticsRequestTransformer(final Gson gson, final String trackingId, final PhoneHomeRequestBody phoneHomeRequestBody) {
        this.gson = gson;
        this.phoneHomeRequestBody = phoneHomeRequestBody;
        this.trackingId = trackingId;
    }

    public List<NameValuePair> getParameters() {
        addParameter(GoogleAnalyticsConstants.API_VERSION_KEY, "1");
        addParameter(GoogleAnalyticsConstants.HIT_TYPE_KEY, "pageview");

        String clientId = generateClientId(phoneHomeRequestBody.getCustomerId(), phoneHomeRequestBody.getHostName());
        addParameter(GoogleAnalyticsConstants.CLIENT_ID_KEY, clientId);
        addParameter(GoogleAnalyticsConstants.TRACKING_ID_KEY, trackingId);
        addParameter(GoogleAnalyticsConstants.DOCUMENT_PATH_KEY, "phone-home");

        // Phone Home Parameters
        addParameter(GoogleAnalyticsConstants.CUSTOMER_ID, phoneHomeRequestBody.getCustomerId());
        addParameter(GoogleAnalyticsConstants.HOST_NAME, phoneHomeRequestBody.getHostName());
        addParameter(GoogleAnalyticsConstants.ARTIFACT_ID, phoneHomeRequestBody.getArtifactId());
        addParameter(GoogleAnalyticsConstants.ARTIFACT_VERSION, phoneHomeRequestBody.getArtifactVersion());
        addParameter(GoogleAnalyticsConstants.PRODUCT_ID, phoneHomeRequestBody.getProductId().name());
        addParameter(GoogleAnalyticsConstants.PRODUCT_VERSION, phoneHomeRequestBody.getProductVersion());
        addParameter(GoogleAnalyticsConstants.META_DATA, gson.toJson(phoneHomeRequestBody.getMetaData()));

        return parameters;
    }

    private void addParameter(final String key, final String value) {
        final NameValuePair parameter = new BasicNameValuePair(key, value);
        parameters.add(parameter);
    }

    private String generateClientId(final String customerId, final String hostName) {
        final String clientId;
        if (!PhoneHomeRequestBody.Builder.UNKNOWN_ID.equals(customerId)) {
            clientId = customerId;
        } else if (!PhoneHomeRequestBody.Builder.UNKNOWN_ID.equals(hostName)) {
            clientId = hostName;
        } else {
            clientId = PhoneHomeRequestBody.Builder.UNKNOWN_ID;
        }

        final byte[] bytesFromString = clientId.getBytes();
        return UUID.nameUUIDFromBytes(bytesFromString).toString();
    }
}