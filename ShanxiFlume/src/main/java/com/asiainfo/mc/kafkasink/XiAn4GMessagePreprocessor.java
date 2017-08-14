/**
 Licensed to the Apache Software Foundation (ASF) under one or more
 contributor license agreements.  See the NOTICE file distributed with
 this work for additional information regarding copyright ownership.
 The ASF licenses this file to You under the Apache License, Version 2.0
 (the "License"); you may not use this file except in compliance with
 the License.  You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 limitations under the License.
 */

package com.asiainfo.mc.kafkasink;

import org.apache.commons.lang.StringUtils;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is an example of a <code>MessagePreprocessor</code> implementation.
 */
public class XiAn4GMessagePreprocessor implements MessagePreprocessor {
    private static final Logger logger = LoggerFactory.getLogger(KafkaSink.class);
    /**
     * extract the hour of the time stamp as the key. So the data is partitioned
     * per hour.
     * @param event This is the Flume event that will be sent to Kafka
     * @param context The Flume runtime context.
     * @return Hour of the timestamp
     */
    @Override
    public String extractKey(Event event, Context context) {
        // get timestamp header if it's present.
        String key = event.getHeaders().get("key");
        return key;
    }

    /**
     * A custom property is read from the Flume config.
     * @param event This is the Flume event that will be sent to Kafka
     * @param context The Flume runtime context.
     * @return topic provided as a custom property
     */
    @Override
    public String extractTopic(Event event, Context context) {
        return context.getString("topic", "default-topic");

        //by chenrui 待测试
        //return context.getString("topic", "default-topic");
    }

    /**
     * Trying to prepend each message with the timestamp.
     * @return modified message of the form: timestamp + ":" + original message body
     */
    @Override
    public String transformMessage(String messageBody) {
        return messageBody;
    }
    // 将unix时间转成格式化时间
    public String TimeStamp2Date(String timestampString){
        Long timestamp = Long.parseLong(timestampString);
        String date = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date(timestamp));
        return date;
    }
}
