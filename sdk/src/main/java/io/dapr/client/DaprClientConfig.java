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

package io.dapr.client;

import java.time.Duration;

/**
 * Properties for configuring the DaprClient interaction with the Dapr sidecar.
 */
public class DaprClientConfig {

  /**
   * API token to authenticate with the sidecar.
   */
  private String apiToken;

  /**
   * Sidecar endpoint.
   */
  private String endpoint;

  /**
   * Timeout for API calls to the sidecar.
   */
  private Duration timeout;

  /**
   * Maximum number of retries for retryable operations in case of failure.
   */
  private int maxRetries;

  private DaprClientConfig(String apiToken, String endpoint, Duration timeout, int maxRetries) {
    this.apiToken = apiToken;
    this.endpoint = endpoint;
    this.timeout = timeout;
    this.maxRetries = maxRetries;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String apiToken;
    private String endpoint;
    private Duration timeout = Duration.ofMillis(0);
    private int maxRetries = 0;

    public Builder apiToken(String apiToken) {
      this.apiToken = apiToken;
      return this;
    }

    public Builder endpoint(String endpoint) {
      this.endpoint = endpoint;
      return this;
    }

    public Builder timeout(Duration timeout) {
      this.timeout = timeout;
      return this;
    }

    public Builder maxRetries(int maxRetries) {
      this.maxRetries = maxRetries;
      return this;
    }

    public DaprClientConfig build() {
      return new DaprClientConfig(apiToken, endpoint, timeout, maxRetries);
    }
  }

  public String getApiToken() {
    return apiToken;
  }

  public void setApiToken(String apiToken) {
    this.apiToken = apiToken;
  }

  public String getEndpoint() {
    return endpoint;
  }

  public void setEndpoint(String endpoint) {
    this.endpoint = endpoint;
  }

  public Duration getTimeout() {
    return timeout;
  }

  public void setTimeout(Duration timeout) {
    this.timeout = timeout;
  }

  public int getMaxRetries() {
    return maxRetries;
  }

  public void setMaxRetries(int maxRetries) {
    this.maxRetries = maxRetries;
  }
}
