package com.lsw.mediapicker.photopicker.event;


import com.lsw.mediapicker.photopicker.entity.Video;

/**
 * Created by donglua on 15/6/30.
 */
public interface VideoSelectable {


  /**
   * Indicates if the item at position position is selected
   *
   * @param video Photo of the item to check
   * @return true if the item is selected, false otherwise
   */
  boolean isSelected(Video video);

  /**
   * Toggle the selection status of the item at a given position
   *
   * @param video Photo of the item to toggle the selection status for
   */
  void toggleSelection(Video video);

  /**
   * Clear the selection status for all items
   */
  void clearSelection();

  /**
   * Count the selected items
   *
   * @return Selected items count
   */
  int getSelectedItemCount();

}
