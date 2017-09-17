// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: Payload.proto

package com.tencent.tvmanager.netty.msg;

public final class PayloadProto {
  private PayloadProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface PayloadOrBuilder extends
      // @@protoc_insertion_point(interface_extends:Payload)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <pre>
     *消息id
     * </pre>
     *
     * <code>required string messageId = 1;</code>
     */
    boolean hasMessageId();
    /**
     * <pre>
     *消息id
     * </pre>
     *
     * <code>required string messageId = 1;</code>
     */
    java.lang.String getMessageId();
    /**
     * <pre>
     *消息id
     * </pre>
     *
     * <code>required string messageId = 1;</code>
     */
    com.google.protobuf.ByteString
        getMessageIdBytes();

    /**
     * <code>required string content = 2;</code>
     */
    boolean hasContent();
    /**
     * <code>required string content = 2;</code>
     */
    java.lang.String getContent();
    /**
     * <code>required string content = 2;</code>
     */
    com.google.protobuf.ByteString
        getContentBytes();
  }
  /**
   * Protobuf type {@code Payload}
   */
  public  static final class Payload extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:Payload)
      PayloadOrBuilder {
    // Use Payload.newBuilder() to construct.
    private Payload(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private Payload() {
      messageId_ = "";
      content_ = "";
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private Payload(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
            case 10: {
              com.google.protobuf.ByteString bs = input.readBytes();
              bitField0_ |= 0x00000001;
              messageId_ = bs;
              break;
            }
            case 18: {
              com.google.protobuf.ByteString bs = input.readBytes();
              bitField0_ |= 0x00000002;
              content_ = bs;
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.tencent.tvmanager.netty.msg.PayloadProto.internal_static_Payload_descriptor;
    }

    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.tencent.tvmanager.netty.msg.PayloadProto.internal_static_Payload_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.tencent.tvmanager.netty.msg.PayloadProto.Payload.class, com.tencent.tvmanager.netty.msg.PayloadProto.Payload.Builder.class);
    }

    private int bitField0_;
    public static final int MESSAGEID_FIELD_NUMBER = 1;
    private volatile java.lang.Object messageId_;
    /**
     * <pre>
     *消息id
     * </pre>
     *
     * <code>required string messageId = 1;</code>
     */
    public boolean hasMessageId() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    /**
     * <pre>
     *消息id
     * </pre>
     *
     * <code>required string messageId = 1;</code>
     */
    public java.lang.String getMessageId() {
      java.lang.Object ref = messageId_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          messageId_ = s;
        }
        return s;
      }
    }
    /**
     * <pre>
     *消息id
     * </pre>
     *
     * <code>required string messageId = 1;</code>
     */
    public com.google.protobuf.ByteString
        getMessageIdBytes() {
      java.lang.Object ref = messageId_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        messageId_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int CONTENT_FIELD_NUMBER = 2;
    private volatile java.lang.Object content_;
    /**
     * <code>required string content = 2;</code>
     */
    public boolean hasContent() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    /**
     * <code>required string content = 2;</code>
     */
    public java.lang.String getContent() {
      java.lang.Object ref = content_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          content_ = s;
        }
        return s;
      }
    }
    /**
     * <code>required string content = 2;</code>
     */
    public com.google.protobuf.ByteString
        getContentBytes() {
      java.lang.Object ref = content_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        content_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      if (!hasMessageId()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasContent()) {
        memoizedIsInitialized = 0;
        return false;
      }
      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 1, messageId_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 2, content_);
      }
      unknownFields.writeTo(output);
    }

    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, messageId_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, content_);
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;
    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof com.tencent.tvmanager.netty.msg.PayloadProto.Payload)) {
        return super.equals(obj);
      }
      com.tencent.tvmanager.netty.msg.PayloadProto.Payload other = (com.tencent.tvmanager.netty.msg.PayloadProto.Payload) obj;

      boolean result = true;
      result = result && (hasMessageId() == other.hasMessageId());
      if (hasMessageId()) {
        result = result && getMessageId()
            .equals(other.getMessageId());
      }
      result = result && (hasContent() == other.hasContent());
      if (hasContent()) {
        result = result && getContent()
            .equals(other.getContent());
      }
      result = result && unknownFields.equals(other.unknownFields);
      return result;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptorForType().hashCode();
      if (hasMessageId()) {
        hash = (37 * hash) + MESSAGEID_FIELD_NUMBER;
        hash = (53 * hash) + getMessageId().hashCode();
      }
      if (hasContent()) {
        hash = (37 * hash) + CONTENT_FIELD_NUMBER;
        hash = (53 * hash) + getContent().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static com.tencent.tvmanager.netty.msg.PayloadProto.Payload parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.tencent.tvmanager.netty.msg.PayloadProto.Payload parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.tencent.tvmanager.netty.msg.PayloadProto.Payload parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.tencent.tvmanager.netty.msg.PayloadProto.Payload parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.tencent.tvmanager.netty.msg.PayloadProto.Payload parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static com.tencent.tvmanager.netty.msg.PayloadProto.Payload parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static com.tencent.tvmanager.netty.msg.PayloadProto.Payload parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static com.tencent.tvmanager.netty.msg.PayloadProto.Payload parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static com.tencent.tvmanager.netty.msg.PayloadProto.Payload parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static com.tencent.tvmanager.netty.msg.PayloadProto.Payload parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(com.tencent.tvmanager.netty.msg.PayloadProto.Payload prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code Payload}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:Payload)
        com.tencent.tvmanager.netty.msg.PayloadProto.PayloadOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return com.tencent.tvmanager.netty.msg.PayloadProto.internal_static_Payload_descriptor;
      }

      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return com.tencent.tvmanager.netty.msg.PayloadProto.internal_static_Payload_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                com.tencent.tvmanager.netty.msg.PayloadProto.Payload.class, com.tencent.tvmanager.netty.msg.PayloadProto.Payload.Builder.class);
      }

      // Construct using com.tencent.tvmanager.netty.msg.PayloadProto.Payload.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      public Builder clear() {
        super.clear();
        messageId_ = "";
        bitField0_ = (bitField0_ & ~0x00000001);
        content_ = "";
        bitField0_ = (bitField0_ & ~0x00000002);
        return this;
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.tencent.tvmanager.netty.msg.PayloadProto.internal_static_Payload_descriptor;
      }

      public com.tencent.tvmanager.netty.msg.PayloadProto.Payload getDefaultInstanceForType() {
        return com.tencent.tvmanager.netty.msg.PayloadProto.Payload.getDefaultInstance();
      }

      public com.tencent.tvmanager.netty.msg.PayloadProto.Payload build() {
        com.tencent.tvmanager.netty.msg.PayloadProto.Payload result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public com.tencent.tvmanager.netty.msg.PayloadProto.Payload buildPartial() {
        com.tencent.tvmanager.netty.msg.PayloadProto.Payload result = new com.tencent.tvmanager.netty.msg.PayloadProto.Payload(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.messageId_ = messageId_;
        if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
          to_bitField0_ |= 0x00000002;
        }
        result.content_ = content_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      public Builder clone() {
        return (Builder) super.clone();
      }
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return (Builder) super.setField(field, value);
      }
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return (Builder) super.clearField(field);
      }
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return (Builder) super.clearOneof(oneof);
      }
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, Object value) {
        return (Builder) super.setRepeatedField(field, index, value);
      }
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return (Builder) super.addRepeatedField(field, value);
      }
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.tencent.tvmanager.netty.msg.PayloadProto.Payload) {
          return mergeFrom((com.tencent.tvmanager.netty.msg.PayloadProto.Payload)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(com.tencent.tvmanager.netty.msg.PayloadProto.Payload other) {
        if (other == com.tencent.tvmanager.netty.msg.PayloadProto.Payload.getDefaultInstance()) return this;
        if (other.hasMessageId()) {
          bitField0_ |= 0x00000001;
          messageId_ = other.messageId_;
          onChanged();
        }
        if (other.hasContent()) {
          bitField0_ |= 0x00000002;
          content_ = other.content_;
          onChanged();
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      public final boolean isInitialized() {
        if (!hasMessageId()) {
          return false;
        }
        if (!hasContent()) {
          return false;
        }
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.tencent.tvmanager.netty.msg.PayloadProto.Payload parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (com.tencent.tvmanager.netty.msg.PayloadProto.Payload) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private java.lang.Object messageId_ = "";
      /**
       * <pre>
       *消息id
       * </pre>
       *
       * <code>required string messageId = 1;</code>
       */
      public boolean hasMessageId() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      /**
       * <pre>
       *消息id
       * </pre>
       *
       * <code>required string messageId = 1;</code>
       */
      public java.lang.String getMessageId() {
        java.lang.Object ref = messageId_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          if (bs.isValidUtf8()) {
            messageId_ = s;
          }
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <pre>
       *消息id
       * </pre>
       *
       * <code>required string messageId = 1;</code>
       */
      public com.google.protobuf.ByteString
          getMessageIdBytes() {
        java.lang.Object ref = messageId_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          messageId_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <pre>
       *消息id
       * </pre>
       *
       * <code>required string messageId = 1;</code>
       */
      public Builder setMessageId(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000001;
        messageId_ = value;
        onChanged();
        return this;
      }
      /**
       * <pre>
       *消息id
       * </pre>
       *
       * <code>required string messageId = 1;</code>
       */
      public Builder clearMessageId() {
        bitField0_ = (bitField0_ & ~0x00000001);
        messageId_ = getDefaultInstance().getMessageId();
        onChanged();
        return this;
      }
      /**
       * <pre>
       *消息id
       * </pre>
       *
       * <code>required string messageId = 1;</code>
       */
      public Builder setMessageIdBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000001;
        messageId_ = value;
        onChanged();
        return this;
      }

      private java.lang.Object content_ = "";
      /**
       * <code>required string content = 2;</code>
       */
      public boolean hasContent() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
      }
      /**
       * <code>required string content = 2;</code>
       */
      public java.lang.String getContent() {
        java.lang.Object ref = content_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          if (bs.isValidUtf8()) {
            content_ = s;
          }
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>required string content = 2;</code>
       */
      public com.google.protobuf.ByteString
          getContentBytes() {
        java.lang.Object ref = content_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          content_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>required string content = 2;</code>
       */
      public Builder setContent(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
        content_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required string content = 2;</code>
       */
      public Builder clearContent() {
        bitField0_ = (bitField0_ & ~0x00000002);
        content_ = getDefaultInstance().getContent();
        onChanged();
        return this;
      }
      /**
       * <code>required string content = 2;</code>
       */
      public Builder setContentBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
        content_ = value;
        onChanged();
        return this;
      }
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:Payload)
    }

    // @@protoc_insertion_point(class_scope:Payload)
    private static final com.tencent.tvmanager.netty.msg.PayloadProto.Payload DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new com.tencent.tvmanager.netty.msg.PayloadProto.Payload();
    }

    public static com.tencent.tvmanager.netty.msg.PayloadProto.Payload getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    @java.lang.Deprecated public static final com.google.protobuf.Parser<Payload>
        PARSER = new com.google.protobuf.AbstractParser<Payload>() {
      public Payload parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
          return new Payload(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<Payload> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<Payload> getParserForType() {
      return PARSER;
    }

    public com.tencent.tvmanager.netty.msg.PayloadProto.Payload getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_Payload_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_Payload_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\rPayload.proto\"-\n\007Payload\022\021\n\tmessageId\030" +
      "\001 \002(\t\022\017\n\007content\030\002 \002(\tB/\n\037com.tencent.tv" +
      "manager.netty.msgB\014PayloadProto"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_Payload_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_Payload_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_Payload_descriptor,
        new java.lang.String[] { "MessageId", "Content", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}