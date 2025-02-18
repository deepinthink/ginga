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
import org.deepinthink.ginga.socket.messaging.GingaSocketRequester;
import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.util.Assert;

public class GingaSocketRequesterMethodArgumentResolver implements HandlerMethodArgumentResolver {

  public static final String GINGA_SOCKET_REQUESTER_HEADER = "gingaSocketRequester";

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    Class<?> type = parameter.getParameterType();
    return GingaSocketRequester.class.equals(type) || GingaSocket.class.isAssignableFrom(type);
  }

  @Override
  public Object resolveArgument(MethodParameter parameter, Message<?> message) throws Exception {
    Object headerValue = message.getHeaders().get(GINGA_SOCKET_REQUESTER_HEADER);
    Assert.notNull(headerValue, "Missing '" + GINGA_SOCKET_REQUESTER_HEADER + "'");

    Assert.isInstanceOf(
        GingaSocketRequester.class,
        headerValue,
        "Expected header value of type GingaSocketRequester");
    GingaSocketRequester requester = (GingaSocketRequester) headerValue;

    Class<?> type = parameter.getParameterType();
    if (GingaSocketRequester.class.equals(type)) {
      return requester;
    } else if (GingaSocket.class.isAssignableFrom(type)) {
      return requester.gingaSocket();
    } else {
      throw new IllegalArgumentException("Unexpected parameter type: " + parameter);
    }
  }
}
