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

import static java.util.concurrent.CompletableFuture.completedStage;

import java.util.concurrent.CompletionStage;
import java.util.function.Function;

@FunctionalInterface
public interface SocketAcceptor {

  CompletionStage<GingaSocket> accept(SetupPayload setup, GingaSocket sending);

  static SocketAcceptor with(GingaSocket socket) {
    return (setup, sending) -> completedStage(socket);
  }

  static SocketAcceptor forFireAndForget(Function<Payload, CompletionStage<Void>> handler) {
    return with(
        new GingaSocket() {
          @Override
          public CompletionStage<Void> fireAndForget(Payload payload) {
            return handler.apply(payload);
          }
        });
  }

  static SocketAcceptor forRequestResponse(Function<Payload, CompletionStage<Payload>> handler) {
    return with(
        new GingaSocket() {
          @Override
          public CompletionStage<Payload> requestResponse(Payload payload) {
            return handler.apply(payload);
          }
        });
  }
}
