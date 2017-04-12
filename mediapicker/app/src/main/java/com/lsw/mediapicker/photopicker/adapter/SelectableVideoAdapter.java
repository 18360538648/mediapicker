package com.lsw.mediapicker.photopicker.adapter;

import android.support.v7.widget.RecyclerView;

import com.lsw.mediapicker.photopicker.entity.Video;
import com.lsw.mediapicker.photopicker.entity.VideoDirectory;
import com.lsw.mediapicker.photopicker.event.VideoSelectable;

import java.util.ArrayList;
import java.util.List;


public abstract class SelectableVideoAdapter<VH extends RecyclerView.ViewHolder>
    extends RecyclerView.Adapter<VH> implements VideoSelectable {

  private static final String TAG = SelectableVideoAdapter.class.getSimpleName();

  protected List<VideoDirectory> videoDirectories;
  protected List<String> selectedPhotos;

  public int currentDirectoryIndex = 0;


  public SelectableVideoAdapter() {
    videoDirectories = new ArrayList<>();
    selectedPhotos = new ArrayList<>();
  }


  /**
   * Indicates if the item at position where is selected
   *
   * @param video
   * @return true if the item is selected, false otherwise
   */
  @Override public boolean isSelected(Video video) {
    return getSelectedPhotos().contains(video.getPath());
  }

  /**
   * Toggle the selection status of the item at a given position
   *
   * @param video
   */
  @Override public void toggleSelection(Video video) {
    if (selectedPhotos.contains(video.getPath())) {
      selectedPhotos.remove(video.getPath());
    } else {
      selectedPhotos.add(video.getPath());
    }
  }


  /**
   * Clear the selection status for all items
   */
  @Override public void clearSelection() {
    selectedPhotos.clear();
  }


  /**
   * Count the selected items
   *
   * @return Selected items count
   */
  @Override public int getSelectedItemCount() {
    return selectedPhotos.size();
  }


  public void setCurrentDirectoryIndex(int currentDirectoryIndex) {
      this.currentDirectoryIndex = currentDirectoryIndex;
  }


  public List<Video> getCurrentVideos() {
    return videoDirectories.get(currentDirectoryIndex).getVideos();
  }


//  public List<String> getCurrentPhotoPaths() {
//    List<String> currentPhotoPaths = new ArrayList<>(getCurrentVideos().size());
//    for (Video video : getCurrentVideos()) {
//      currentPhotoPaths.add(video.getPath());
//    }
//    return currentPhotoPaths;
//  }


  public List<String> getSelectedPhotos() {
    return selectedPhotos;
  }

}