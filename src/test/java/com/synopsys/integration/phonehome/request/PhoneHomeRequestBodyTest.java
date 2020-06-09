package com.synopsys.integration.phonehome.request;

import com.synopsys.integration.phonehome.UniquePhoneHomeProduct;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PhoneHomeRequestBodyTest {
    @Test
    public void testBuildingImmutablePhoneHomeRequestBody() {
        PhoneHomeRequestBodyBuilder builder = PhoneHomeRequestBodyBuilder.createForBlackDuck("artifactId", "customerId", "hostName", "artifactVersion", "productVersion");

        builder.addArtifactModules("module1", "module2");
        builder.addToMetaData("metaKey", "metaValue");
        Map<String, String> metaData = new HashMap<>();
        metaData.put("metaKey2", "metaValue2");
        builder.addAllToMetaData(metaData);

        PhoneHomeRequestBody phoneHomeRequestBody = builder.build();
        assertNotNull(phoneHomeRequestBody);

        assertEquals("artifactId", phoneHomeRequestBody.getArtifactId());
        assertEquals("artifactVersion", phoneHomeRequestBody.getArtifactVersion());
        assertEquals("customerId", phoneHomeRequestBody.getCustomerId());
        assertEquals("hostName", phoneHomeRequestBody.getHostName());
        assertEquals(UniquePhoneHomeProduct.BLACK_DUCK.getName(), phoneHomeRequestBody.getProductName());
        assertEquals("productVersion", phoneHomeRequestBody.getProductVersion());

        assertEquals(2, phoneHomeRequestBody.getMetaData().size());
        assertTrue(phoneHomeRequestBody.getMetaData().containsKey("metaKey"));
        assertTrue(phoneHomeRequestBody.getMetaData().containsKey("metaKey2"));
        assertEquals("metaValue", phoneHomeRequestBody.getMetaData().get("metaKey"));
        assertEquals("metaValue2", phoneHomeRequestBody.getMetaData().get("metaKey2"));

        assertEquals(2, phoneHomeRequestBody.getArtifactModules().size());
        assertTrue(phoneHomeRequestBody.getArtifactModules().contains("module1"));
        assertTrue(phoneHomeRequestBody.getArtifactModules().contains("module2"));
    }

    @Test
    public void validatePhoneHomeRequestBuilding() throws Exception {
        PhoneHomeRequestBodyBuilder builder = PhoneHomeRequestBodyBuilder.createForProduct(UniquePhoneHomeProduct.CODE_CENTER, "artifactId", "customerId", "hostName", "artifactVersion", "productVersion");

        Map<String, String> expectedMetaData = new HashMap<>();
        expectedMetaData.put("example_meta_data", "data");
        builder.addAllToMetaData(expectedMetaData);

        PhoneHomeRequestBody phoneHomeRequest = builder.build();

        assertEquals("customerId", phoneHomeRequest.getCustomerId());
        assertEquals("artifactId", phoneHomeRequest.getArtifactId());
        assertEquals("artifactVersion", phoneHomeRequest.getArtifactVersion());
        assertEquals(UniquePhoneHomeProduct.CODE_CENTER.getName(), phoneHomeRequest.getProductName());
        assertEquals("productVersion", phoneHomeRequest.getProductVersion());
        assertEquals(expectedMetaData, phoneHomeRequest.getMetaData());
    }

}
