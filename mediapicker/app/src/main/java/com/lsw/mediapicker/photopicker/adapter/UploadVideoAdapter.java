package com.lsw.mediapicker.photopicker.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.lsw.mediapicker.R;
import com.lsw.mediapicker.photopicker.ui.PhotoPickerActivity;
import com.lsw.mediapicker.photopicker.utils.AndroidLifecycleUtils;

import java.io.File;
import java.util.ArrayList;


/**
 * Created by Luosiwei on 2017/3/17.
 */
public class UploadVideoAdapter extends RecyclerView.Adapter<UploadVideoAdapter.PhotoViewHolder> {

    private ArrayList<String> videoPaths = new ArrayList<String>();
    private LayoutInflater inflater;

    private Activity mContext;

    final static int TYPE_ADD = 1;
    final static int TYPE_PHOTO = 2;

    int MAX = 1;

    public UploadVideoAdapter(Activity mContext, ArrayList<String> videoPaths) {
        this.videoPaths = videoPaths;
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
    }


    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        switch (viewType) {
            case TYPE_ADD:
                itemView = inflater.inflate(R.layout.picker_item_add_video, parent, false);
                RelativeLayout relativeLayout = (RelativeLayout) itemView.findViewById(R.id.rl_add_new_video);
                relativeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, PhotoPickerActivity.class);
                        mContext.startActivity(intent);
                        mContext.finish();
                    }
                });
                break;
            case TYPE_PHOTO:
                Log.i("lsw", "TYPE_PHOTO");
                itemView = inflater.inflate(R.layout.picker_item_video, parent, false);
                break;
        }
        return new PhotoViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final PhotoViewHolder holder, final int position) {
        if (getItemViewType(position) == TYPE_PHOTO) {
            if (position != videoPaths.size()) {
                Log.i("lsw", "position------------:" + position);
                Log.i("lsw", "photoPaths.get(position)------------:" + videoPaths.get(position));
                Uri uri = Uri.fromFile(new File(videoPaths.get(position)));
                Log.i("lsw", "11111");
                boolean canLoadImage = AndroidLifecycleUtils.canLoadImage(holder.ivPhoto.getContext());
                Log.i("lsw", "2222");
                if (canLoadImage) {
                    Glide.with(mContext)
                            .load(uri)
                            .centerCrop()
                            .thumbnail(0.1f)
                            .placeholder(R.mipmap.__picker_ic_photo_black_48dp)
                            .error(R.mipmap.__picker_ic_broken_image_black_48dp)
                            .into(holder.ivPhoto);
                }
                Log.i("lsw", "图片加载完毕");
            }


        }
    }


    @Override
    public int getItemCount() {
        int count = videoPaths.size() + 1;
        if (count > MAX) {
            count = MAX;
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == videoPaths.size() && position != MAX) ? TYPE_ADD : TYPE_PHOTO;
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPhoto;
        private View vSelected;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            ivPhoto = (ImageView) itemView.findViewById(R.id.iv_video);
            Log.i("lsw", "ivPhoto------" + ivPhoto);
            vSelected = itemView.findViewById(R.id.video_selected);
            Log.i("lsw", "vSelected------" + vSelected);
            if (vSelected != null) vSelected.setVisibility(View.GONE);
        }
    }

}
