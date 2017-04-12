package com.lsw.mediapicker.photopicker.event;


import com.lsw.mediapicker.photopicker.entity.Video;

/**
 * Created by donglua on 15/6/20.
 */
public interface OnItemVideoCheckListener {

  /***
   *
   * @param position 所选图片的位置
   * @param path     所选的图片
   * @param selectedItemCount  已选数量
   * @return enable check
   */
  boolean onItemCheck(int position, Video path, int selectedItemCount);

}
