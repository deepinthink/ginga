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
package org.deepinthink.ginga.socket.messaging.annotation.support;

import org.deepinthink.ginga.socket.GingaSocket;
import org.deepinthink.ginga.socket.SetupPayload;
import org.deepinthink.ginga.socket.SocketAcceptor;
import org.springframework.messaging.handler.CompositeMessageCondition;
import org.springframework.messaging.handler.invocation.AbstractMethodMessageHandler;

public abstract class GingaSocketMessageHandler
    extends AbstractMethodMessageHandler<CompositeMessageCondition> {

  public SocketAcceptor responder() {
    return (setup, sending) -> {
      MessagingGingaSocket responder;
      try {
        responder = createResponder(setup, sending);
      } catch (Throwable cause) {

      }
      return null;
    };
  }

  private MessagingGingaSocket createResponder(SetupPayload setup, GingaSocket sending) {
    return new MessagingGingaSocket();
  }
}
