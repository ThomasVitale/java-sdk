/*
 * Copyright 2023 The Dapr Authors
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

package io.dapr.client.resiliency;

import java.time.Duration;

/**
 * Resiliency policy for SDK communication to Dapr API.
 */
public final class ResiliencyOptions {

  private Duration timeout;

  private Integer maxRetries;

  public ResiliencyOptions() {
  }

  public ResiliencyOptions(Duration timeout, Integer maxRetries) {
    this.timeout = timeout;
    this.maxRetries = maxRetries;
  }

  public Duration getTimeout() {
    return timeout;
  }

  public ResiliencyOptions setTimeout(Duration timeout) {
    this.timeout = timeout;
    return this;
  }

  public Integer getMaxRetries() {
    return maxRetries;
  }

  public ResiliencyOptions setMaxRetries(Integer maxRetries) {
    this.maxRetries = maxRetries;
    return this;
  }
}
