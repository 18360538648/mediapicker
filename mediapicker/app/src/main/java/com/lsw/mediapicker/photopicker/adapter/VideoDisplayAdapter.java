package com.lsw.mediapicker.photopicker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.lsw.mediapicker.R;
import com.lsw.mediapicker.photopicker.entity.Video;
import com.lsw.mediapicker.photopicker.entity.VideoDirectory;
import com.lsw.mediapicker.photopicker.event.OnItemVideoCheckListener;
import com.lsw.mediapicker.photopicker.event.OnVideoClickListener;
import com.lsw.mediapicker.photopicker.utils.AndroidLifecycleUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luosiwei on 2017/3/23.
 */
public class VideoDisplayAdapter extends SelectableVideoAdapter<VideoDisplayAdapter.VideoViewHolder> {
    private int imageSize;
    private RequestManager glide;
    private LayoutInflater inflater;
    private OnItemVideoCheckListener onVideoItemCheckListener = null;
    private OnVideoClickListener onVideoClickListener = null;
    private View.OnClickListener onCameraClickListener = null;
    public final static int ITEM_TYPE_CAMERA = 100;

    public VideoDisplayAdapter(Context context, RequestManager requestManager, List<VideoDirectory> videoDirectories) {
        this.videoDirectories = videoDirectories;
        this.glide = requestManager;
        inflater = LayoutInflater.from(context);
        selectedPhotos = new ArrayList<>();
        setimageSize(context);
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = inflater.inflate(R.layout.videoitem, parent, false);
        final VideoViewHolder holder = new VideoViewHolder(itemView);
        if (viewType == ITEM_TYPE_CAMERA) {
            holder.vSelected.setVisibility(View.GONE);
            holder.ivVideo.setScaleType(ImageView.ScaleType.CENTER);
            holder.ivVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onCameraClickListener != null) {
                        onCameraClickListener.onClick(view);
                    }
                }
            });
        }
        return holder;
    }


    @Override
    public void onBindViewHolder(final VideoViewHolder holder, int position) {
        if (position != 0) {
             //videoDirectories 应该是文件对象
            if (this.videoDirectories.size() > 0) {
                List<Video> videos = getCurrentVideos();
                final Video video;
                video = videos.get(position-1);
                boolean canLoadImage = AndroidLifecycleUtils.canLoadImage(holder.ivVideo.getContext());
                if (canLoadImage) {
                    glide
                            .load(new File(video.getPath()))
                            .centerCrop()
                            .dontAnimate()
                            .thumbnail(0.5f)
                            .override(imageSize, imageSize)
                            .placeholder(R.mipmap.__picker_ic_photo_black_48dp)
                            .error(R.mipmap.__picker_ic_broken_image_black_48dp)
                            .into(holder.ivVideo);
                }

                final boolean isChecked = isSelected(video);

                holder.vSelected.setSelected(isChecked);
                holder.ivVideo.setSelected(isChecked);

                holder.ivVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (onVideoClickListener != null) {
                            int pos = holder.getAdapterPosition();
                            holder.vSelected.performClick();
                        }
                    }
                });
                holder.vSelected.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int pos = holder.getAdapterPosition();
                        boolean isEnable = true;

                        if (onVideoItemCheckListener != null) {
                            isEnable = onVideoItemCheckListener.onItemCheck(pos, video,
                                    getSelectedPhotos().size() + (isSelected(video) ? -1 : 1));
                        }
                        if (isEnable) {
                            toggleSelection(video);
                            notifyItemChanged(pos);
                        }
                    }
                });
            }
        } else {
            holder.ivVideo.setImageResource(R.mipmap.camera);
            holder.ivVideo.setScaleType(ImageView.ScaleType.CENTER);
            holder.vSelected.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        int photosCount =
                videoDirectories.size() == 0 ? 0 : getCurrentVideos().size();
        return photosCount + 1;

    }

    public void setOnPhotoClickListener(OnVideoClickListener onVideoClickListener) {
        this.onVideoClickListener = onVideoClickListener;
    }

    public void setOnCameraClickListener(View.OnClickListener onCameraClickListener) {
        this.onCameraClickListener = onCameraClickListener;
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivVideo;
        private View vSelected;

        public VideoViewHolder(View itemView) {
            super(itemView);
            ivVideo = (ImageView) itemView.findViewById(R.id.iv_video);
            vSelected = itemView.findViewById(R.id.cb_video);
        }
    }

    /**
     * 设置图片尺寸
     *
     * @param context
     */
    private void setimageSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        int widthPixels = metrics.widthPixels;
        imageSize = widthPixels / 4;
    }

    @Override
    public void onViewRecycled(VideoViewHolder holder) {
        Glide.clear(holder.ivVideo);
        super.onViewRecycled(holder);
    }

    public ArrayList<String> getSelectedVideoPaths() {
        ArrayList<String> selectedVideoPaths = new ArrayList<>(getSelectedItemCount());

        for (String video : selectedPhotos) {
            selectedVideoPaths.add(video);
        }

        return selectedVideoPaths;
    }

    public void setOnItemVideoCheckListener(OnItemVideoCheckListener onItemVideoCheckListener) {
        this.onVideoItemCheckListener = onItemVideoCheckListener;
    }
    /**
     * 根据位置返回viewType
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return position == 0 ? ITEM_TYPE_CAMERA : 0;
    }
}
