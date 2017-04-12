package com.lsw.mediapicker.photopicker.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.lsw.mediapicker.R;
import com.lsw.mediapicker.photopicker.UploadVideoFileActivity;
import com.lsw.mediapicker.photopicker.adapter.VideoDisplayAdapter;
import com.lsw.mediapicker.photopicker.entity.Video;
import com.lsw.mediapicker.photopicker.entity.VideoDirectory;
import com.lsw.mediapicker.photopicker.event.OnItemVideoCheckListener;
import com.lsw.mediapicker.photopicker.utils.AndroidLifecycleUtils;
import com.lsw.mediapicker.photopicker.utils.ImageCaptureManager;
import com.lsw.mediapicker.photopicker.utils.MediaStoreHelper;
import com.lsw.mediapicker.photopicker.utils.PermissionsConstant;
import com.lsw.mediapicker.photopicker.utils.PermissionsUtils;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.widget.Toast.LENGTH_LONG;
import static com.lsw.mediapicker.photopicker.utils.MediaStoreHelper.INDEX_ALL_PHOTOS;

/**
 * Created by Luosiwei on 2017/3/22.
 */
public class VideoFragment extends Fragment {
    private VideoDisplayAdapter videoDisplayAdapter;
    private ImageCaptureManager captureManager;
    //所有photos的路径
    private List<VideoDirectory> directories;
    private RequestManager mGlideRequestManager;
    private int SCROLL_THRESHOLD = 30;
    private Button btnSubmit;
//    private Map<String, String> submitMediaMap = new HashMap();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mGlideRequestManager = Glide.with(this);

        directories = new ArrayList<>();
        videoDisplayAdapter = new VideoDisplayAdapter(getActivity(), mGlideRequestManager, directories);
        Bundle mediaStoreArgs = new Bundle();
        MediaStoreHelper.getVidoDirs(getActivity(), mediaStoreArgs,
                new MediaStoreHelper.VideosResultCallback() {
                    @Override
                    public void onResultCallback(List<VideoDirectory> dirs) {
                        Log.i("lsw", "video result------" + dirs);
                        directories.clear();
                        directories.addAll(dirs);
                        videoDisplayAdapter.notifyDataSetChanged();
                    }
                });

        captureManager = new ImageCaptureManager(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_videos);
        // 第一个参数为列数，将屏幕分几列
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(4, OrientationHelper.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        Log.i("lsw", "layoutManager:" + layoutManager);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(videoDisplayAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        btnSubmit = (Button) view.findViewById(R.id.btn_submit_video);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("lsw", "btnSubmit");
                Intent intent = new Intent(getActivity(), UploadVideoFileActivity.class);
                ArrayList<String> selectedVideos = videoDisplayAdapter.getSelectedVideoPaths();
                intent.putStringArrayListExtra("videopath", selectedVideos);
                startActivity(intent);
                getActivity().finish();
            }
        });
        videoDisplayAdapter.setOnCameraClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!PermissionsUtils.checkCameraPermission(VideoFragment.this)) return;
                if (!PermissionsUtils.checkWriteStoragePermission(VideoFragment.this)) return;
                openCamera();
            }
        });
        videoDisplayAdapter.setOnItemVideoCheckListener(new OnItemVideoCheckListener() {
            @Override
            public boolean onItemCheck(int position, Video path, int selectedItemCount) {
                if (selectedItemCount > 1) {
                    Toast.makeText(getActivity(), "选择的视频个数不能大于1个",
                            LENGTH_LONG).show();
                    return false;
                }
                return true;
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (Math.abs(dy) > SCROLL_THRESHOLD) {
                    mGlideRequestManager.pauseRequests();
                } else {
                    resumeRequestsIfNotDestroyed();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    resumeRequestsIfNotDestroyed();
                }
            }
        });
        return view;
    }

    private void openCamera() {
        Intent intent = new Intent(
                android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
        startActivity(intent);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ImageCaptureManager.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

            if (captureManager == null) {
                FragmentActivity activity = getActivity();
                captureManager = new ImageCaptureManager(activity);
            }

            captureManager.galleryAddPic();
            if (directories.size() > 0) {
                String path = captureManager.getCurrentPhotoPath();
                VideoDirectory directory = directories.get(INDEX_ALL_PHOTOS);
                directory.getVideos().add(INDEX_ALL_PHOTOS, new Video(path.hashCode(), path));
                directory.setCoverPath(path);
                videoDisplayAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case PermissionsConstant.REQUEST_CAMERA:
                case PermissionsConstant.REQUEST_EXTERNAL_WRITE:
                    if (PermissionsUtils.checkWriteStoragePermission(this) &&
                            PermissionsUtils.checkCameraPermission(this)) {
                        openCamera();
                    }
                    break;
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        captureManager.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        captureManager.onRestoreInstanceState(savedInstanceState);
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (directories == null) {
            return;
        }

        for (VideoDirectory directory : directories) {
            directory.getPhotoPaths().clear();
            directory.getVideos().clear();
            directory.setVideos(null);
        }
        directories.clear();
        directories = null;
    }

    private void resumeRequestsIfNotDestroyed() {
        if (!AndroidLifecycleUtils.canLoadImage(this)) {
            return;
        }

        mGlideRequestManager.resumeRequests();
    }
}
