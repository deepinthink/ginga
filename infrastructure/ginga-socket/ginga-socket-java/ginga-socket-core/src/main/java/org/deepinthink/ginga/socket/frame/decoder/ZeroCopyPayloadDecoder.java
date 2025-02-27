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
package org.deepinthink.ginga.socket.frame.decoder;

import io.netty.buffer.ByteBuf;
import org.deepinthink.ginga.socket.Payload;
import org.deepinthink.ginga.socket.core.ByteBufPayload;
import org.deepinthink.ginga.socket.frame.FrameHeaderCodec;
import org.deepinthink.ginga.socket.frame.FrameType;

class ZeroCopyPayloadDecoder implements PayloadDecoder {

  @Override
  public Payload apply(ByteBuf byteBuf) {
    ByteBuf m = null;
    ByteBuf d = null;

    FrameType frameType = FrameHeaderCodec.frameType(byteBuf);

    switch (frameType) {
    }

    return ByteBufPayload.create(d.retain(), m != null ? m.retain() : null);
  }
}
