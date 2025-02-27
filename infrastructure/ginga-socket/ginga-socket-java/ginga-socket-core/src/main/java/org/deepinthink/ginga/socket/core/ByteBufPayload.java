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
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.Recycler;
import io.netty.util.Recycler.Handle;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import org.deepinthink.ginga.socket.Payload;

public final class ByteBufPayload extends AbstractReferenceCounted implements Payload {
  private static final Recycler<ByteBufPayload> RECYCLER =
      new Recycler<>() {
        @Override
        protected ByteBufPayload newObject(Handle<ByteBufPayload> handle) {
          return new ByteBufPayload(handle);
        }
      };

  private final Handle<ByteBufPayload> handle;
  private ByteBuf data;
  private ByteBuf metadata;

  public static Payload create(String data) {
    return create(ByteBufUtil.writeUtf8(ByteBufAllocator.DEFAULT, data), null);
  }

  public static Payload create(String data, String metadata) {
    return create(
        ByteBufUtil.writeUtf8(ByteBufAllocator.DEFAULT, data),
        metadata == null ? null : ByteBufUtil.writeUtf8(ByteBufAllocator.DEFAULT, metadata));
  }

  public static Payload create(CharSequence data, Charset dataCharset) {
    return create(data, dataCharset, null, null);
  }

  public static Payload create(
      CharSequence data, Charset dataCharset, CharSequence metadata, Charset metadataCharset) {
    return create(
        ByteBufUtil.encodeString(ByteBufAllocator.DEFAULT, CharBuffer.wrap(data), dataCharset),
        metadata == null
            ? null
            : ByteBufUtil.encodeString(
                ByteBufAllocator.DEFAULT, CharBuffer.wrap(metadata), metadataCharset));
  }

  public static Payload create(byte[] data) {
    return create(data, null);
  }

  public static Payload create(byte[] data, byte[] metadata) {
    return create(
        Unpooled.wrappedBuffer(data), metadata == null ? null : Unpooled.wrappedBuffer(metadata));
  }

  public static Payload create(ByteBuffer data, ByteBuffer metadata) {
    return create(
        Unpooled.wrappedBuffer(data), metadata == null ? null : Unpooled.wrappedBuffer(metadata));
  }

  public static Payload create(ByteBuf data) {
    return create(data, null);
  }

  public static Payload create(ByteBuf data, ByteBuf metadata) {
    ByteBufPayload payload = RECYCLER.get();
    payload.data = data;
    payload.metadata = metadata;
    payload.setRefCnt(1);
    return payload;
  }

  public ByteBufPayload(Handle<ByteBufPayload> handle) {
    this.handle = handle;
  }

  @Override
  public boolean hasMetadata() {
    ensureAccessible();
    return metadata != null;
  }

  @Override
  public ByteBuf sliceMetadata() {
    ensureAccessible();
    return metadata == null ? Unpooled.EMPTY_BUFFER : metadata.slice();
  }

  @Override
  public ByteBuf sliceData() {
    ensureAccessible();
    return data.slice();
  }

  @Override
  public ByteBuf data() {
    ensureAccessible();
    return data;
  }

  @Override
  public ByteBuf metadata() {
    ensureAccessible();
    return metadata == null ? Unpooled.EMPTY_BUFFER : metadata;
  }

  @Override
  public ByteBufPayload retain() {
    super.retain();
    return this;
  }

  @Override
  public ByteBufPayload retain(int increment) {
    super.retain(increment);
    return this;
  }

  @Override
  public ByteBufPayload touch() {
    ensureAccessible();
    data.touch();
    if (metadata != null) {
      metadata.touch();
    }
    return this;
  }

  @Override
  public ByteBufPayload touch(Object hint) {
    ensureAccessible();
    data.touch(hint);
    if (metadata != null) {
      metadata.touch(hint);
    }
    return this;
  }

  @Override
  protected void deallocate() {
    data.release();
    data = null;
    if (metadata != null) {
      metadata.release();
      metadata = null;
    }
    handle.recycle(this);
  }

  void ensureAccessible() {
    if (!isAccessible()) {
      throw new IllegalReferenceCountException(0);
    }
  }

  boolean isAccessible() {
    return refCnt() != 0;
  }
}
