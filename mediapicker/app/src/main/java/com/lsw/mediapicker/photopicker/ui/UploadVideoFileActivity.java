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
import com.lsw.mediapicker.photopicker.adapter.UploadVideoAdapter;
import com.lsw.mediapicker.photopicker.utils.Const;

import java.util.List;

/**
 * Created by Luosiwei on 2017/3/23.
 */
public class UploadVideoFileActivity extends Activity {
    private UploadVideoAdapter videoAdapter;
    List<String> videos = null;
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
        setContentView(R.layout.picker_upload_video);
        videos = getIntent().getStringArrayListExtra("videopath");
        // 将这一次选中的图片放在selectedVideos中
        Const.selectedVideos.addAll(videos);
        initView();

    }

    private void initView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_video);
        videoAdapter = new UploadVideoAdapter(UploadVideoFileActivity.this, Const.selectedVideos);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, OrientationHelper.VERTICAL));
        recyclerView.setAdapter(videoAdapter);
        tvUploadCancel = (TextView) findViewById(R.id.upload_video_cancel);
        tvUploadCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Const.selectedVideos.clear();
            }
        });
        tvUploadMedia = (TextView) findViewById(R.id.upload_video);
        tvUploadMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videos.size() > 0) {
                    uploadMediaFile(videos);
                }

            }
        });
        etNote = (EditText) findViewById(R.id.et_video_note);
    }

    public void uploadMediaFile(List<String> videos) {
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
//        if (userId > -1) {
//            BasicUtils.uploadMediaFile(getApplicationContext(), Const.URL + "useracts", videos, userId, noteInf, 2);
//            finish();
//            Const.selectedVideos.clear();
//        }


    }
}
