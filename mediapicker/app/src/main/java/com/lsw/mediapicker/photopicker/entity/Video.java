package com.lsw.mediapicker.photopicker.entity;

/**
 * Created by donglua on 15/6/30.
 */
public class Video {

  private int id;
  private String path;

  public Video(int id, String path) {
    this.id = id;
    this.path = path;
  }

  public Video() {
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Video)) return false;

    Video video = (Video) o;

    return id == video.id;
  }

  @Override public int hashCode() {
    return id;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }
}
