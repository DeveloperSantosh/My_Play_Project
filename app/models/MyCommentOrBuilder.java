// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: entities.proto

package models;

public interface MyCommentOrBuilder extends
    // @@protoc_insertion_point(interface_extends:models.MyComment)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>int32 id = 1;</code>
   * @return The id.
   */
  int getId();

  /**
   * <code>string comment = 2;</code>
   * @return The comment.
   */
  java.lang.String getComment();
  /**
   * <code>string comment = 2;</code>
   * @return The bytes for comment.
   */
  com.google.protobuf.ByteString
      getCommentBytes();

  /**
   * <code>string timestamp = 3;</code>
   * @return The timestamp.
   */
  java.lang.String getTimestamp();
  /**
   * <code>string timestamp = 3;</code>
   * @return The bytes for timestamp.
   */
  com.google.protobuf.ByteString
      getTimestampBytes();

  /**
   * <code>.models.MyBlog blog = 4;</code>
   * @return Whether the blog field is set.
   */
  boolean hasBlog();
  /**
   * <code>.models.MyBlog blog = 4;</code>
   * @return The blog.
   */
  models.MyBlog getBlog();
  /**
   * <code>.models.MyBlog blog = 4;</code>
   */
  models.MyBlogOrBuilder getBlogOrBuilder();
}