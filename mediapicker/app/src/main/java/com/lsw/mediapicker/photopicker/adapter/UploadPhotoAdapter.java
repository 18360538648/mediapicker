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
public class UploadPhotoAdapter extends RecyclerView.Adapter<UploadPhotoAdapter.PhotoViewHolder> {

    private ArrayList<String> photoPaths = new ArrayList<String>();
    private LayoutInflater inflater;

    private Activity mContext;

    final static int TYPE_ADD = 1;
    final static int TYPE_PHOTO = 2;

    int MAX = 9;

    public UploadPhotoAdapter(Activity mContext, ArrayList<String> photoPaths) {
        this.photoPaths = photoPaths;
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
    }


    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        switch (viewType) {
            case TYPE_ADD:
                itemView = inflater.inflate(R.layout.picker_item_add_pic, parent, false);
                RelativeLayout relativeLayout = (RelativeLayout) itemView.findViewById(R.id.rl_add_new);
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
                itemView = inflater.inflate(R.layout.picker_item_photo, parent, false);
                break;
        }
        return new PhotoViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final PhotoViewHolder holder, final int position) {
        if (getItemViewType(position) == TYPE_PHOTO) {
            if (position != photoPaths.size()) {
                Log.i("lsw", "position------------:" + position);
                Log.i("lsw", "photoPaths.get(position)------------:" + photoPaths.get(position));
                Uri uri = Uri.fromFile(new File(photoPaths.get(position)));
                boolean canLoadImage = AndroidLifecycleUtils.canLoadImage(holder.ivPhoto.getContext());
                if (canLoadImage) {
                    Glide.with(mContext)
                            .load(uri)
                            .centerCrop()
                            .thumbnail(0.1f)
                            .placeholder(R.mipmap.__picker_ic_photo_black_48dp)
                            .error(R.mipmap.__picker_ic_broken_image_black_48dp)
                            .into(holder.ivPhoto);
                }
            }


        }
    }


    @Override
    public int getItemCount() {
        int count = photoPaths.size() + 1;
        if (count > MAX) {
            count = MAX;
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == photoPaths.size() && position != MAX) ? TYPE_ADD : TYPE_PHOTO;
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPhoto = (ImageView) itemView.findViewById(R.id.iv_photo);
        private View vSelected;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            vSelected = itemView.findViewById(R.id.v_selected);
            if (vSelected != null) vSelected.setVisibility(View.GONE);
        }
    }

}
