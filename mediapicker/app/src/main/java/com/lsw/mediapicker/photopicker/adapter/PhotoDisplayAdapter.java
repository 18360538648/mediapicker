package com.lsw.mediapicker.photopicker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.lsw.mediapicker.R;
import com.lsw.mediapicker.photopicker.entity.Photo;
import com.lsw.mediapicker.photopicker.entity.PhotoDirectory;
import com.lsw.mediapicker.photopicker.event.OnItemCheckListener;
import com.lsw.mediapicker.photopicker.event.OnPhotoClickListener;
import com.lsw.mediapicker.photopicker.utils.AndroidLifecycleUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luosiwei on 2017/3/23.
 */
public class PhotoDisplayAdapter extends SelectableAdapter<PhotoDisplayAdapter.PhotoViewHolder> {
    private int imageSize;
    private RequestManager glide;
    private LayoutInflater inflater;
    private OnItemCheckListener onItemCheckListener = null;
    private OnPhotoClickListener onPhotoClickListener = null;
    private View.OnClickListener onCameraClickListener = null;
    public final static int ITEM_TYPE_PHOTO = 100;

    public PhotoDisplayAdapter(Context context, RequestManager requestManager, List<PhotoDirectory> photoDirectories) {
        this.photoDirectories = photoDirectories;
        this.glide = requestManager;
        inflater = LayoutInflater.from(context);
        selectedPhotos = new ArrayList<>();
        setimageSize(context);
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i("lsw", "viewType__------" + viewType);
        final View itemView = inflater.inflate(R.layout.picker_photo_item, parent, false);
        final PhotoViewHolder holder = new PhotoViewHolder(itemView);
        // 当返回的viewType为拍照的话，给这个位置设置监听
        if (viewType == ITEM_TYPE_PHOTO) {
            holder.vSelected.setVisibility(View.GONE);
            holder.ivPhoto.setScaleType(ImageView.ScaleType.CENTER);
            holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
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
    public void onBindViewHolder(final PhotoViewHolder holder, int position) {
        if (position != 0) {
            if (this.photoDirectories.size() > 0) {
                List<Photo> photos = getCurrentPhotos();
                final Photo photo;
                photo = photos.get(position - 1);
                boolean canLoadImage = AndroidLifecycleUtils.canLoadImage(holder.ivPhoto.getContext());
                if (canLoadImage) {
                    glide
                            .load(new File(photo.getPath()))
                            .centerCrop()
                            .dontAnimate()
                            .thumbnail(0.5f)
                            .override(imageSize, imageSize)
                            .placeholder(R.mipmap.__picker_ic_photo_black_48dp)
                            .error(R.mipmap.__picker_ic_broken_image_black_48dp)
                            .into(holder.ivPhoto);
                }

                final boolean isChecked = isSelected(photo);

                holder.vSelected.setSelected(isChecked);
                holder.ivPhoto.setSelected(isChecked);

                holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (onPhotoClickListener != null) {
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

                        if (onItemCheckListener != null) {
                            isEnable = onItemCheckListener.onItemCheck(pos, photo,
                                    getSelectedPhotos().size() + (isSelected(photo) ? -1 : 1));
                        }
                        if (isEnable) {
                            toggleSelection(photo);
                            notifyItemChanged(pos);
                        }
                    }
                });

            }
        } else {
            holder.ivPhoto.setImageResource(R.mipmap.photo);
            holder.ivPhoto.setScaleType(ImageView.ScaleType.CENTER);
            holder.vSelected.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        int photosCount =
                photoDirectories.size() == 0 ? 0 : getCurrentPhotos().size();
        return photosCount + 1;

    }

    public void setOnPhotoClickListener(OnPhotoClickListener onPhotoClickListener) {
        this.onPhotoClickListener = onPhotoClickListener;
    }

    public void setOnCameraClickListener(View.OnClickListener onCameraClickListener) {
        this.onCameraClickListener = onCameraClickListener;
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPhoto;
        private View vSelected;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            ivPhoto = (ImageView) itemView.findViewById(R.id.iv_photo);
            vSelected = itemView.findViewById(R.id.cb_img);
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
        Log.i("lsw", "imageSize-----" + imageSize);
    }

    @Override
    public void onViewRecycled(PhotoViewHolder holder) {
        Glide.clear(holder.ivPhoto);
        super.onViewRecycled(holder);
    }

    public ArrayList<String> getSelectedPhotoPaths() {
        ArrayList<String> selectedPhotoPaths = new ArrayList<>(getSelectedItemCount());

        for (String photo : selectedPhotos) {
            selectedPhotoPaths.add(photo);
        }

        return selectedPhotoPaths;
    }

    public void setOnItemCheckListener(OnItemCheckListener onItemCheckListener) {
        this.onItemCheckListener = onItemCheckListener;
    }

    /**
     * 根据位置返回viewType
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return position == 0 ? ITEM_TYPE_PHOTO : 0;
    }
}
