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

import java.util.Arrays;

public enum FrameType {
  RESERVED(0X00),

  SETUP(0x01, Flags.CAN_HAVE_DATA | Flags.CAN_HAVE_METADATA),

  KEEPALIVE(0x03, Flags.CAN_HAVE_DATA),

  REQUEST_RESPONSE(0x04, Flags.CAN_HAVE_DATA | Flags.CAN_HAVE_METADATA),

  REQUEST_FNF(0x05, Flags.CAN_HAVE_DATA | Flags.CAN_HAVE_METADATA),

  ERROR(0x0B, Flags.CAN_HAVE_DATA),

  METADATA_PUSH(0x0C, Flags.CAN_HAVE_METADATA);

  private final int encodedType;
  private final int flags;

  private static final FrameType[] FRAME_TYPES_BY_ENCODED_TYPE;

  static {
    FRAME_TYPES_BY_ENCODED_TYPE = new FrameType[getMaximumEncodedType() + 1];

    for (FrameType frameType : values()) {
      FRAME_TYPES_BY_ENCODED_TYPE[frameType.encodedType] = frameType;
    }
  }

  FrameType(int encodedType) {
    this(encodedType, Flags.EMPTY);
  }

  FrameType(int encodedType, int flags) {
    this.encodedType = encodedType;
    this.flags = flags;
  }

  public static FrameType fromEncodedType(int encodedType) {
    FrameType frameType = FRAME_TYPES_BY_ENCODED_TYPE[encodedType];

    if (frameType == null) {
      throw new IllegalArgumentException(String.format("Frame type %d is unknown", encodedType));
    }

    return frameType;
  }

  private static int getMaximumEncodedType() {
    return Arrays.stream(values()).mapToInt(frameType -> frameType.encodedType).max().orElse(0);
  }

  public boolean canHaveData() {
    return Flags.CAN_HAVE_DATA == (flags & Flags.CAN_HAVE_DATA);
  }

  public boolean canHaveMetadata() {
    return Flags.CAN_HAVE_METADATA == (flags & Flags.CAN_HAVE_METADATA);
  }

  public int getEncodedType() {
    return encodedType;
  }

  private static class Flags {
    private static final int EMPTY = 0b00000;
    private static final int CAN_HAVE_DATA = 0b10000;
    private static final int CAN_HAVE_METADATA = 0b01000;

    private Flags() {}
  }
}
