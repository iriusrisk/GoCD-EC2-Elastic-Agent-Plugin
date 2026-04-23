/*
 * Copyright 2017 ThoughtWorks, Inc.
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


import static com.continuumsecurity.elasticagent.ec2.utils.Util.isBlank;

public class NumberMetadata extends Metadata {

    public NumberMetadata(String key, boolean required) {
        super(key, required, false);
    }

    @Override
    protected String doValidate(String input) {
        if (isRequired() || !isBlank(input)) {
            if (isBlank(input) || Integer.parseInt(input) < 0) {
                return this.getKey() + " must be a positive integer.";
            }
        }
        return null;
    }
}
