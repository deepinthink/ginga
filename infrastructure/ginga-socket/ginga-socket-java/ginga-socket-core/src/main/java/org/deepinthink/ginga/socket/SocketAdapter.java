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

import static java.util.concurrent.CompletableFuture.failedStage;

import java.util.concurrent.CompletionStage;

class SocketAdapter {

  private static final CompletionStage<Void> UNSUPPORTED_FIRE_AND_FORGET =
      failedStage(new UnsupportedInteractionException("Fire-and-Forget"));

  private static final CompletionStage<Payload> UNSUPPORTED_REQUEST_RESPONSE =
      failedStage(new UnsupportedInteractionException("Request-Response"));

  private static final CompletionStage<Void> UNSUPPORTED_METADATA_PUSH =
      failedStage(new UnsupportedInteractionException("Metadata-Push"));

  static CompletionStage<Void> fireAndForget(Payload payload) {
    payload.release();
    return SocketAdapter.UNSUPPORTED_FIRE_AND_FORGET;
  }

  static CompletionStage<Payload> requestResponse(Payload payload) {
    payload.release();
    return SocketAdapter.UNSUPPORTED_REQUEST_RESPONSE;
  }

  public static CompletionStage<Void> metadataPush(Payload payload) {
    payload.release();
    return SocketAdapter.UNSUPPORTED_METADATA_PUSH;
  }

  private static class UnsupportedInteractionException extends RuntimeException {
    public UnsupportedInteractionException(String interactionName) {
      super(interactionName + " not implemented.", null, false, false);
    }
  }
}
