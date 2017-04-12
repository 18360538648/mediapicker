package com.lsw.mediapicker.photopicker.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lsw.mediapicker.R;
import com.lsw.mediapicker.photopicker.adapter.UploadPhotoAdapter;
import com.lsw.mediapicker.photopicker.utils.Const;

import java.util.List;

/**
 * Created by Luosiwei on 2017/3/23.
 */
public class UploadPhotoFileActivity extends Activity {
    private UploadPhotoAdapter mediaAdapter;
    List<String> photos = null;
    // 取消发送按钮
    private TextView tvUploadCancel;
    // 发送按钮
    private TextView tvUploadMedia;
    // 输入内容
    private EditText etNote;
    private String noteInf;
    private int userId = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picker_upload_photo);
        photos = getIntent().getStringArrayListExtra("mediapath");
        // 将这一次选中的图片放在selectedPhotos中
        Const.selectedPhotos.addAll(photos);
        initView();

    }

    private void initView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mediaAdapter = new UploadPhotoAdapter(UploadPhotoFileActivity.this, Const.selectedPhotos);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, OrientationHelper.VERTICAL));
        recyclerView.setAdapter(mediaAdapter);
        tvUploadCancel = (TextView) findViewById(R.id.upload_cancel);
        tvUploadCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Const.selectedPhotos.clear();
            }
        });
        tvUploadMedia = (TextView) findViewById(R.id.upload_media);
        tvUploadMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photos.size() > 0) {
                    uploadMediaFile(photos);
                }

            }
        });
        etNote = (EditText) findViewById(R.id.et_note);
    }

    public void uploadMediaFile(List<String> photos) {
        noteInf = etNote.getText().toString().trim();
//        String userinfFormSp = SpTools.getString(getApplicationContext(), "userinf", null);
//        if (userinfFormSp != null) {
//            try {
//                JSONObject userJsonObject = new JSONObject(userinfFormSp);
//                if (!userJsonObject.isNull("_id")) {
//                    userId = userJsonObject.getInt("_id");
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        if (userId > -1 ) {
//            BasicUtils.uploadMediaFile(getApplicationContext(), Const.URL + "useracts", photos, userId, noteInf, 1);
//            finish();
//            Const.selectedPhotos.clear();
//        }


    }
}
