/*
 * Copyright 2021 The Dapr Authors
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
limitations under the License.
*/

package io.dapr.actors.client;

import io.dapr.client.DaprApiProtocol;
import io.dapr.client.DaprClientConfig;
import io.dapr.client.DaprHttpBuilder;
import io.dapr.client.resiliency.ResiliencyOptions;
import io.dapr.config.Properties;
import io.dapr.utils.NetworkUtils;
import io.dapr.v1.DaprGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

/**
 * Holds a client for Dapr sidecar communication. ActorClient should be reused.
 */
public class ActorClient implements AutoCloseable {

  private static final Logger LOGGER = LoggerFactory.getLogger(ActorClient.class);

  /**
   * Properties for configuring the interaction with the Dapr sidecar.
   */
  private final DaprClientConfig daprClientConfig;

  /**
   * gRPC channel for communication with Dapr sidecar.
   */
  private final ManagedChannel grpcManagedChannel;

  /**
   * Dapr's client.
   */
  private final DaprClient daprClient;

  /**
   * Instantiates a new channel for Dapr sidecar communication.
   */
  public ActorClient() {
    this(DaprClientConfig.builder()
            .apiToken(Properties.API_TOKEN.get())
            .endpoint(Properties.GRPC_ENDPOINT.get())
            .maxRetries(Properties.MAX_RETRIES.get())
            .timeout(Properties.TIMEOUT.get())
            .build());
  }

  /**
   * Instantiates a new channel for Dapr sidecar communication.
   *
   * @param resiliencyOptions Client resiliency options.
   */
  public ActorClient(ResiliencyOptions resiliencyOptions) {
    this(DaprClientConfig.builder()
            .apiToken(Properties.API_TOKEN.get())
            .endpoint(Properties.GRPC_ENDPOINT.get())
            .maxRetries(resiliencyOptions.getMaxRetries())
            .timeout(resiliencyOptions.getTimeout())
            .build());
  }

  /**
   * Instantiates a new channel for Dapr sidecar communication.
   *
   * @param daprClientConfig Properties for configuring the interaction with the Dapr sidecar.
   */
  public ActorClient(DaprClientConfig daprClientConfig) {
    this(Properties.API_PROTOCOL.get(), daprClientConfig);
  }

  /**
   * Instantiates a new channel for Dapr sidecar communication.
   *
   * @param apiProtocol    Dapr's API protocol.
   * @param daprClientConfig Properties for configuring the interaction with the Dapr sidecar.
   */
  private ActorClient(DaprApiProtocol apiProtocol, DaprClientConfig daprClientConfig) {
    this(apiProtocol, buildManagedChannel(apiProtocol, daprClientConfig.getEndpoint()), daprClientConfig);
  }

  /**
   * Instantiates a new channel for Dapr sidecar communication.
   *
   * @param apiProtocol    Dapr's API protocol.
   * @param grpcManagedChannel gRPC channel.
   * @param daprClientConfig Properties for configuring the interaction with the Dapr sidecar.
   */
  private ActorClient(
      DaprApiProtocol apiProtocol,
      ManagedChannel grpcManagedChannel,
      DaprClientConfig daprClientConfig) {
    this.grpcManagedChannel = grpcManagedChannel;
    this.daprClient = buildDaprClient(apiProtocol, grpcManagedChannel, daprClientConfig);
    this.daprClientConfig = daprClientConfig;
  }

  /**
   * Invokes an Actor method on Dapr.
   *
   * @param actorType   Type of actor.
   * @param actorId     Actor Identifier.
   * @param methodName  Method name to invoke.
   * @param jsonPayload Serialized body.
   * @return Asynchronous result with the Actor's response.
   */
  Mono<byte[]> invoke(String actorType, String actorId, String methodName, byte[] jsonPayload) {
    return daprClient.invoke(actorType, actorId, methodName, jsonPayload);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void close() {
    if (grpcManagedChannel != null && !grpcManagedChannel.isShutdown()) {
      grpcManagedChannel.shutdown();
    }
  }

  /**
   * Creates a GRPC managed channel (or null, if not applicable).
   *
   * @param apiProtocol Dapr's API protocol.
   * @return GRPC managed channel or null.
   */
  private static ManagedChannel buildManagedChannel(DaprApiProtocol apiProtocol, String grpcEndpoint) {
    if (apiProtocol != DaprApiProtocol.GRPC) {
      return null;
    }

    return NetworkUtils.buildGrpcManagedChannel(grpcEndpoint);
  }

  /**
   * Build an instance of the Client based on the provided setup.
   *
   * @return an instance of the setup Client
   * @throws java.lang.IllegalStateException if any required field is missing
   */
  private static DaprClient buildDaprClient(
          DaprApiProtocol apiProtocol,
          Channel grpcManagedChannel,
          DaprClientConfig daprClientConfig) {
    switch (apiProtocol) {
      case GRPC: return new DaprGrpcClient(DaprGrpc.newStub(grpcManagedChannel),
              new ResiliencyOptions(daprClientConfig.getTimeout(), daprClientConfig.getMaxRetries()));
      case HTTP: {
        LOGGER.warn("HTTP client protocol is deprecated and will be removed in Dapr's Java SDK version 1.10.");
        return new DaprHttpClient(new DaprHttpBuilder().build());
      }
      default: throw new IllegalStateException("Unsupported protocol: " + apiProtocol.name());
    }
  }
}
