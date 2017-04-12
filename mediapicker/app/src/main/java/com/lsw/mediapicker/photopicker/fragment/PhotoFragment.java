package com.lsw.mediapicker.photopicker.fragment;

import android.content.ActivityNotFoundException;
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
import com.lsw.mediapicker.photopicker.adapter.PhotoDisplayAdapter;
import com.lsw.mediapicker.photopicker.entity.Photo;
import com.lsw.mediapicker.photopicker.entity.PhotoDirectory;
import com.lsw.mediapicker.photopicker.event.OnItemCheckListener;
import com.lsw.mediapicker.photopicker.ui.UploadPhotoFileActivity;
import com.lsw.mediapicker.photopicker.utils.AndroidLifecycleUtils;
import com.lsw.mediapicker.photopicker.utils.Const;
import com.lsw.mediapicker.photopicker.utils.ImageCaptureManager;
import com.lsw.mediapicker.photopicker.utils.MediaStoreHelper;
import com.lsw.mediapicker.photopicker.utils.PermissionsConstant;
import com.lsw.mediapicker.photopicker.utils.PermissionsUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static android.widget.Toast.LENGTH_LONG;
import static com.lsw.mediapicker.photopicker.utils.MediaStoreHelper.INDEX_ALL_PHOTOS;
/**
 * Created by Luosiwei on 2017/3/22.
 */
public class PhotoFragment extends Fragment {
    private static final String TAG = "lsw";
    private PhotoDisplayAdapter photoDisplayAdapter;
    private ImageCaptureManager captureManager;
    //所有photos的路径
    private List<PhotoDirectory> directories;
    private RequestManager mGlideRequestManager;
    private int SCROLL_THRESHOLD = 30;
    private Button btnSubmit;
    private Map<String, String> submitMediaMap = new HashMap();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mGlideRequestManager = Glide.with(this);
        directories = new ArrayList<>();
        photoDisplayAdapter = new PhotoDisplayAdapter(getActivity(), mGlideRequestManager, directories);
        Bundle mediaStoreArgs = new Bundle();
        MediaStoreHelper.getPhotoDirs(getActivity(), mediaStoreArgs,
                new MediaStoreHelper.PhotosResultCallback() {
                    @Override
                    public void onResultCallback(List<PhotoDirectory> dirs) {
                        Log.i("lsw", "pic result------" + dirs);
                        directories.clear();
                        directories.addAll(dirs);
                        photoDisplayAdapter.notifyDataSetChanged();
                    }
                });

        captureManager = new ImageCaptureManager(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_phote, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_photos);
        btnSubmit = (Button) view.findViewById(R.id.btn_submit_pic);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("lsw", "btnSubmit");
                Intent intent = new Intent(getActivity(), UploadPhotoFileActivity.class);
                ArrayList<String> selectedPhotos = photoDisplayAdapter.getSelectedPhotoPaths();
                intent.putStringArrayListExtra("mediapath", selectedPhotos);
                startActivity(intent);
                getActivity().finish();
            }
        });
        // 第一个参数为列数，将屏幕分几列
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(4, OrientationHelper.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(photoDisplayAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        photoDisplayAdapter.setOnCameraClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!PermissionsUtils.checkCameraPermission(PhotoFragment.this)) return;
                if (!PermissionsUtils.checkWriteStoragePermission(PhotoFragment.this)) return;
                openCamera();
            }
        });
        photoDisplayAdapter.setOnItemCheckListener(new OnItemCheckListener() {
            @Override
            public boolean onItemCheck(int position, Photo path, int selectedItemCount) {
                if (selectedItemCount + Const.selectedPhotos.size() > 9) {
                    Toast.makeText(getActivity(), "选择的图片个数不能大于9张",
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
        try {
            Intent intent = captureManager.dispatchTakePictureIntent();
            startActivityForResult(intent, ImageCaptureManager.REQUEST_TAKE_PHOTO);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ActivityNotFoundException e) {
            // TODO No Activity Found to handle Intent
            e.printStackTrace();
        }
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
                PhotoDirectory directory = directories.get(INDEX_ALL_PHOTOS);
                directory.getPhotos().add(INDEX_ALL_PHOTOS, new Photo(path.hashCode(), path));
                directory.setCoverPath(path);
                photoDisplayAdapter.notifyDataSetChanged();
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

        for (PhotoDirectory directory : directories) {
            directory.getPhotoPaths().clear();
            directory.getPhotos().clear();
            directory.setPhotos(null);
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
