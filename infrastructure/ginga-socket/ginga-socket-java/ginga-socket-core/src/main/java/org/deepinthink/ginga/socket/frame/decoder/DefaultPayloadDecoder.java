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
import java.nio.ByteBuffer;
import org.deepinthink.ginga.socket.Payload;
import org.deepinthink.ginga.socket.core.DefaultPayload;
import org.deepinthink.ginga.socket.frame.FrameHeaderCodec;
import org.deepinthink.ginga.socket.frame.FrameType;

class DefaultPayloadDecoder implements PayloadDecoder {

  @Override
  public Payload apply(ByteBuf byteBuf) {
    ByteBuf m = null;
    ByteBuf d = null;

    FrameType frameType = FrameHeaderCodec.frameType(byteBuf);
    switch (frameType) {
    }

    ByteBuffer data = ByteBuffer.allocate(d.readableBytes());
    data.put(d.nioBuffer());
    data.flip();

    if (m != null) {
      ByteBuffer metadata = ByteBuffer.allocate(m.readableBytes());
      metadata.put(m.nioBuffer());
      metadata.flip();
      return new DefaultPayload(data, metadata);
    }

    return DefaultPayload.create(data);
  }
}
