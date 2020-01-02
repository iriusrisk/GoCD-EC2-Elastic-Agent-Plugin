/*
 * Copyright 2019 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This file incorporates changes by @continuumsecurity
 */

package com.continuumsecurity.elasticagent.ec2.executors;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class GoServerURLFieldTest {
    private final GoServerURLField goServerURLField = new GoServerURLField("0");

    @Test
    public void shouldCheckBlankInput() {
        String result = goServerURLField.doValidate("");

        assertThat(result, is("Go Server URL must not be blank."));
    }

    @Test
    public void shouldCheckIfStringIsValidUrl() {
        String result = goServerURLField.doValidate("foobar");

        assertThat(result, is("Go Server URL must be a valid URL (https://example.com:8154/go)"));
    }

    @Test
    public void shouldCheckIfSchemeIsValid() {
        String result = goServerURLField.doValidate("example.com");

        assertThat(result, is("Go Server URL must be a valid URL (https://example.com:8154/go)"));
    }

    @Test
    public void shouldCheckIfSchemeIsHTTPS() {
        String result = goServerURLField.doValidate("http://example.com");

        assertThat(result, is("Go Server URL must be a valid HTTPs URL (https://example.com:8154/go)"));
    }

    @Test
    public void shouldCheckForLocalhost() {
        String result = goServerURLField.doValidate("https://localhost:8154/go");

        assertThat(result, is("Go Server URL must not be localhost, since this gets resolved on the agents"));

        result = goServerURLField.doValidate("https://127.0.0.1:8154/go");

        assertThat(result, is("Go Server URL must not be localhost, since this gets resolved on the agents"));
    }

    @Test
    public void shouldCheckIfUrlEndsWithContextGo() {
        String result = goServerURLField.doValidate("https://example.com:8154/");
        assertThat(result, is("Go Server URL must be a valid URL ending with '/go' (https://example.com:8154/go)"));

        result = goServerURLField.doValidate("https://example.com:8154/crimemastergogo");
        assertThat(result, is("Go Server URL must be a valid URL ending with '/go' (https://example.com:8154/go)"));
    }

    @Test
    public void shouldReturnNullForValidUrls() {
        String result = goServerURLField.doValidate("https://example.com:8154/go");
        assertThat(result, is(nullValue()));

        result = goServerURLField.doValidate("https://example.com:8154/go/");
        assertThat(result, is(nullValue()));

        result = goServerURLField.doValidate("https://example.com:8154/foo/go/");
        assertThat(result, is(nullValue()));
    }
}
