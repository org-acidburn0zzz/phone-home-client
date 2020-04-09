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

import com.synopsys.integration.util.Stringable;

import java.util.HashSet;
import java.util.Set;

public final class UniquePhoneHomeProduct extends Stringable {
    private static final Set<String> USED_NAMES = new HashSet<>();

    public static final UniquePhoneHomeProduct BLACK_DUCK = create("BLACK_DUCK");
    public static final UniquePhoneHomeProduct CODE_CENTER = create("CODE_CENTER");
    public static final UniquePhoneHomeProduct COVERITY = create("COVERITY");
    public static final UniquePhoneHomeProduct POLARIS = create("POLARIS");
    public static final UniquePhoneHomeProduct PROTEX = create("PROTEX");

    public static UniquePhoneHomeProduct create(String name) {
        if (!USED_NAMES.add(name)) {
            throw new IllegalArgumentException(String.format("The product name '%s' is already defined - using it again could cause unintended collisions in the data.", name));
        }
        return new UniquePhoneHomeProduct(name);
    }

    public static boolean isUsed(String name) {
        return USED_NAMES.contains(name);
    }

    private String name;

    private UniquePhoneHomeProduct(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
