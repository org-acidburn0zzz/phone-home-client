/**
 * phone-home-client
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.blackducksoftware.integration.phonehome;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import com.blackducksoftware.integration.log.IntLogger;

public class PhoneHomeService {
    private final IntLogger logger;
    private final ExecutorService executorService;

    public PhoneHomeService(final IntLogger logger, final ExecutorService executorService) {
        this.logger = logger;
        this.executorService = executorService;
    }

    public PhoneHomeService(final IntLogger logger) {
        this.logger = logger;
        final ThreadFactory threadFactory = Executors.defaultThreadFactory();
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), threadFactory);
    }

    public PhoneHomeResponse startPhoneHome(final PhoneHomeCallable phoneHomeCallable) {
        try {
            final Future<Boolean> resultTask = executorService.submit(phoneHomeCallable);
            return new PhoneHomeResponse(resultTask);
        } catch (final Exception e) {
            logger.debug("Could not build phone home body" + e.getMessage(), e);
        }
        return null;
    }

    public Boolean phoneHome(final PhoneHomeCallable phoneHomeCallable) {
        try {
            return phoneHomeCallable.call();
        } catch (final Exception e) {
            logger.debug("Could not build phone home body" + e.getMessage(), e);
        }
        return false;
    }
}
