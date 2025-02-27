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
package org.deepinthink.ginga.socket.exceptions;

import static org.deepinthink.ginga.socket.frame.ErrorFrameCodec.APPLICATION_ERROR;

import io.netty.buffer.ByteBuf;
import java.util.Objects;
import org.deepinthink.ginga.socket.frame.ErrorFrameCodec;

public final class Exceptions {
  private Exceptions() {}

  public static RuntimeException from(int streamId, ByteBuf frame) {
    Objects.requireNonNull(frame, "frame must not be null");

    int errorCode = ErrorFrameCodec.errorCode(frame);
    String message = ErrorFrameCodec.dataUtf8(frame);

    if (streamId == 0) {
      switch (errorCode) {
        default -> {
          return new IllegalArgumentException(
              String.format("Invalid Error frame in Stream ID 0: 0x%08X '%s'", errorCode, message));
        }
      }
    } else {
      switch (errorCode) {
        case APPLICATION_ERROR -> {
          return new ApplicationErrorException(message);
        }
        default -> {
          return new IllegalArgumentException(
              String.format(
                  "Invalid Error frame in Stream ID %d: 0x%08X '%s'",
                  streamId, errorCode, message));
        }
      }
    }
  }
}
