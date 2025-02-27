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
import org.deepinthink.ginga.socket.Payload;
import org.deepinthink.ginga.socket.SetupPayload;
import org.deepinthink.ginga.socket.frame.FrameHeaderCodec;
import org.deepinthink.ginga.socket.frame.SetupFrameCodec;

public final class DefaultSetupPayload extends SetupPayload {

  private final ByteBuf setupFrame;

  public DefaultSetupPayload(ByteBuf setupFrame) {
    this.setupFrame = setupFrame;
  }

  @Override
  public boolean hasMetadata() {
    return FrameHeaderCodec.hasMetadata(setupFrame);
  }

  @Override
  public ByteBuf sliceMetadata() {
    ByteBuf metadata = SetupFrameCodec.metadata(setupFrame);
    return metadata == null ? Unpooled.EMPTY_BUFFER : metadata;
  }

  @Override
  public ByteBuf sliceData() {
    return SetupFrameCodec.data(setupFrame);
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
  public String metadataMimeType() {
    return SetupFrameCodec.metadataMimeType(setupFrame);
  }

  @Override
  public String dataMimeType() {
    return SetupFrameCodec.dataMimeType(setupFrame);
  }

  @Override
  public int keepAliveInterval() {
    return SetupFrameCodec.keepAliveInterval(setupFrame);
  }

  @Override
  public int getFlags() {
    return FrameHeaderCodec.flags(setupFrame);
  }

  @Override
  public SetupPayload touch() {
    setupFrame.touch();
    return this;
  }

  @Override
  public Payload touch(Object hint) {
    setupFrame.touch(hint);
    return this;
  }

  @Override
  protected void deallocate() {
    setupFrame.release();
  }
}
