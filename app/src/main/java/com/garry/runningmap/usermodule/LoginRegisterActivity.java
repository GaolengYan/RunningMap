package com.garry.runningmap.usermodule;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.garry.runningmap.R;

public class LoginRegisterActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);
        initViews();
        mViewPager.setAdapter(new LoginRegistViewPagerAdapter(getSupportFragmentManager()));
        mTabLayout.setupWithViewPager(mViewPager);

    }

    /**
     * 初始化控件
     */
    private void initViews(){
        this.mTabLayout = (TabLayout) findViewById(R.id.tablayout);
        this.mViewPager = (ViewPager) findViewById(R.id.viewpager);
    }

}
