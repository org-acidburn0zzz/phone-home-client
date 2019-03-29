/**
 * phone-home-client
 *
 * Copyright (c) 2019 Synopsys, Inc.
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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PhoneHomeResponse {
    private final Future<Boolean> phoneHomeTask;
    private final Boolean result;

    public static PhoneHomeResponse createResponse(final Boolean result) {
        return new PhoneHomeResponse(null, result);
    }

    public static PhoneHomeResponse createAsynchronousResponse(final Future<Boolean> phoneHomeTask) {
        return new PhoneHomeResponse(phoneHomeTask, Boolean.FALSE);
    }

    private PhoneHomeResponse(final Future<Boolean> phoneHomeTask, final Boolean result) {
        this.phoneHomeTask = phoneHomeTask;
        this.result = result;
    }

    /**
     * If Phone Home was called asynchronously: If the result is available, it will be returned, otherwise the task will be cancelled and this will return Boolean.FALSE.
     * If Phone Home was called synchronously: The result of the synchronous call will be returned.
     */
    public Boolean getImmediateResult() {
        return awaitResult(0L);
    }

    /**
     * @param timeoutInSeconds time to wait before cancelling the task
     */
    public Boolean awaitResult(final long timeoutInSeconds) {
        if (phoneHomeTask != null) {
            try {
                return phoneHomeTask.get(timeoutInSeconds, TimeUnit.SECONDS);
            } catch (final InterruptedException | ExecutionException | TimeoutException e) {
                return Boolean.FALSE;
            } finally {
                endPhoneHome();
            }
        }
        return result;
    }

    public boolean isDone() {
        if (phoneHomeTask != null) {
            return phoneHomeTask.isDone();
        }
        return true;
    }

    private boolean endPhoneHome() {
        if (phoneHomeTask != null) {
            if (!phoneHomeTask.isDone()) {
                return phoneHomeTask.cancel(true);
            }
        }
        return false;
    }
}
