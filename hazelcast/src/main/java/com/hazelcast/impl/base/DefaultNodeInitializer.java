/* 
 * Copyright (c) 2008-2010, Hazel Ltd. All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.hazelcast.impl.base;

import com.hazelcast.impl.IHazelcastFactory;
import com.hazelcast.impl.Node;
import com.hazelcast.impl.concurrentmap.DefaultRecordFactory;
import com.hazelcast.impl.concurrentmap.RecordFactory;
import com.hazelcast.impl.concurrentmap.SimpleRecordFactory;
import com.hazelcast.logging.ILogger;
import com.hazelcast.security.SecurityContext;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;

public class DefaultNodeInitializer implements NodeInitializer {

    protected ILogger logger;
    protected ILogger systemLogger;
    protected String version;
    protected String build;
    private int buildNumber;
    protected boolean simpleRecord = false;

    public void beforeInitialize(Node node) {
        systemLogger = node.getLogger("com.hazelcast.system");
        logger = node.getLogger("com.hazelcast.initializer");
        parseSystemProps();
        simpleRecord = node.groupProperties.CONCURRENT_MAP_SIMPLE_RECORD.getBoolean();
    }

    public void afterInitialize(Node node) {
        systemLogger.log(Level.INFO, "Hazelcast Community Edition " + version + " ("
                + build + ") starting at " + node.getThisAddress());
        systemLogger.log(Level.INFO, "Copyright (C) 2008-2011 Hazelcast.com");
    }

    protected void parseSystemProps() {
        version = System.getProperty("hazelcast.version", "unknown");
        build = System.getProperty("hazelcast.build", "unknown");
        if ("unknown".equals(version) || "unknown".equals(build)) {
            try {
                InputStream inRuntimeProperties = NodeInitializer.class.getClassLoader().getResourceAsStream("hazelcast-runtime.properties");
                if (inRuntimeProperties != null) {
                    Properties runtimeProperties = new Properties();
                    runtimeProperties.load(inRuntimeProperties);
                    version = runtimeProperties.getProperty("hazelcast.version");
                    build = runtimeProperties.getProperty("hazelcast.build");
                }
            } catch (Exception ignored) {
            }
        }
        try {
            buildNumber = Integer.getInteger("hazelcast.build", -1);
            if (buildNumber == -1) {
                buildNumber = Integer.parseInt(build);
            }
        } catch (Exception ignored) {
        }
    }

    public RecordFactory getRecordFactory() {
        return simpleRecord ? new SimpleRecordFactory() : new DefaultRecordFactory();
    }

    public int getBuildNumber() {
        return buildNumber;
    }

    public String getVersion() {
        return version;
    }

    public String getBuild() {
        return build;
    }

    public SecurityContext createSecurityContext() {
        logger.log(Level.WARNING, "Security features are only available on Hazelcast Enterprise Edition!");
        return null;
    }

    public IHazelcastFactory getSecureHazelcastFactory() {
        logger.log(Level.WARNING, "Security features are only available on Hazelcast Enterprise Edition!");
        return null;
    }
}
