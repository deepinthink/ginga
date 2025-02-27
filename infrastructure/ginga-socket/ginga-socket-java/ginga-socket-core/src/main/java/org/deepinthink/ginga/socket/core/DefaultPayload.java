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
package org.deepinthink.ginga.socket.core;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.deepinthink.ginga.socket.Payload;

public final class DefaultPayload implements Payload {
  public static final ByteBuffer EMPTY_BUFFER = ByteBuffer.allocateDirect(0);

  private final ByteBuffer data;
  private final ByteBuffer metadata;

  public static Payload create(CharSequence data) {
    return create(data, StandardCharsets.UTF_8);
  }

  public static Payload create(CharSequence data, Charset dataCharset) {
    return create(data, dataCharset, null, null);
  }

  public static Payload create(CharSequence data, CharSequence metadata) {
    return create(data, StandardCharsets.UTF_8, metadata, StandardCharsets.UTF_8);
  }

  public static Payload create(
      CharSequence data, Charset dataCharset, CharSequence metadata, Charset metadataCharset) {
    return create(
        dataCharset.encode(CharBuffer.wrap(data)),
        metadata == null ? null : metadataCharset.encode(CharBuffer.wrap(metadata)));
  }

  public static Payload create(byte[] data) {
    return create(data, null);
  }

  public static Payload create(byte[] data, byte[] metadata) {
    return create(ByteBuffer.wrap(data), metadata == null ? null : ByteBuffer.wrap(metadata));
  }

  public static Payload create(ByteBuffer data) {
    return create(data, null);
  }

  public static Payload create(ByteBuffer data, ByteBuffer metadata) {
    return new DefaultPayload(data, metadata);
  }

  public static Payload create(ByteBuf data) {
    return create(data, null);
  }

  public static Payload create(ByteBuf data, ByteBuf metadata) {
    try {
      return create(toBytes(data), metadata != null ? toBytes(metadata) : null);
    } finally {
      data.release();
      if (metadata != null) {
        metadata.release();
      }
    }
  }

  public static Payload create(Payload payload) {
    return create(payload.getData(), payload.getMetadata());
  }

  private static byte[] toBytes(ByteBuf byteBuf) {
    byte[] bytes = new byte[byteBuf.readableBytes()];
    byteBuf.markReaderIndex();
    byteBuf.readBytes(bytes);
    byteBuf.resetReaderIndex();
    return bytes;
  }

  public DefaultPayload(ByteBuffer data, ByteBuffer metadata) {
    this.data = data;
    this.metadata = metadata;
  }

  @Override
  public boolean hasMetadata() {
    return metadata != null;
  }

  @Override
  public ByteBuf sliceMetadata() {
    return metadata == null ? Unpooled.EMPTY_BUFFER : Unpooled.wrappedBuffer(metadata);
  }

  @Override
  public ByteBuf sliceData() {
    return Unpooled.wrappedBuffer(data);
  }

  @Override
  public ByteBuffer getMetadata() {
    return metadata == null ? DefaultPayload.EMPTY_BUFFER : metadata.duplicate();
  }

  @Override
  public ByteBuffer getData() {
    return data.duplicate();
  }

  @Override
  public ByteBuf data() {
    return sliceData();
  }

  @Override
  public ByteBuf metadata() {
    return sliceMetadata();
  }

  @Override
  public DefaultPayload retain() {
    return this;
  }

  @Override
  public DefaultPayload retain(int increment) {
    return this;
  }

  @Override
  public DefaultPayload touch() {
    return this;
  }

  @Override
  public DefaultPayload touch(Object hint) {
    return this;
  }

  @Override
  public int refCnt() {
    return 1;
  }

  @Override
  public boolean release() {
    return false;
  }

  @Override
  public boolean release(int i) {
    return false;
  }
}
