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
package com.github.camellabs.test.raspberrypi.input;

import com.github.camellabs.component.raspberrypi.RaspberryPiConsumer;
import com.github.camellabs.component.raspberrypi.mock.RaspiGpioProviderMock;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class DigitalInputLow2Test extends CamelTestSupport {

    static {
        // Mandatory we are not inside a Real Raspberry PI
        GpioFactory.setDefaultProvider(new RaspiGpioProviderMock());
    }

    @Test
    public void consumeDigitalEvent() throws Exception {

        RaspberryPiConsumer pinConsumer = (RaspberryPiConsumer)this.context.getRoute("test-route").getConsumer();

        MockEndpoint mock = getMockEndpoint("mock:result");

        pinConsumer.handleGpioPinDigitalStateChangeEvent(new GpioPinDigitalStateChangeEvent("CAMEL-EVENT", (GpioPin)pinConsumer.getPin(), PinState.LOW));

        mock.expectedMessageCount(1);

        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("raspberrypi://pin:13?mode=DIGITAL_INPUT&state=LOW").id("test-route").to("mock:result");

            }
        };
    }
}