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
 */

package com.continuumsecurity.elasticagent.ec2.executors;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static com.continuumsecurity.elasticagent.ec2.utils.Util.isBlank;


public class GoServerURLMetadata extends Metadata {
    private static String GO_SERVER_URL = "go_server_url";
    private static String GO_SERVER_URL_DISPLAY_VALUE = "Go Server URL";

    public GoServerURLMetadata() {
        super(GO_SERVER_URL, true, false);
    }

    @Override
    public String doValidate(String input) {
        if (isBlank(input)) {
            return GO_SERVER_URL_DISPLAY_VALUE + " must not be blank.";
        }

        URI uri = null;
        try {
            uri = new URL(input).toURI();
        } catch (MalformedURLException | URISyntaxException e) {
            return GO_SERVER_URL_DISPLAY_VALUE + " must be a valid URL (https://example.com:8154/go)";
        }

        if (isBlank(uri.getScheme())) {
            return GO_SERVER_URL_DISPLAY_VALUE + " must be a valid URL (https://example.com:8154/go)";
        }

        if (!uri.getScheme().equalsIgnoreCase("https")) {
            return GO_SERVER_URL_DISPLAY_VALUE + " must be a valid HTTPs URL (https://example.com:8154/go)";
        }

        if (uri.getHost().equalsIgnoreCase("localhost") || uri.getHost().equalsIgnoreCase("127.0.0.1")) {
            return GO_SERVER_URL_DISPLAY_VALUE + " must not be localhost, since this gets resolved on the agents";
        }

        if (!(uri.getPath().endsWith("/go") || uri.getPath().endsWith("/go/"))) {
            return GO_SERVER_URL_DISPLAY_VALUE + " must be a valid URL ending with '/go' (https://example.com:8154/go)";
        }

        return null;
    }
}
