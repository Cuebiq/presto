/*
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
package com.facebook.presto.plugin.redshift;

import com.facebook.presto.plugin.jdbc.BaseJdbcConfig;
import com.fasterxml.jackson.databind.JsonNode;
import io.airlift.configuration.Config;
import io.airlift.log.Logger;

public class RedshiftConfig
        extends BaseJdbcConfig
{
    private static final Logger log = Logger.get(RedshiftConfig.class);
    protected String secretName;

    public String getSecretName()
    {
        return this.secretName;
    }

    @Config("redshift.secret-name")
    public RedshiftConfig setSecretName(String secretName)
    {
        this.secretName = secretName;
        if (this.secretName == null || this.secretName.isEmpty()) {
            throw new IllegalArgumentException("Redshift secret name was null or empty!");
        }
        AwsSecretManager asm = new AwsSecretManager();
        JsonNode secretsJson = asm.getSecret(this.secretName);
        this.setConnectionUser(secretsJson.get("username").textValue());
        this.setConnectionPassword(secretsJson.get("password").textValue());

        return this;
    }
}
