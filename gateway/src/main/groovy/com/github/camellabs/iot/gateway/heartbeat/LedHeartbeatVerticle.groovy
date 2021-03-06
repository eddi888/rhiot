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
package com.github.camellabs.iot.gateway.heartbeat

import com.github.camellabs.iot.gateway.GatewayVerticle
import com.github.camellabs.iot.vertx.camel.GroovyCamelVerticle

import static com.github.camellabs.iot.vertx.PropertyResolver.intProperty
import static com.github.camellabs.iot.vertx.PropertyResolver.stringProperty

@GatewayVerticle(conditionProperty = 'camellabs.iot.gateway.heartbeat.led')
class LedHeartbeatVerticle extends GroovyCamelVerticle {

    private final def ledComponent = stringProperty('camellabs.iot.gateway.heartbeat.led.component', 'pi4j-gpio')

    private final def ledGppioId = intProperty('camellabs.iot.gateway.heartbeat.led.gpioId', 0)

    @Override
    void start() {
        fromEventBus('heartbeat') {
            it.to("${ledComponent}:${ledGppioId}?mode=DIGITAL_OUTPUT&state=LOW&action=BLINK")
        }
    }

}