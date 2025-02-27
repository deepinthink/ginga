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
package org.deepinthink.ginga.socket.frame;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class KeepAliveFrameCodec {

  public static final int FLAGS_KEEPALIVE_R = 0b00_1000_0000;
  public static final long LAST_POSITION_MASK = 0x8000000000000000L;

  private KeepAliveFrameCodec() {}

  public static ByteBuf encode(
      ByteBufAllocator allocator, boolean respond, long lastPosition, ByteBuf data) {
    int flags = respond ? FLAGS_KEEPALIVE_R : 0;
    ByteBuf header = FrameHeaderCodec.encodeStreamZero(allocator, FrameType.KEEPALIVE, flags);

    long lp = 0;
    if (lastPosition > 0) {
      lp |= lastPosition;
    }

    header.writeLong(lp);

    return FrameBodyCodec.encode(allocator, header, null, false, data);
  }

  public static boolean respondFlag(ByteBuf byteBuf) {
    FrameHeaderCodec.ensureFrameType(FrameType.KEEPALIVE, byteBuf);
    int flags = FrameHeaderCodec.flags(byteBuf);
    return (flags & FLAGS_KEEPALIVE_R) == FLAGS_KEEPALIVE_R;
  }

  public static long lastPosition(ByteBuf byteBuf) {
    FrameHeaderCodec.ensureFrameType(FrameType.KEEPALIVE, byteBuf);
    byteBuf.markReaderIndex();
    long l = byteBuf.skipBytes(FrameHeaderCodec.size()).readLong();
    byteBuf.resetReaderIndex();
    return l;
  }

  public static ByteBuf data(ByteBuf byteBuf) {
    FrameHeaderCodec.ensureFrameType(FrameType.KEEPALIVE, byteBuf);
    byteBuf.markReaderIndex();
    ByteBuf slice = byteBuf.skipBytes(FrameHeaderCodec.size() + Long.BYTES).slice();
    byteBuf.resetReaderIndex();
    return slice;
  }
}
