/**
 * Licensed to the Camel Labs under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.camellabs.component.pubnub.example;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.Main;

public class PubNubPresenseExample {

    public static void main(String[] args) throws Exception {
        Main main = new Main();
        main.enableHangupSupport();
        main.addRouteBuilder(new PresensRoute());
        main.run();
    }

    static private class PresensRoute extends RouteBuilder {
        @Override
        public void configure() throws Exception {
            //@formatter:off
            from("pubnub://presence:iot?subscriberKey="+PubNubExampleConstants.PUBNUB_SUBSCRIBER_KEY)
            .log("${body}")
            .to("mock:result");
            //@formatter:on
        }
    }

}
