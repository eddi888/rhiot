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
package com.github.camellabs.iot.deployer

class ConsoleInputParser {

    String[] args

    ConsoleInputParser(String[] args) {
        this.args = args
    }

    boolean isHelp() {
        args.contains('--help') || args.contains('-h')
    }

    String helpText() {
        """Welcome to the Camel IoT Labs Deployer application.

Usage: docker run -t camellabs/deployer

Options:
 -h --help                      Prints this help page.
 -d --debug                     Debug (verbose) mode.
 -Pfoo=bar                      Adds foo=bar configuration property to the deployed gateway. Can be used multiple times.
 -u=user --username=user        SSH username of the target device.
 -p=secret --password=secret    SSH password of the target device."""
    }

    boolean isDebug() {
        args.contains('--debug') || args.contains('-d')
    }

    Map<String, String> properties() {
        args.findAll { it.startsWith('-P') }.inject([:]) { props, propertyArgument ->
            def propertyWithoutPrefix = propertyArgument.substring(2)
            def separatorIndex = propertyWithoutPrefix.indexOf('=')
            def propertyKey = propertyWithoutPrefix.substring(0, separatorIndex)
            def propertyValue = propertyWithoutPrefix.substring(separatorIndex + 1)
            props[propertyKey] = propertyValue
            props
        }
    }

    boolean hasCredentials() {
        def hasUsername = args.find{ it.startsWith('--username=')} || args.find{ it.startsWith('-u=')}
        def hasPassword = args.find{ it.startsWith('--password=')} || args.find{ it.startsWith('-p=')}
        if(hasUsername ^ hasPassword) {
            throw new ConsoleInformation('Both username and password must be specified.')
        }
        hasUsername && hasPassword
    }

    String username() {
        def argument = args.find{it.startsWith('--username=') || it.startsWith('-u=')}
        argument.substring(argument.indexOf('=') + 1)
    }

    String password() {
        def argument = args.find{it.startsWith('--password=') || it.startsWith('-p=')}
        argument.substring(argument.indexOf('=') + 1)
    }

}
