# Rhiot - the messaging platform for the Internet Of Things

<a href="https://github.com/rhiot/rhiot"><img src="../rhiot.png" align="left" height="80" hspace="30"></a>
Rhiot is the messaging platform for the Internet Of Things. We are focused on the adoption of the
[Red Hat JBoss middleware portfolio](http://www.redhat.com/en/technologies/jboss-middleware) to provide the solutions to
the common IoT-related challenges.

<br>

Rhiot comes with the following features:
- [IoT gateway software](https://github.com/rhiot/rhiot/blob/master/docs/readme.md#camel-iot-gateway)
- [Camel components for the IoT](https://github.com/rhiot/rhiot/blob/master/docs/readme.md#camel-iot-components)
- [Backend cloud services (Cloudlets)](https://github.com/rhiot/rhiot/blob/master/docs/readme.md#cloudlets)
- Web console for managing the devices, gateways and Cloudlets
- IoT deployment utilities
- [Performance Testing Framework for the IoT gateways](https://github.com/rhiot/rhiot/blob/master/docs/readme.md#performance-testing-framework)

## Table Of Contents
 
<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->


- [Camel IoT Labs stack](#camel-iot-labs-stack)
- [Camel IoT gateway](#camel-iot-gateway)
  - [Installing gateway on the Raspbian](#installing-gateway-on-the-raspbian)
  - [Configuration of the gateway](#configuration-of-the-gateway)
  - [Gateway logger configuration](#gateway-logger-configuration)
  - [Device heartbeats](#device-heartbeats)
    - [Logging heartbeat](#logging-heartbeat)
    - [MQTT heartbeat](#mqtt-heartbeat)
    - [LED heartbeat](#led-heartbeat)
  - [Monitoring gateway with Jolokia](#monitoring-gateway-with-jolokia)
  - [Adding the custom code to the gateway](#adding-the-custom-code-to-the-gateway)
    - [Adding custom Groovy Camel verticle to the gateway](#adding-custom-groovy-camel-verticle-to-the-gateway)
- [Camel IoT components](#camel-iot-components)
  - [Camel GPS BU353 component](#camel-gps-bu353-component)
    - [Maven dependency](#maven-dependency)
    - [URI format](#uri-format)
    - [Options](#options)
    - [Process manager](#process-manager)
  - [Camel Kura Wifi component](#camel-kura-wifi-component)
    - [Maven dependency](#maven-dependency-1)
    - [URI format](#uri-format-1)
    - [Options](#options-1)
    - [Detecting Kura NetworkService](#detecting-kura-networkservice)
  - [Camel TinkerForge component](#camel-tinkerforge-component)
    - [Maven dependency](#maven-dependency-2)
    - [General URI format](#general-uri-format)
      - [Ambientlight](#ambientlight)
      - [Temperature](#temperature)
      - [Lcd20x4](#lcd20x4)
        - [Optional URI Parameters](#optional-uri-parameters)
    - [Humidity](#humidity)
    - [Io16](#io16)
      - [Consuming:](#consuming)
      - [Producing](#producing)
  - [Camel Pi4j component](#camel-pi4j-component)
    - [Maven dependency](#maven-dependency-3)
    - [URI format for GPIO](#uri-format-for-gpio)
        - [Optional URI Parameters](#optional-uri-parameters-1)
      - [Consuming:](#consuming-1)
      - [Producing](#producing-1)
      - [Simple button w/ LED mode](#simple-button-w-led-mode)
    - [URI format for I2C](#uri-format-for-i2c)
        - [Optional URI Parameters](#optional-uri-parameters-2)
        - [i2c driver](#i2c-driver)
  - [Camel PubNub component](#camel-pubnub-component)
    - [Maven dependency](#maven-dependency-4)
    - [General URI format](#general-uri-format-1)
        - [URI Parameters](#uri-parameters)
      - [Consuming:](#consuming-2)
      - [Producing](#producing-2)
- [Rhiot Cloud](#rhiot-cloud)
  - [Dockerized Rhiot Cloud](#dockerized-rhiot-cloud)
  - [Device management cloudlet](#device-management-cloudlet)
    - [Device management REST API](#device-management-rest-api)
- [Performance Testing Framework](#performance-testing-framework)
  - [Hardware profiles](#hardware-profiles)
    - [Raspberry PI 2 B+ (aka RPI2)](#raspberry-pi-2-b-aka-rpi2)
    - [Raspberry PI 2 B+ with BU353 (aka RPI2_BU353)](#raspberry-pi-2-b-with-bu353-aka-rpi2_bu353)
  - [Running the performance tester](#running-the-performance-tester)
  - [Analysis of the selected tests results](#analysis-of-the-selected-tests-results)
    - [Mock sensor to the external MQTT broker](#mock-sensor-to-the-external-mqtt-broker)
    - [Sample results for the RPI2 hardware kit](#sample-results-for-the-rpi2-hardware-kit)
- [Articles, presentations & videos](#articles-presentations-&-videos)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## Camel IoT Labs stack

The Camel IoT Labs stack is based on the following libraries and frameworks:

<br>

**IoT Gateway stack**

| Scope             | Libraries/Frameworks                      | 
|-------------------|-------------------------------------------|
| Device management | - [Eclipse Leshan](https://projects.eclipse.org/projects/iot.leshan) |
| Message routing   | - [Apache Camel](http://camel.apache.org) |
| Application framework | - [Vert.x](http://vertx.io)           |

<br>

**Cloudlet API stack**

| Scope                 | Libraries/Frameworks                      |
|-----------------------|-------------------------------------------|
| Device management     | - [Eclipse Leshan](https://projects.eclipse.org/projects/iot.leshan) |
| Message routing       | - [Apache Camel](http://camel.apache.org) |
| Application framework | - [Vert.x](http://vertx.io)           |

<br>

**Cloudlet Console stack**

| Scope                 | Libraries/Frameworks                      |
|-----------------------|-------------------------------------------|
| Application framework       | - [Hawt.io](http://hawt.io) |
| Application server       | - [Gulp](http://gulpjs.com) |


## Camel IoT gateway

Camel IoT gateway is the small fat jar application that can be installed into the field device. Gateway acts as a bridge
between the sensors and the data center. Under the hood, Camel IoT gateway is the fat jat running
[Vert.x](http://vertx.io) and Apache Camel.

### Installing gateway on the Raspbian

In order to install Camel IoT gateway on the Raspberry Pi running Raspbian, connect the device to your local network
(using WiFi or the ethernet cable) and execute the following command:

    docker run --net=host camellabs/deploy-gateway

From this point forward Camel IoT gateway will be installed on your device as `camel-iot-gateway` service and started
whenever the device boots up. Under the hood, gateway deployer performs the simple port scanning in the local network
and attempts to connect to the Raspian devices using the default SSH credentials.

To see all the options available for the gateway deployer, execute the following command:

    docker run --net=host camellabs/deploy-gateway --help

In case of problems with the gateway, you can try to run it in verbose mode (called *debug mode*):

    docker run --net=host camellabs/deploy-gateway --debug

You can also configure the gateway during the deployment process using the `-P` option. For example to set the configuration property
responsible for gateway heartbeats interval, execute the following command:

    docker run --net=host camellabs/deploy-gateway -Pcamellabs_iot_gateway_heartbeat_rate=10000

You can use the `-P` option multiple times:

    docker run --net=host camellabs/deploy-gateway -Pfoo=bar -Pbar=qux

If you would like to use different SSH credentials then default (username `pi`, password `raspberry`), then pass the
`--username` and `--password` options to the deployer:

    docker run --net=host camellabs/deploy-gateway --username=john --password=secret

### Configuration of the gateway

The gateway configuration file is `/etc/default/camel-labs-iot-gateway`. The latter file is loaded by the gateway 
starting script. It means that all the configuration environment variables can be added there. For example to set the
`foo_bar_baz` configuration property to value `qux`, the following environment variable should be added to the
`/etc/default/camel-labs-iot-gateway` file:

    export foo_bar_baz=qux

### Gateway logger configuration

By default gateway keeps the last 100 MB of the logging history. Logs are grouped by the days and split into the 
10 MB files. The default logging level is `INFO`. You can change it by setting the `camellabs_iot_gateway_log_root_level`
environment variable:

    export camellabs_iot_gateway_log_root_level=DEBUG

### Device heartbeats

Camel gateway generates heartbeats indicating that the device is alive and optionally connected to the data
center.

The default heartbeat rate is 5 seconds, which means that heartbeat events will be generated every 5 second. You
can change the heartbeat rate by setting the `camellabs_iot_gateway_heartbeat_rate` environment variable to the desired
number of the rate miliseconds. The snippet below demonstrates how to change the heartbeat rate to 10 seconds:

    export camellabs_iot_gateway_heartbeat_rate=10000
    
The heartbeat events are broadcasted to the Vert.x event bus address `heartbeat` (
`com.github.camellabs.iot.gateway.CamelIotGatewayConstants.BUS_HEARTBEAT` constant).

#### Logging heartbeat

By default Camel gateway sends the heartbeat event to the application log (at the `INFO` level). Logging heartbeats are
useful when verifying that gateway is still running - you can just take a look into the application log files. The name of the logger is `com.github.camellabs.iot.gateway.heartbeat.LoggingHeartbeatVerticle`
and the message is `Ping!`.

#### MQTT heartbeat

Camel IoT gateway can send the heartbeat events to the data center using the MQTT protocol. MQTT heartbeats are 
useful when verifying that gateway is still running and connected to the backend services deployed into the data
center.

In order to enable the 
MQTT-based heartbeats set the `camellabs.iot.gateway.heartbeat.mqtt` environment variable to `true`. Just as 
demonstrated on the snippet below:

    export camellabs.iot.gateway.heartbeat.mqtt=true

The address of the target MQTT broker can be set using the `camellabs.iot.gateway.heartbeat.mqtt.broker.url` environment 
variable, just as demonstrated on the example below:

    export camellabs.iot.gateway.heartbeat.mqtt.broker.url=tcp://mydatacenter.com
    
By default MQTT heartbeat sends events to the `heartbeat` topic. You can change the name of the topic using the
`camellabs.iot.gateway.heartbeat.mqtt.topic` environment variable. For example to send the heartbeats to the 
`myheartbeats` queue set the `camellabs.iot.gateway.heartbeat.mqtt.topic` environment variable as follows:

    export camellabs.iot.gateway.heartbeat.mqtt.topic=myheartbeats

The heartbeat message format is `hostname:timestamp`, where `hostname` is the name of the device host and `timestamp` is
the current time converted to the Java miliseconds.

#### LED heartbeat

Heartbeats can blink the LED node connected to the gateway device. This is great visual indication that the gateway
software is still up and running. For activating LED heartbeat set `camellabs.iot.gateway.heartbeat.led` property to `true`.
Like this

    export camellabs.iot.gateway.heartbeat.led=true
    
The LED output port can be set via `camellabs.iot.gateway.heartbeat.led.gpioId` environment variable, Default value is 0 *wiring lib pin index*
Change LED output port like this:

    export camellabs.iot.gateway.heartbeat.led.gpioId=11

Please add resistor (220 Ohms) between LED and Mass (0v) to avoid excessive current through it.

### Monitoring gateway with Jolokia

The gateway exposes its JMX beans using the [Jolokia REST API](https://jolokia.org). The default Jolokia URL for the
gateway is `http://0.0.0.0:8778/jolokia`. You can take advantage of the Jolokia to monitor and perform administrative
tasks on your gateway.

### Adding the custom code to the gateway

Camel IoT gateway comes with the set of predefined components and features that can be used out of the box. It is
however very likely that your gateway will execute some custom logic related to your business domain. This section of
the documentation covers how can you add the custom code to the gateway.

We highly recommend to deploy the gateway as a fat jar. This approach reduces the devOps cycles needed to deliver the
working solution. If you would like to add the custom logic to the gateway, we recommend to create the fat jar Maven
project including the gateway core dependency:

    <dependencies>
      <dependency>
        <groupId>com.github.camel-labs</groupId>
        <artifactId>camel-labs-iot-gateway</artifactId>
        <version>0.1.1</version>
      </dependency>
    </dependencies>

Now all your custom code can just be added to the project. The resulting fat jar will contain both gateway core logic
and your custom code.

#### Adding custom Groovy Camel verticle to the gateway

As the Rhiot gateway uses Vert.x event bus as its internal messaging core, the recommended option to add new Camel routes to the
gateway is to deploy those as the Vert.x verticle. The Vert.x helper classes for the gateway are available in the
following jar:

    <dependencies>
      <dependency>
        <groupId>io.rhiot</groupId>
        <artifactId>rhiot-vertx</artifactId>
        <version>0.1.1</version>
      </dependency>
    </dependencies>

In order to create Vert.x verticle that can access single `CamelContex` instance shared between all the verticles
within the given JVM, extend the `com.github.camellabs.iot.vertx.camel.GroovyCamelVerticle` class:

    @GatewayVerticle
    class HeartbeatConsumerVerticle extends GroovyCamelVerticle {

        @Override
        void start() {
            fromEventBus('heartbeat') { it.to('mock:camelHeartbeatConsumer') }
        }

    }

Rhiot gateway scans the classpath for the verticle classes marked with the `com.github.camellabs.iot.gateway.GatewayVerticle`
annotation. All those verticles are automatically loaded into the Vert.x backbone.

As you can see in the example above you can read the messages from the event bus and forward these to your Camel
route using the `fromEventBus(channel, closure(route))` method. You can also access the Camel context directly:

    @GatewayVerticle
    class HeartbeatConsumerVerticle extends GroovyCamelVerticle {

        @Override
        void start() {
            camelContext.addRoutes(new RouteBuilder(){
                @Override
                void configure() {
                    from('timer:test').to('seda:test')
                }
            })
        }

    }

## Camel IoT components

Camel IoT Labs brings some extra components for the Apache Camel intended to make both device- and server-side IoT
development easier.

### Camel GPS BU353 component

[BU353](http://usglobalsat.com/p-688-bu-353-s4.aspx#images/product/large/688_2.jpg) is one of the most popular and the 
cheapest GPS units on the market. It is connected to the device via the USB port. If you are looking for good and cheap
GPS receiver for your IoT solution, definitely consider purchsing this unit.

Camel GPS BU353 component can be used to read current GPS information from that device. With Camel GPS BU353 you can
just connect the receiver to your computer's USB port and read the GPS data - the component
will make sure that GPS daemon is up, running and
switched to the [NMEA mode](http://www.gpsinformation.org/dale/nmea.htm). The component also takes care of parsing the
NMEA data read from the serial port, so you can enjoy the `com.github.camellabs.iot.component.gps.bu353.ClientGpsCoordinates`
instances received by your Camel routes.

#### Maven dependency

Maven users should add the following dependency to their POM file:

    <dependency>
      <groupId>com.github.camel-labs</groupId>
      <artifactId>camel-gps-bu353</artifactId>
      <version>0.1.1</version>
    </dependency>

#### URI format

BU353 component supports only consumer endpoints. The BU353 consumer is the polling one, i.e. it periodically asks the GPS device for the
current coordinates. The Camel endpoint URI format for the B3353 consumer is as follows:

    gps-bu353:label
    
Where `label` can be replaced with any text label:

    from("gps-bu353:current-position").
      to("file:///var/gps-coordinates");
      
BU353 consumer receives the `com.github.camellabs.iot.component.gps.bu353.ClientGpsCoordinates` instances:

    ClientGpsCoordinates currentPosition = consumerTemplate.receiveBody("gps-bu353:current-position", ClientGpsCoordinates.class);

`ClientGpsCoordinates` class name is prefixed with the `Client` to indicate that these coordinates have been created on the device,
not on the server side of the IoT solution.

#### Options

| Option                   | Default value                                                                 | Description   |
|:-------------------------|:-----------------------------------------------------------------------       |:------------- |
| `consumer.initialDelay`  | 1000                                                                          | Milliseconds before the polling starts. |
| `consumer.delay`         | 5000 | Delay between each GPS scan. |
| `consumer.useFixedDelay` | false | Set to true to use a fixed delay between polls, otherwise fixed rate is used. See ScheduledExecutorService in JDK for details. |
| `coordinatesSource`   | `new SerialGpsCoordinatesSource()`                                               | reference to the`com.github.camellabs.iot.component.gps.bu353.GpsCoordinatesSource` instance used to read the current GPS coordinates. |

#### Process manager

Process manager is used by the BU353 component to execute Linux commands responsible for starting GPSD daemon or
configuring the GPS receive to provide GPS coordinates in the NMEA mode. If for some reason you would like to change
the default implementation of the process manager used by Camel (i.e. `com.github.camellabs.iot.utils.process.DefaultProcessManager`),
you can set it on the component level:

    GpsBu353Component bu353 = new GpsBu353Component();
    bu353.setProcessManager(new CustomProcessManager());
    camelContext.addComponent("gps-bu353", bu353);

If custom process manager is not set on the component, Camel will try to find the manager instance in the
[registry](http://camel.apache.org/registry.html). So for example for Spring application, you can just configure
the manager as the bean:

    @Bean
    ProcessManager myProcessManager() {
        new CustomProcessManager();
    }

Custom process manager may be useful if for some reasons your Linux distribution requires executing some unusual commands
in order to make the GPSD up and running.

### Camel Kura Wifi component

The common scenario for the mobile IoT Gateways, for example those mounted on the trucks or other vehicles, is to cache
collected data locally on the device storage and synchronizing the data with the data center only when trusted WiFi
access point is available near the gateway. Such trusted WiFi network could be localized near the truck fleet parking.
Using this approach, less urgent data (like GPS coordinates stored for the further offline analysis) can be delivered to 
the data center without the additional cost related to the GPS transmission fees.

<a href="https://github.com/camel-labs/camel-labs"><img src="images/wifi_truck_1.png" align="center" height="400" hspace="30"></a>
<br>
<a href="https://github.com/camel-labs/camel-labs"><img src="images/wifi_truck_2.png" align="center" height="400" hspace="30"></a>

Camel Kura WiFi component can be used to retrieve the information about the WiFi access spots available within the device
range. Under the hood Kura Wifi component uses Kura `org.eclipse.kura.net.NetworkService`. Kura WiFi component
supports both the consumer and producer endpoints.

#### Maven dependency

Maven users should add the following dependency to their POM file:

    <dependency>
      <groupId>com.github.camel-labs</groupId>
      <artifactId>camel-kura</artifactId>
      <version>0.0.0</version>
    </dependency>
    
#### URI format

    kura:networkInterface/ssid
    
Where both `networkInterface` and `ssid` can be replaced with the `*` wildcard matching respectively all the network 
interfaces and SSIDs.

For example to read all the SSID available near the device, the following route can be used:

    from("kura:*/*").to("mock:SSIDs");

The Kura WiFi consumer returns the list of the `org.eclipse.kura.net.wifi.WifiAccessPoint` classes returned as a result
of the WiFi scan:

    WifiAccessPoint[] accessPoints = consumerTemplate.receiveBody("kura:wlan0/*", WifiAccessPoint[].class);
    
You can also request the WiFi scanning using the producer endpoint:

    from("direct:WifiScan").to("kura-wifi:*/*").to("mock:accessPoints");
    
Or using the producer template directly:
 
    WifiAccessPoint[] accessPoints = template.requestBody("kura-wifi:*/*", null, WifiAccessPoint[].class);


#### Options

| Option                    | Default value                                                                 | Description   |
|:------------------------- |:-----------------------------------------------------------------------       |:------------- |
| `accessPointsProvider`    | `com.github.camellabs.iot.component.` `kura.wifi.KuraAccessPointsProvider`    | `com.github.camellabs.iot.component.kura.` `wifi.AccessPointsProvider` strategy instance registry reference used to resolve the list of the access points available to consume. |
| `consumer.initialDelay`   | 1000                                                                          | Milliseconds before the polling starts. |
| `consumer.delay`          | 500 | Delay between each access points scan. |
| `consumer.useFixedDelay`  | false | Set to true to use a fixed delay between polls, otherwise fixed rate is used. See ScheduledExecutorService in JDK for details. |

#### Detecting Kura NetworkService

In the first place `com.github.camellabs.iot.component.kura.wifi.KuraAccessPointsProvider` tries to locate `org.eclipse.kura.net.NetworkService`
in the Camel registry. If exactly one instance of the `NetworkService`  is found (this is usually the case when
if you deploy the route into the Kura container), that instance will be used by the Kura component. Otherwise new instance of the
`org.eclipse.kura.linux.net.NetworkServiceImpl` will be created and cached by the `KuraAccessPointsProvider`.

### Camel TinkerForge component

The Camel Tinkerforge component can be used to connect to the TinkerForge brick deamon.

#### Maven dependency

Maven users should add the following dependency to their POM file:

    <dependency>
      <groupId>com.github.camel-labs</groupId>
      <artifactId>camel-tinkerforge</artifactId>
      <version>0.0.0</version>
    </dependency>

#### General URI format

    tinkerforge:/<brickletType>/<uid>[?parameter=value][&parameter=value]

By default a connection is created to the brickd process running on localhost using no authentication.
If you want to connect to another host use the following format:

    tinkerforge://[username:password@]<host>[:port]/<brickletType>/<uid>[?parameter=value][&parameter=value]

The following values are currently supported as brickletType:

* ambientlight
* temperature
* lcd20x4
* humidity
* io4
* io16
* distance
* ledstrip
* motion
* soundintensity
* piezospeaker
* linearpoti
* rotarypoti
* dualrelay
* solidstaterelay

##### Ambientlight

    from("tinkerforge:/ambientlight/al1")
    .to("log:default");

##### Temperature

    from("tinkerforge:/temperature/T1")
    .to("log:default");

##### Lcd20x4

The LCD 20x4 bricklet has a character based screen that can display 20 characters on 4 rows.

###### Optional URI Parameters

| Parameter | Default value | Description                              |
|-----------|---------------|------------------------------------------|
| line      | 0             | Show message on line 0                   |
| position  | 0             | Show message starting at position 0      |

    from("tinkerforge:/temperature/T1
    .to("tinkerforge:/lcd20x4/lcd1?line=2&position=10

The parameters can be overridden for individual messages by settings them as headers on the exchange:

    from("tinkerforge:/temperature/T1
    .setHeader("line", constant("2"))
    .setHeader("position", constant("10"))
    .to("tinkerforge:/lcd20x4/lcd1");

#### Humidity

     from("tinkerforge:/humidity/H1")
     .to("log:default");

#### Io16

The IO16 bricklet has 2 ports (A and B) which both have 8 IO pins. Consuming and producing
messages happens on port level. So only the port can be specified in the URI and the pin will
be a header on the exchange.

##### Consuming:

    from("tinkerforge:/io16/io9?ioport=a")
    .to("log:default?showHeaders=true");

##### Producing

    from("timer:default?period=2000")
    .setHeader("iopin", constant(0))
    .setHeader("duration", constant(1000))
    .setBody(constant("on"))
    .to("tinkerforge:/io16/io9?ioport=b");

### Camel Pi4j component

Camel Pi4j component can be used to manage GPIO and I2C bus features from Raspberry Pi.
This component uses [pi4j](http://pi4j.com) library

#### Maven dependency

Maven users should add the following dependency to their POM file:

    <dependency>
      <groupId>com.github.camel-labs</groupId>
      <artifactId>camel-pi4j</artifactId>
      <version>0.0.0</version>
    </dependency>

#### URI format for GPIO

    pi4j-gpio://gpioId[?options]

*gpioId* must match [A-Z_0-9]+ pattern.
By default, pi4j-gpio uses *RaspiPin* Class, change it via *gpioClass* property
You can use static field name "*GPIO_XX*", pin name "*GPIO [0-9]*" or pin address "*[0-9]*" 


###### Optional URI Parameters

| Parameter            | Default value             | Description                                               |
|----------------------|---------------------------|-----------------------------------------------------------|
| `gpioId`               |                           |                                                           |
| `state`                |                           | Digital Only: if input mode then state trigger event, if output then started value                       |
| `mode`                 | `DIGITAL_OUTPUT`            | To configure GPIO pin mode, Check Pi4j library for more details                     |
| `action`               |                           | Default : use Body if Action for output Pin (TOGGLE, BUZZ, HIGH, LOW for digital only) (HEADER digital and analog) |
| `value`                | `0`                         | Analog or PWN Only                       |
| `shutdownExport`       | `true`                      | To configure the pin shutdown export                      |
| `shutdownResistance`   | `OFF`                       | To configure the pin resistance before exit program                      |
| `shutdownState`        | `LOW`                       | To configure the pin state value before exit program                      |
| `pullResistance`       | `PULL_UP`                   | To configure the input pull resistance, Avoid strange value for info http://en.wikipedia.org/wiki/Pull-up_resistor                     |
| `gpioClass`            | `com.pi4j.io.gpio.RaspiPin` | `class<com.pi4j.io.gpio.Pin>` pin implementation                  |
| `controller`           | `com.pi4j.io.gpio.impl.GpioControllerImpl`            | `instance of <com.pi4j.io.gpio.GpioController>` GPIO controller instance, check gpioClass pin implementation to use the same  |

##### Consuming:

    from("pi4j-gpio://13?mode=DIGITAL_INPUT&state=LOW")
    .to("log:default?showHeaders=true");

##### Producing

    from("timer:default?period=2000")
    .to("pi4j-gpio://GPIO_04?mode=DIGITAL_OUTPUT&state=LOW&action=TOGGLE");
    
    
##### Simple button w/ LED mode

Plug an button on GPIO 1, and LED on GPIO 2 (with Resistor) and code a route like this

    from("pi4j-gpio://1?mode=DIGITAL_INPUT&state=HIGH").id("switch-led")
    .to("pi4j-gpio://2?&action=TOGGLE");


#### URI format for I2C

    pi4j-i2c://busId/deviceId[?options]

###### Optional URI Parameters

| Parameter            | Default value             | Description                                               |
|----------------------|---------------------------|-----------------------------------------------------------|
| `busId`              |                           | i2c bus                                                   |
| `deviceId`           |                           | i2c device                                                |
| `address`            |  `0x00`                   | address to read                                           |
| `readAction`         |                           | READ, READ_ADDR, READ_BUFFER, READ_ADDR_BUFFER            |
| `size`               |  `-1`                     |                                                           |
| `offset`             |  `-1`                     |                                                           |
| `bufferSize`         |  `-1`                     |                                                           |
| `driver`             |                           | cf available i2c driver                                   |

i2c component is realy simplist, for consumer endpoint you can just read byte or buffer byte,
for producer one you can 

for smarter device, you must implement an driver 

###### i2c driver

| Driver            | Feature                                                            |
|-------------------|--------------------------------------------------------------------|
| bmp180            | Temp and Pressure sensor   (http://www.adafruit.com/products/1603) |
| tsl2561           | Light sensor            (http://www.adafruit.com/products/439)     |
| lsm303-accel      | Accelerometer sensor    (http://www.adafruit.com/products/1120)    |
| lsm303-magne      | Magnetometer sensor     (http://www.adafruit.com/products/1120)    |
| mcp23017-lcd      | LCD 2x16 char           (http://www.adafruit.com/products/1109)    |



### Camel PubNub component

Camel PubNub component can be used to communicate with the [PubNub](http://www.pubnub.com) data stream network for connected devices. 
This component uses [pubnub](https://www.pubnub.com/docs/java/javase/javase-sdk.html) library

#### Maven dependency

Maven users should add the following dependency to their POM file:

    <dependency>
      <groupId>com.github.camel-labs</groupId>
      <artifactId>camel-pubnub</artifactId>
      <version>0.0.0</version>
    </dependency>

#### General URI format

    pubnub://<pubnubEndpointType>:channel[?options]

The following values are currently supported as pubnubEndpointType:

* pubsub
* presence

###### URI Parameters

| Option                    | Default value                                                                 | Description   |
|:------------------------- |:-----------------------------------------------------------------------       |:------------- |
| `publisherKey`            |                      | The punub publisher key optained from pubnub. Mandatory for publishing events              |
| `subscriberKey`           |                      | The punub subsciber key optained from pubnub. Mandatory when subscribing to events         |
| `secretKey`               |                      | The pubnub secret key.
| `ssl`                     | true                 | Use SSL transport. |
| `uuid`                    |                      | The uuid identifying the connection. If not set it will be auto assigned |
| `operation`               | PUBLISH              | Producer only. The operation to perform when publishing events or ad hoc querying pubnub. Valid values are HERE_NOW, WHERE_NOW, GET_STATE, SET_STATE, GET_HISTORY, PUBLISH |

Operations can be used on the producer endpoint, or as a header:

| Operation                 | Description   |
|:------------------------- |:------------- |
| `PUBLISH`                 | Publish a message to pubnub. The message body shold contain a instance of  `org.json.JSONObject` or `org.json.JSONArray`. Otherwise the message is expected to be a string.
| `HERE_NOW`                | Read presence (Who's online) information from the endpoint channel.|  
| `WHERE_NOW`               | Read presence information for the uuid on the endpoint. You can override that by setting the header `CamelPubNubUUID` to another uuid. | 
| `SET_STATE`               | Set the state by uuid. The message body should contain a instance of `org.json.JSONObject` with any state information. By default the endpoint uuid is updated, but you can override that by setting the header `CamelPubNubUUID` to another uuid. |
| `GET_STATE`               | Get the state object `org.json.JSONObject` by for the endpoint uuid. You can override that by setting the `CamelPubNubUUID` header to another uuid. |
| `GET_HISTORY`             | Gets the message history for the endpoint channel. | 


##### Consuming:

Route that consumes messages from mychannel:

    from("pubnub://pubsub:mychannel?uuid=master&subscriberKey=mysubkey").routeId("my-route")
    .to("log:default?showHeaders=true");
    
Route that listens for presence (eg. join, leave, state change) events on a channel

    from("pubnub://presence:mychannel?subscriberKey=mysubkey").routeId("presence-route")
    .to("log:default?showHeaders=true");

##### Producing

Route the collect data and sendt it to pubnub channel mychannel:

    from("timer:default?period=2000").routeId("device-event-route")
    .bean(EventGeneratorBean.class, "getEvent()")
    .convertBodyTo(JSONObject.class)
    .to("pubnub://pubsub:mychannel?uuid=deviceuuid&publisherKey=mypubkey");

## Rhiot Cloud

Rhiot Cloud is the set of the backend (micro)services and UI application used to managed these.

*Cloudlets* are server-side microservices that come with some common functionalities required by the IoT systems. *Cloudlets
UI plugins* are [Hawt.io](http://hawt.io)-based plugins which provides nice web UI for the cloudlets back-end services. *Cloudlet
Console* is the web application assembling all the Cloudlets UI plugins. The *Rhiot Cloud* then is the
complete cloud-based installation setup including Cloudlet Console, Cloudlets backend services and all the other necessary
services (like database servers) installed.

### Dockerized Rhiot Cloud

We recommend to run the Rhiot Cloud using the Docker container. We love Docker and believe that containers are the
future of the applications deployment. To install the Rhiot Cloud on the Linux server of your choice, just execute the
following command:

    GOOGLE_OAUTH_CLIENT_ID=foo.apps.googleusercontent.com \
    GOOGLE_OAUTH_CLIENT_SECRET=yourSecret \
    GOOGLE_OAUTH_REDIRECT_URI=http://myapp.com \
      bash <(curl -s https://raw.githubusercontent.com/rhiot/rhiot/master/iot/cloudlet/deployment/rhiot-cloud.sh)

The script above installs the proper version of Docker server. Keep in mind that the minimal Docker version required by
Rhiot Cloud is 1.7.1 - if the older version of the Docker is installed, our script will upgrade your Docker server. After
Docker server is properly installed, our script downloads and starts the Cloudlet Console, device management cloudlet,
geofencing cloudlet and MongoDB server containers.

By default Rhiot Cloud runs the console UI using the development HTTP port 9000. If you want to change it, use the `HTTP_PORT`
environment variable:

    HTTP_PORT=80 \
      ...
      bash <(curl -s https://raw.githubusercontent.com/rhiot/rhiot/master/iot/cloudlet/deployment/rhiot-cloud.sh)

Environment variables starting with `GOOGLE_OAUTH` prefix are used to configure the Google OAuth authentication
used by the Cloudlet Console. You have to create the Google application in the
[Developers Console](https://console.developers.google.com) to get your client identifier, secret and configure the
accepted redirect URIs. If `GOOGLE_OAUTH_REDIRECT_URI` variable is net given, `http://localhost:9000` will be used.

Rhiot Cloud relies on the MongoDB to store some of the data processed by it. For example MongoDB backend is the default
store used by the device management cloudlet's Leshan server. By default the MongoDB data is stored in the `mongodb_data`
volume container. If such volume doesn't exist, Rhiot Cloud script will create it for you.

### Device management cloudlet

Device management cloudlet provides backend service for registering and tracking devices connected to the Rhiot Cloud.
Under the hood device management cloudlet uses [Eclipse Leshan](https://projects.eclipse.org/projects/iot.leshan), the
open source implementation of the [LWM2M](https://en.wikipedia.org/wiki/OMA_LWM2M) protocol.

The device management cloudlet is distributed as a fat jar. Its Maven coordinates are
`io.rhiot/rhiot-cloudlet-device/0.1.1`. The dockerized artifact is available in Docker Hub as
[rhiot/cloudlet-device:0.1.1](https://hub.docker.com/r/rhiot/cloudlet-device).

#### Device management REST API

The device management cloudlet exposes REST API that can be used to work with the devices. By default the device
management REST API is exposed using the following base URI - `http:0.0.0.0:15000`.

To list the devices send the `GET` request to the following URL `http:localhost:15000/device`. You should receive
response similar to the following JSON:

    {"devices":
      [{"registrationDate":1439822565254,
      "address":"127.0.0.1",
      "port":1103,
      "registrationEndpointAddress":"0.0.0.0:5683",
      "lifeTimeInSec":86400,
      "lwM2mVersion":"1.0",
      "bindingMode":"U",
      "endpoint":"f4650db1-01e7-49e0-a8d7-da6217213907",
      "registrationId":"7OjdvHCVUb",
      "objectLinks":[{"url":"/",
        "attributes":{"rt":"oma.lwm2m"},
        "path":"/"},
        ...],
      "alive":true}]}

To return the list of identifiers of the disconnected devices send the `GET` request to the following URL -
`http:localhost:15000/device/disconnected`. In the response you will receive the list of the identifiers of the devices
that have not send the heartbeat signal to the device management cloudlet for the given *disconnection period* (one minute by
default). The list will be formatted as the JSON document similar to the following one:

    {"disconnectedDevices": ["device1", "device2", ...]}

#### Device registry

Device registry is used by Leshan to store the information about the managed devices. By default the device cloudlet uses
the MongoDB registry. If environment variables `mongodb_host` are no specified, the cloudlet will try to
connect to the `mongodb` and `localhost` hosts respectively, using default MongoDB port (`27017`) or the one specified by
the `mongodb_port` environment variable.


## Performance Testing Framework

The key part of the process of tailoring the perfect IoT solution is choosing the proper hardware for the gateway device.
In general the more expensive gateway hardware is, the more messages per second you can process. However the more
expensive the gateway device is, the less affordable your IoT solution becomes for the end client. That is the main
reason why would you like to have a proper tool for measuring the given IoT messages flow scenario in the unified way,
on multiple devices.

Camel IoT Labs comes with the *Performance testing framework* that can be used to define the hardware profile and
test scenarios. Performance framework takes care of detecting the devices connected to your local network, deploying the
test application into these, executing the actual tests and generating the results as the human-readable chart.
For example the sample output for the MQTT QOS testing could generate the following diagram:

<img src="images/sample_perf_chart.png" align="center" height="400" hspace="30">

Performance Testing Framework excels when you would like to answer the following question - how the different field hardware setups
perform against the given task. Just connect your devices to the local network, execute the performance testing application
and compare the generated diagrams.

### Hardware profiles

This section covers the *hardware profiles* for the performance tests. Profiles are used to describe the particular
hardware configuration that can be used as a target device for the performance benchmark. Every performance test
definition can be executed on the particular hardware profiles.

#### Raspberry PI 2 B+ (aka RPI2)

The `RPI2` hardware profile is just the Raspberry Pi 2 B+ model equipped with the network connector (WiFi adapter or
the ethernet cable). Currently we assume that the device is running [Raspbian](https://www.raspbian.org/) operating
system (version 2015-05-05).

<img src="images/rpi2_open.jpg" align="center" height="600" hspace="30">
<img src="images/rpi2_closed.jpg" align="center" height="600" hspace="30">

#### Raspberry PI 2 B+ with BU353 (aka RPI2_BU353)

The `RPI2_BU353` hardware profile is the same as `RPI2` profile, but additionally equipped with the
[BU353 GPS receiver](http://usglobalsat.com/p-688-bu-353-s4.aspx#images/product/large/688_2.jpg)
plugged into the USB port.

<img src="images/rpi2_bu353_open.jpg" align="center" height="500" hspace="30">
<img src="images/rpi2_bu353_closed.jpg" align="center" height="500" hspace="30">

### Running the performance tester

The easiest way to run the performance benchmark is to connect the target device (for example Rapsberry Pi) into your
local network (for example via the WiFi or the Ethernet cable) and start the tester as a Docker container, using the
following command:

    docker run -v=/tmp/gateway-performance:/tmp/gateway-performance --net=host camellabs/performance-of RPI2

Keep in mind that `RPI2` can be replaced with the other supported hardware profile (like `RPI2_BU353`). The performance
tester detects the tests that can be executed for the given hardware profile, deploy the gateway software to the target
device, executes the tests and collects the results.

When the execution
of the benchmark ends, the result diagrams will be located in the `/tmp/gateway-performance` directory (or any other
directory you specified when executing the command above). The sample diagram may look as follows:

<a href="https://github.com/camel-labs/camel-labs/iot"><img src="images/sample_perf_chart.png" align="center" height="400" hspace="30"></a>

Keep in mind that currently we assume that your Raspberry Pi has the default Raspbian SSH account available (username: *pi* / password: *raspberry*).

### Analysis of the selected tests results

Below you can find some performance benchmarks with the comments regarding the resulted numbers.

#### Mock sensor to the external MQTT broker

In this test we generate mock sensor events in the gateway using the timer trigger. The message is the random UUID encoded
to the array of bytes. Then we send those messages to the
external MQTT broker. We test and compare various MQTT QOS levels. The MQTT client used to send the messages is
[Eclipse Paho](https://www.eclipse.org/paho/).

#### Sample results for the RPI2 hardware kit

<img src="images/RPI2 Mock sensor to external MQTT broker.png" align="center" height="500" hspace="30">

The very first question that comes to the mind when you look at these benchmarks is why there is so huge difference between
the MQTT QOS level 0 and the other QOS levels? The reason is that currently Eclipse Paho client doesn't work well with
QOS greater than 0 and the high messages load. The reason for that is that Paho client enforces inflight messages limit to 10. This
is pretty restrictive treshold considering that MQTT client should have more time for receiving the acknowledgement from the
MQTT server. Such acknowledgement is required for the MQTT QOS levels greater than 0. Waiting for the acknowledge reply
from the server increases the number of the inflight messages hold by the Paho client. As a result Paho client
throughput for QOS 1 and 2 is limited for the extremely large number of messages.

Regardless of the current Paho limits (that are very likely to be changed in the future), the overall performance of the
MQTT client is really great. As the majority of the gateway solutions can safely uses the QOS 0 for forwarding the data
from the field to the data center (as losing the single message from the stream of the sensors data, is definitely acceptable).
Almost 1800 messages per second for QOS 0 and around 500 messages per second for the highest QOS 2 is really good result
considering the class of the Raspberry Pi 2 hardware.

## Articles, presentations & videos

Here is the bunch of useful resources regarding Camel IoT project:
- [Make Your IoT Gateway WiFi-Aware Using Camel and Kura](http://java.dzone.com/articles/make-your-iot-gateway-wifi) - DZone article by Henryk Konsek (2015)
- [IoT gateway dream team - Eclipse Kura and Apache Camel](http://www.slideshare.net/hekonsek/io-t-gateway-dream-team-eclipse-kura-and-apache-camel) - slides from the Henryk Konsek talk for Eclipse IoT Virtual Meetup (2015)
- [IoT gateway dream team - Eclipse Kura and Apache Camel](https://www.youtube.com/watch?v=mli5c-oTN1U) - video from the Henryk Konsek talk for Eclipse IoT Virtual Meetup (2015)
- [Apache Camel & RaspberryPi PoC w/ GPIO & LED & Button](http://gautric.github.io/blog/2015/04/03/apache-camel-raspberrypi-integration.html) - Greg's blog post (video included) (April 2015) 
- [Using Camel & Tinkerforge in Jboss Fuse](https://www.youtube.com/watch?v=J1hN9NLLbro) - Interview with Geert, includes live demo of Camel loadbalancer via RGB Led Strip (October 2014)
- [Camel IoT Labs i2c gpio mqtt lcd](http://gautric.github.io/blog/2015/05/20/camel-iot-labs-i2c-gpio-mqtt-lcd.html) - Greg's blog post (video included) (may 2015)
- [Running Camel-Tinkerforge on Karaf](https://geertschuring.wordpress.com/2015/05/25/running-camel-tinkerforge-on-karaf/) - Blogpost describing how to install and run camel-tinkerforge on Karaf. Geerts blog (may 2015)
- [Over-the-Air Runtime Updates of the IoT Gateways](http://java.dzone.com/articles/over-air-runtime-updates-iot) - DZone article by Henryk Konsek (2015)
- [Where Am I? Collecting GPS Data With Apache Camel](https://dzone.com/articles/where-am-i-collecting-gps-data-with-apache-camel) - DZone article by Henryk Konsek (2015)
- [Let's start the Rhiot](http://henryk-konsek.blogspot.com/2015/07/lets-start-rhiot.html) - blog post by Henryk Konsek (2015)