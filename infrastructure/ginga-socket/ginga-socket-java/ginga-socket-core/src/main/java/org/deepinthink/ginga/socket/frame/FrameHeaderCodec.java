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

public final class FrameHeaderCodec {

  public static final int FLAGS_M = 0b01_0000_0000;

  public static final int FLAGS_F = 0b00_1000_0000;
  public static final int FLAGS_C = 0b00_0100_0000;
  public static final int FLAGS_N = 0b00_0010_0000;

  public static final String DISABLE_FRAME_TYPE_CHECK = "ginga.frames.disableFrameTypeCheck";
  private static final int FRAME_FLAGS_MASK = 0b0000_0011_1111_1111;
  private static final int FRAME_TYPE_BITS = 6;
  private static final int FRAME_TYPE_SHIFT = 16 - FRAME_TYPE_BITS;
  private static final int HEADER_SIZE = Integer.BYTES + Short.BYTES;

  private static boolean disableFrameTypeCheck;

  static {
    disableFrameTypeCheck = Boolean.getBoolean(DISABLE_FRAME_TYPE_CHECK);
  }

  static ByteBuf encodeStreamZero(ByteBufAllocator allocator, FrameType frameType, int flags) {
    return encode(allocator, 0, frameType, flags);
  }

  public static ByteBuf encode(
      ByteBufAllocator allocator, int streamId, FrameType frameType, int flags) {
    if (!frameType.canHaveMetadata() && ((flags & FLAGS_M) == FLAGS_M)) {
      throw new IllegalStateException("bad value for metadata flag");
    }
    short typeAndFlags = (short) (frameType.getEncodedType() << FRAME_TYPE_SHIFT | (short) flags);
    return allocator.buffer().writeInt(streamId).writeShort(typeAndFlags);
  }

  public static int flags(ByteBuf byteBuf) {
    byteBuf.markReaderIndex();
    byteBuf.skipBytes(Integer.BYTES);
    short typeAndFlags = byteBuf.readShort();
    byteBuf.resetReaderIndex();
    return typeAndFlags & FRAME_FLAGS_MASK;
  }

  public static boolean hasMetadata(ByteBuf byteBuf) {
    return (flags(byteBuf) & FLAGS_M) == FLAGS_M;
  }

  public static FrameType frameType(ByteBuf byteBuf) {
    byteBuf.markReaderIndex();
    byteBuf.skipBytes(Integer.BYTES);
    int typeAndFlags = byteBuf.readShort() & 0xFFFF;
    FrameType result = FrameType.fromEncodedType(typeAndFlags >> FRAME_TYPE_SHIFT);
    byteBuf.resetReaderIndex();
    return result;
  }

  public static void ensureFrameType(final FrameType frameType, ByteBuf byteBuf) {
    if (!disableFrameTypeCheck) {
      final FrameType typeInFrame = frameType(byteBuf);

      if (typeInFrame != frameType) {
        throw new AssertionError("expected " + frameType + ", but saw " + typeInFrame);
      }
    }
  }

  public static int size() {
    return HEADER_SIZE;
  }
}
