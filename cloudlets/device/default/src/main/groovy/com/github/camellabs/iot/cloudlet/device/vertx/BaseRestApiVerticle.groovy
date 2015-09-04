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
package com.github.camellabs.iot.cloudlet.device.vertx

import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.http.HttpMethod
import io.vertx.groovy.core.eventbus.Message
import io.vertx.groovy.core.http.HttpServer
import io.vertx.groovy.core.http.HttpServerResponse
import io.vertx.groovy.ext.web.Router
import io.vertx.groovy.ext.web.RoutingContext
import io.vertx.groovy.ext.web.handler.CorsHandler
import io.vertx.lang.groovy.GroovyVerticle

import static com.github.camellabs.iot.vertx.PropertyResolver.intProperty
import static io.rhiot.vertx.jackson.Jacksons.json
import static io.vertx.core.http.HttpMethod.DELETE
import static io.vertx.core.http.HttpMethod.GET
import static io.vertx.core.http.HttpMethod.OPTIONS
import static io.vertx.core.http.HttpMethod.POST
import static io.vertx.groovy.ext.web.Router.router

abstract class BaseRestApiVerticle extends GroovyVerticle {

    protected HttpServer http

    protected Router router

    protected Closure restApi

    @Override
    void start(Future<Void> startFuture) {
        vertx.runOnContext {
            http = vertx.createHttpServer()
            router = router(vertx)

            router.route().handler(new HttpRequestInterceptorHandler())

            router.route().handler(CorsHandler.create('*').
                    allowedMethod(GET).allowedMethod(OPTIONS).allowedHeader('Authorization'))

            http.requestHandler(router.&accept).listen(intProperty('api_rest_port', 15000))

            restApi(this)

            startFuture.complete()
        }
    }

    protected restApi(Closure restApi) {
        this.restApi = restApi
    }

    // REST DSL

    def forMethods(String uri, String channel, HttpMethod... method) {
        def route = router.route(uri)
        method.each { route.method(it) }
        route.handler { rc ->
            String parameter = null
            if(rc.request().params().size() == 1) {
                def parameterName = rc.request().params().names().first()
                parameter = rc.request().getParam(parameterName)
            }
            vertx.eventBus().send(channel, parameter, { result -> jsonResponse(rc, result) })
        }
    }

    def get(String uri, String channel) {
        forMethods(uri, channel, GET)
    }

    def post(String uri, String channel) {
        forMethods(uri, channel, POST)
    }

    def delete(String uri, String channel) {
        forMethods(uri, channel, DELETE)
    }

    // Helpers

    static HttpServerResponse jsonResponse(RoutingContext routingContext) {
        routingContext.response().putHeader("content-type", "application/json")
    }

    protected void jsonResponse(RoutingContext routingContext, AsyncResult<Message> message) {
        if(message.succeeded()) {
            jsonResponse(routingContext).end(message.result().body().toString())
        } else {
            jsonResponse(routingContext).end(json().writeValueAsString([failure: message.cause().message]))
        }
    }

}
