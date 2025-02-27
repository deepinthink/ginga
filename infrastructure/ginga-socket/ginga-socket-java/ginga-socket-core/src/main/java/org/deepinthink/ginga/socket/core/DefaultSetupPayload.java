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
import org.deepinthink.ginga.socket.Payload;
import org.deepinthink.ginga.socket.SetupPayload;

public final class DefaultSetupPayload extends SetupPayload {

  private final ByteBuf setupFrame;

  public DefaultSetupPayload(ByteBuf setupFrame) {
    this.setupFrame = setupFrame;
  }

  @Override
  public boolean hasMetadata() {
    return false;
  }

  @Override
  public ByteBuf sliceMetadata() {
    return null;
  }

  @Override
  public ByteBuf sliceData() {
    return null;
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
    return "";
  }

  @Override
  public String dataMimeType() {
    return "";
  }

  @Override
  public int keepAliveInterval() {
    return 0;
  }

  @Override
  public int getFlags() {
    return 0;
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
