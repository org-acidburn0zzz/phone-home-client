/**
 * phone-home-client
 *
 * Copyright (c) 2020 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.phonehome;

import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.phonehome.request.PhoneHomeRequestBody;
import com.synopsys.integration.util.NoThreadExecutorService;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PhoneHomeService {
    private final IntLogger logger;
    private final PhoneHomeClient phoneHomeClient;
    private final ExecutorService executorService;

    public static PhoneHomeService createPhoneHomeService(IntLogger logger, PhoneHomeClient phoneHomeClient) {
        return new PhoneHomeService(logger, phoneHomeClient, new NoThreadExecutorService());
    }

    public static PhoneHomeService createAsynchronousPhoneHomeService(IntLogger logger, PhoneHomeClient phoneHomeClient, ExecutorService executorService) {
        return new PhoneHomeService(logger, phoneHomeClient, executorService);
    }

    public static PhoneHomeService creatDefaultAsynchronousPhoneHomeService(IntLogger logger) {
        PhoneHomeClient phoneHomeClient = new PhoneHomeClient(logger);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        return new PhoneHomeService(logger, phoneHomeClient, executorService);
    }

    public PhoneHomeService(IntLogger logger, PhoneHomeClient phoneHomeClient, ExecutorService executorService) {
        this.logger = logger;
        this.phoneHomeClient = phoneHomeClient;
        this.executorService = executorService;
    }

    public PhoneHomeResponse phoneHome(PhoneHomeRequestBody phoneHomeRequestBody) {
        return phoneHome(phoneHomeRequestBody, Collections.emptyMap());
    }

    public PhoneHomeResponse phoneHome(PhoneHomeRequestBody phoneHomeRequestBody, Map<String, String> environmentVariables) {
        Future<Boolean> phoneHomeTask = null;
        PhoneHomeCallable phoneHomeCallable = new PhoneHomeCallable(phoneHomeRequestBody, environmentVariables);
        try {
            phoneHomeTask = executorService.submit(phoneHomeCallable);
        } catch (Exception e) {
            logger.debug("Problem executing phone home asynchronously: " + e.getMessage(), e);
        }
        return PhoneHomeResponse.createAsynchronousResponse(phoneHomeTask);
    }

    private class PhoneHomeCallable implements Callable<Boolean> {
        private final PhoneHomeRequestBody phoneHomeRequestBody;
        private final Map<String, String> environmentVariables;

        public PhoneHomeCallable(PhoneHomeRequestBody phoneHomeRequestBody, Map<String, String> environmentVariables) {
            this.phoneHomeRequestBody = phoneHomeRequestBody;
            this.environmentVariables = environmentVariables;
        }

        @Override
        public Boolean call() {
            Boolean result = Boolean.FALSE;
            try {
                logger.debug("starting phone home");
                phoneHomeClient.postPhoneHomeRequest(phoneHomeRequestBody, environmentVariables);
                result = Boolean.TRUE;
                logger.debug("completed phone home");
            } catch (Exception ex) {
                logger.debug("Phone home error.", ex);
            }

            return result;
        }
    }

}
