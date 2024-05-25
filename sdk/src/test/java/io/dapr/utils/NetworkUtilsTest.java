/*
 * Copyright 2024 The Dapr Authors
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

package io.dapr.utils;

import io.dapr.config.Properties;
import io.grpc.ManagedChannel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NetworkUtilsTest {
  private final int defaultGrpcPort = 4000;
  private final String defaultSidecarIP = "127.0.0.1";

  private ManagedChannel channel;

  @BeforeEach
  public void setUp() {
    System.setProperty(Properties.GRPC_PORT.getName(), Integer.toString(defaultGrpcPort));
    System.setProperty(Properties.SIDECAR_IP.getName(), defaultSidecarIP);
    System.setProperty(Properties.GRPC_ENDPOINT.getName(), "");
  }

  @AfterEach
  public void tearDown() {
    if (channel != null && !channel.isShutdown()) {
      channel.shutdown();
    }
  }

  @Test
  public void testBuildGrpcManagedChannel() {
    channel = NetworkUtils.buildGrpcManagedChannel();

    String expectedAuthority = String.format("%s:%s", defaultSidecarIP, defaultGrpcPort);
    Assertions.assertEquals(expectedAuthority, channel.authority());
  }

  @Test
  public void testBuildGrpcManagedChannel_httpEndpointNoPort() {
    System.setProperty(Properties.GRPC_ENDPOINT.getName(), "http://example.com");
    channel = NetworkUtils.buildGrpcManagedChannel();

    String expectedAuthority = "example.com:80";
    Assertions.assertEquals(expectedAuthority, channel.authority());
  }

  @Test
  public void testBuildGrpcManagedChannel_httpEndpointWithPort() {
    System.setProperty(Properties.GRPC_ENDPOINT.getName(), "http://example.com:3000");
    channel = NetworkUtils.buildGrpcManagedChannel();

    String expectedAuthority = "example.com:3000";
    Assertions.assertEquals(expectedAuthority, channel.authority());
  }

  @Test
  public void testBuildGrpcManagedChannel_httpsEndpointNoPort() {
    System.setProperty(Properties.GRPC_ENDPOINT.getName(), "https://example.com");
    channel = NetworkUtils.buildGrpcManagedChannel();

    String expectedAuthority = "example.com:443";
    Assertions.assertEquals(expectedAuthority, channel.authority());
  }

  @Test
  public void testBuildGrpcManagedChannel_httpsEndpointWithPort() {
    System.setProperty(Properties.GRPC_ENDPOINT.getName(), "https://example.com:3000");
    channel = NetworkUtils.buildGrpcManagedChannel();

    String expectedAuthority = "example.com:3000";
    Assertions.assertEquals(expectedAuthority, channel.authority());
  }

  @Test
  public void testBuildGrpcManagedChannel_endpointArgument() {
    channel = NetworkUtils.buildGrpcManagedChannel("http://example.com");

    String expectedAuthority = "example.com:80";
    Assertions.assertEquals(expectedAuthority, channel.authority());
  }

}
