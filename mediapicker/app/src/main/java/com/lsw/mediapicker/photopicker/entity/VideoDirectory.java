package com.lsw.mediapicker.photopicker.entity;

import android.text.TextUtils;


import com.lsw.mediapicker.photopicker.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by donglua on 15/6/28.
 */
public class VideoDirectory {

  private String id;
  private String coverPath;
  private String name;
  private long   dateAdded;
  private List<Video> videos = new ArrayList<>();

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof VideoDirectory)) return false;

    VideoDirectory directory = (VideoDirectory) o;

    boolean hasId = !TextUtils.isEmpty(id);
    boolean otherHasId = !TextUtils.isEmpty(directory.id);

    if (hasId && otherHasId) {
      if (!TextUtils.equals(id, directory.id)) {
        return false;
      }

      return TextUtils.equals(name, directory.name);
    }

    return false;
  }

  @Override public int hashCode() {
    if (TextUtils.isEmpty(id)) {
      if (TextUtils.isEmpty(name)) {
        return 0;
      }

      return name.hashCode();
    }

    int result = id.hashCode();

    if (TextUtils.isEmpty(name)) {
      return result;
    }

    result = 31 * result + name.hashCode();
    return result;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCoverPath() {
    return coverPath;
  }

  public void setCoverPath(String coverPath) {
    this.coverPath = coverPath;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getDateAdded() {
    return dateAdded;
  }

  public void setDateAdded(long dateAdded) {
    this.dateAdded = dateAdded;
  }

  public List<Video> getVideos() {
    return videos;
  }

  public void setVideos(List<Video> videos) {
    if (videos == null) return;
    for (int i = 0, j = 0, num = videos.size(); i < num; i++) {
        Video p = videos.get(j);
        if (p == null || !FileUtils.fileIsExists(p.getPath())) {
          videos.remove(j);
        } else {
            j++;
        }
    }
    this.videos = videos;
  }

  public List<String> getPhotoPaths() {
    List<String> paths = new ArrayList<>(videos.size());
    for (Video video : videos) {
      paths.add(video.getPath());
    }
    return paths;
  }

  public void addPhoto(int id, String path) {
    if (FileUtils.fileIsExists(path)) {
      videos.add(new Video(id, path));
    }
  }

}
