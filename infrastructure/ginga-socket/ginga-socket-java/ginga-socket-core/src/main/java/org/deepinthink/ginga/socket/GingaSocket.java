/*
 * Copyright (c) 2024-present DeepInThink. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.deepinthink.ginga.socket;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public interface GingaSocket extends Availability, Closable {

  default CompletionStage<Void> fireAndForget(Payload payload) {
    return SocketAdapter.fireAndForget(payload);
  }

  default CompletionStage<Payload> requestResponse(Payload payload) {
    return SocketAdapter.requestResponse(payload);
  }

  default CompletionStage<Void> metadataPush(Payload payload) {
    return SocketAdapter.metadataPush(payload);
  }

  @Override
  default double availability() {
    return isDispose() ? 0.0 : 1.0;
  }

  @Override
  default void dispose() {}

  @Override
  default boolean isDispose() {
    return false;
  }

  @Override
  default CompletionStage<Void> onClose() {
    return CompletableFuture.failedFuture(null);
  }
}
