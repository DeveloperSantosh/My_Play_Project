// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: entities.proto

package models;

/**
 * Protobuf type {@code com.treeleaf.RequestComment}
 */
public final class RequestComment extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:com.treeleaf.RequestComment)
        RequestCommentOrBuilder {
private static final long serialVersionUID = 0L;
  // Use RequestComment.newBuilder() to construct.
  private RequestComment(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private RequestComment() {
    comment_ = "";
    blogTitle_ = "";
  }

  @Override
  @SuppressWarnings({"unused"})
  protected Object newInstance(
      UnusedPrivateParameter unused) {
    return new RequestComment();
  }

  @Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private RequestComment(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new NullPointerException();
    }
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
          case 10: {
            String s = input.readStringRequireUtf8();

            comment_ = s;
            break;
          }
          case 18: {
            String s = input.readStringRequireUtf8();

            blogTitle_ = s;
            break;
          }
          default: {
            if (!parseUnknownField(
                input, unknownFields, extensionRegistry, tag)) {
              done = true;
            }
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
    return Entities.internal_static_com_treeleaf_RequestComment_descriptor;
  }

  @Override
  protected FieldAccessorTable
      internalGetFieldAccessorTable() {
    return Entities.internal_static_com_treeleaf_RequestComment_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            RequestComment.class, RequestComment.Builder.class);
  }

  public static final int COMMENT_FIELD_NUMBER = 1;
  private volatile Object comment_;
  /**
   * <code>string comment = 1;</code>
   * @return The comment.
   */
  @Override
  public String getComment() {
    Object ref = comment_;
    if (ref instanceof String) {
      return (String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      String s = bs.toStringUtf8();
      comment_ = s;
      return s;
    }
  }
  /**
   * <code>string comment = 1;</code>
   * @return The bytes for comment.
   */
  @Override
  public com.google.protobuf.ByteString
      getCommentBytes() {
    Object ref = comment_;
    if (ref instanceof String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (String) ref);
      comment_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int BLOGTITLE_FIELD_NUMBER = 2;
  private volatile Object blogTitle_;
  /**
   * <code>string blogTitle = 2;</code>
   * @return The blogTitle.
   */
  @Override
  public String getBlogTitle() {
    Object ref = blogTitle_;
    if (ref instanceof String) {
      return (String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      String s = bs.toStringUtf8();
      blogTitle_ = s;
      return s;
    }
  }
  /**
   * <code>string blogTitle = 2;</code>
   * @return The bytes for blogTitle.
   */
  @Override
  public com.google.protobuf.ByteString
      getBlogTitleBytes() {
    Object ref = blogTitle_;
    if (ref instanceof String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (String) ref);
      blogTitle_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  private byte memoizedIsInitialized = -1;
  @Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (!getCommentBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 1, comment_);
    }
    if (!getBlogTitleBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 2, blogTitle_);
    }
    unknownFields.writeTo(output);
  }

  @Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (!getCommentBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, comment_);
    }
    if (!getBlogTitleBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, blogTitle_);
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof RequestComment)) {
      return super.equals(obj);
    }
    RequestComment other = (RequestComment) obj;

    if (!getComment()
        .equals(other.getComment())) return false;
    if (!getBlogTitle()
        .equals(other.getBlogTitle())) return false;
    if (!unknownFields.equals(other.unknownFields)) return false;
    return true;
  }

  @Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    hash = (37 * hash) + COMMENT_FIELD_NUMBER;
    hash = (53 * hash) + getComment().hashCode();
    hash = (37 * hash) + BLOGTITLE_FIELD_NUMBER;
    hash = (53 * hash) + getBlogTitle().hashCode();
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static RequestComment parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static RequestComment parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static RequestComment parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static RequestComment parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static RequestComment parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static RequestComment parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static RequestComment parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static RequestComment parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static RequestComment parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static RequestComment parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static RequestComment parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static RequestComment parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(RequestComment prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @Override
  protected Builder newBuilderForType(
      BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code com.treeleaf.RequestComment}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:com.treeleaf.RequestComment)
          RequestCommentOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return Entities.internal_static_com_treeleaf_RequestComment_descriptor;
    }

    @Override
    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
      return Entities.internal_static_com_treeleaf_RequestComment_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              RequestComment.class, RequestComment.Builder.class);
    }

    // Construct using com.treeleaf.RequestComment.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    @Override
    public Builder clear() {
      super.clear();
      comment_ = "";

      blogTitle_ = "";

      return this;
    }

    @Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return Entities.internal_static_com_treeleaf_RequestComment_descriptor;
    }

    @Override
    public RequestComment getDefaultInstanceForType() {
      return RequestComment.getDefaultInstance();
    }

    @Override
    public RequestComment build() {
      RequestComment result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @Override
    public RequestComment buildPartial() {
      RequestComment result = new RequestComment(this);
      result.comment_ = comment_;
      result.blogTitle_ = blogTitle_;
      onBuilt();
      return result;
    }

    @Override
    public Builder clone() {
      return super.clone();
    }
    @Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        Object value) {
      return super.setField(field, value);
    }
    @Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    @Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    @Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, Object value) {
      return super.setRepeatedField(field, index, value);
    }
    @Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        Object value) {
      return super.addRepeatedField(field, value);
    }
    @Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof RequestComment) {
        return mergeFrom((RequestComment)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(RequestComment other) {
      if (other == RequestComment.getDefaultInstance()) return this;
      if (!other.getComment().isEmpty()) {
        comment_ = other.comment_;
        onChanged();
      }
      if (!other.getBlogTitle().isEmpty()) {
        blogTitle_ = other.blogTitle_;
        onChanged();
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    @Override
    public final boolean isInitialized() {
      return true;
    }

    @Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      RequestComment parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (RequestComment) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private Object comment_ = "";
    /**
     * <code>string comment = 1;</code>
     * @return The comment.
     */
    public String getComment() {
      Object ref = comment_;
      if (!(ref instanceof String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        comment_ = s;
        return s;
      } else {
        return (String) ref;
      }
    }
    /**
     * <code>string comment = 1;</code>
     * @return The bytes for comment.
     */
    public com.google.protobuf.ByteString
        getCommentBytes() {
      Object ref = comment_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        comment_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string comment = 1;</code>
     * @param value The comment to set.
     * @return This builder for chaining.
     */
    public Builder setComment(
        String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      comment_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string comment = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearComment() {
      
      comment_ = getDefaultInstance().getComment();
      onChanged();
      return this;
    }
    /**
     * <code>string comment = 1;</code>
     * @param value The bytes for comment to set.
     * @return This builder for chaining.
     */
    public Builder setCommentBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      comment_ = value;
      onChanged();
      return this;
    }

    private Object blogTitle_ = "";
    /**
     * <code>string blogTitle = 2;</code>
     * @return The blogTitle.
     */
    public String getBlogTitle() {
      Object ref = blogTitle_;
      if (!(ref instanceof String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        blogTitle_ = s;
        return s;
      } else {
        return (String) ref;
      }
    }
    /**
     * <code>string blogTitle = 2;</code>
     * @return The bytes for blogTitle.
     */
    public com.google.protobuf.ByteString
        getBlogTitleBytes() {
      Object ref = blogTitle_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        blogTitle_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string blogTitle = 2;</code>
     * @param value The blogTitle to set.
     * @return This builder for chaining.
     */
    public Builder setBlogTitle(
        String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      blogTitle_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string blogTitle = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearBlogTitle() {
      
      blogTitle_ = getDefaultInstance().getBlogTitle();
      onChanged();
      return this;
    }
    /**
     * <code>string blogTitle = 2;</code>
     * @param value The bytes for blogTitle to set.
     * @return This builder for chaining.
     */
    public Builder setBlogTitleBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      blogTitle_ = value;
      onChanged();
      return this;
    }
    @Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:com.treeleaf.RequestComment)
  }

  // @@protoc_insertion_point(class_scope:com.treeleaf.RequestComment)
  private static final RequestComment DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new RequestComment();
  }

  public static RequestComment getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<RequestComment>
      PARSER = new com.google.protobuf.AbstractParser<RequestComment>() {
    @Override
    public RequestComment parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new RequestComment(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<RequestComment> parser() {
    return PARSER;
  }

  @Override
  public com.google.protobuf.Parser<RequestComment> getParserForType() {
    return PARSER;
  }

  @Override
  public RequestComment getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

