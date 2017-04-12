package com.lsw.mediapicker.photopicker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.lsw.mediapicker.R;
import com.lsw.mediapicker.photopicker.fragment.PhotoFragment;
import com.lsw.mediapicker.photopicker.fragment.VideoFragment;

import java.util.ArrayList;
import java.util.List;


public class PhotoPickerActivity extends FragmentActivity {
    // PicFragmentIndex
    private static final int PicViewPagerIndex = 0;
    // VideoFragmentIndex
    private static final int VideoViewPagerIndex = 1;
    private ViewPager viewPager;
    // 展示照片Fragment
    private PhotoFragment photoFragment;
    // 展示视频Fragment
    private VideoFragment videoFragment;
    // 存放Fragment集合
    private List<Fragment> fragmentList;
    // 菜单RadioGroup
    private RadioGroup radioGroup;
    // 显示图片按钮
    private RadioButton rbtnPic;
    // 显示视频按钮
    private RadioButton rbtnVideo;
    // FragmentAdapter
    private MyPageFramgentAdapter myPageFramgentAdapter;
    private ImageButton ibtnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_picker);
        initView();

    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        photoFragment = new PhotoFragment();
        videoFragment = new VideoFragment();
        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(photoFragment);
        fragmentList.add(videoFragment);
        radioGroup = (RadioGroup) findViewById(R.id.rg_menue);
        rbtnPic = (RadioButton) findViewById(R.id.rbtn_pic);
        rbtnVideo = (RadioButton) findViewById(R.id.rbtn_video);
        ibtnBack = (ImageButton) findViewById(R.id.ibtn_back);
        ibtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbtn_pic:
                        viewPager.setCurrentItem(PicViewPagerIndex);
                        break;

                    case R.id.rbtn_video:
                        viewPager.setCurrentItem(VideoViewPagerIndex);
                        break;
                }
            }
        });
        FragmentManager fm = getSupportFragmentManager();
        myPageFramgentAdapter = new MyPageFramgentAdapter(fm);
        viewPager.setAdapter(myPageFramgentAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case PicViewPagerIndex:
                        rbtnPic.setChecked(true);
                        break;
                    case VideoViewPagerIndex:
                        rbtnVideo.setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public PhotoPickerActivity getActivity() {
        return this;
    }

    // viewPager所需的适配器
    class MyPageFramgentAdapter extends FragmentPagerAdapter {

        public MyPageFramgentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }
}
