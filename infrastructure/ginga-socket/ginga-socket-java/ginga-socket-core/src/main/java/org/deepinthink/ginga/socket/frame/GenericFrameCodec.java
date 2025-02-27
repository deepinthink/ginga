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
import io.netty.util.IllegalReferenceCountException;
import org.deepinthink.ginga.socket.Payload;

class GenericFrameCodec {

  static ByteBuf encodeReleasingPayload(
      final ByteBufAllocator allocator,
      final FrameType frameType,
      final int streamId,
      boolean complete,
      boolean next,
      final Payload payload) {
    return encodeReleasingPayload(allocator, frameType, streamId, complete, next, 0, payload);
  }

  static ByteBuf encodeReleasingPayload(
      final ByteBufAllocator allocator,
      final FrameType frameType,
      final int streamId,
      boolean complete,
      boolean next,
      int requestN,
      final Payload payload) {

    boolean hasMetadata = payload.hasMetadata();
    final ByteBuf metadata = hasMetadata ? payload.metadata().retain() : null;
    final ByteBuf data;
    try {
      data = payload.data().retain();
    } catch (IllegalReferenceCountException | NullPointerException e) {
      if (hasMetadata) {
        metadata.release();
      }
      throw e;
    }
    try {
      payload.release();
    } catch (IllegalReferenceCountException e) {
      data.release();
      if (hasMetadata) {
        metadata.release();
      }
      throw e;
    }

    return encode(allocator, frameType, streamId, false, complete, next, requestN, metadata, data);
  }

  static ByteBuf encode(
      final ByteBufAllocator allocator,
      final FrameType frameType,
      final int streamId,
      boolean fragmentFollows,
      ByteBuf metadata,
      ByteBuf data) {
    return encode(allocator, frameType, streamId, fragmentFollows, false, false, 0, metadata, data);
  }

  static ByteBuf encode(
      final ByteBufAllocator allocator,
      final FrameType frameType,
      final int streamId,
      boolean fragmentFollows,
      boolean complete,
      boolean next,
      int requestN,
      ByteBuf metadata,
      ByteBuf data) {

    final boolean hasMetadata = metadata != null;

    int flags = 0;

    if (hasMetadata) {
      flags |= FrameHeaderCodec.FLAGS_M;
    }

    if (fragmentFollows) {
      flags |= FrameHeaderCodec.FLAGS_F;
    }

    if (complete) {
      flags |= FrameHeaderCodec.FLAGS_C;
    }

    if (next) {
      flags |= FrameHeaderCodec.FLAGS_N;
    }

    final ByteBuf header = FrameHeaderCodec.encode(allocator, streamId, frameType, flags);

    if (requestN > 0) {
      header.writeInt(requestN);
    }

    return FrameBodyCodec.encode(allocator, header, metadata, hasMetadata, data);
  }

  static ByteBuf data(ByteBuf byteBuf) {
    boolean hasMetadata = FrameHeaderCodec.hasMetadata(byteBuf);
    int idx = byteBuf.readerIndex();
    byteBuf.skipBytes(FrameHeaderCodec.size());
    ByteBuf data = FrameBodyCodec.dataWithoutMarking(byteBuf, hasMetadata);
    byteBuf.readerIndex(idx);
    return data;
  }

  static ByteBuf metadata(ByteBuf byteBuf) {
    boolean hasMetadata = FrameHeaderCodec.hasMetadata(byteBuf);
    if (!hasMetadata) {
      return null;
    }
    byteBuf.markReaderIndex();
    byteBuf.skipBytes(FrameHeaderCodec.size());
    ByteBuf metadata = FrameBodyCodec.metadataWithoutMarking(byteBuf);
    byteBuf.resetReaderIndex();
    return metadata;
  }

  static ByteBuf dataWithRequestN(ByteBuf byteBuf) {
    boolean hasMetadata = FrameHeaderCodec.hasMetadata(byteBuf);
    byteBuf.markReaderIndex();
    byteBuf.skipBytes(FrameHeaderCodec.size() + Integer.BYTES);
    ByteBuf data = FrameBodyCodec.dataWithoutMarking(byteBuf, hasMetadata);
    byteBuf.resetReaderIndex();
    return data;
  }

  static ByteBuf metadataWithRequestN(ByteBuf byteBuf) {
    boolean hasMetadata = FrameHeaderCodec.hasMetadata(byteBuf);
    if (!hasMetadata) {
      return null;
    }
    byteBuf.markReaderIndex();
    byteBuf.skipBytes(FrameHeaderCodec.size() + Integer.BYTES);
    ByteBuf metadata = FrameBodyCodec.metadataWithoutMarking(byteBuf);
    byteBuf.resetReaderIndex();
    return metadata;
  }

  static int initialRequestN(ByteBuf byteBuf) {
    byteBuf.markReaderIndex();
    int i = byteBuf.skipBytes(FrameHeaderCodec.size()).readInt();
    byteBuf.resetReaderIndex();
    return i;
  }
}
