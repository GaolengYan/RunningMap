package com.garry.runningmap.usermodule;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.garry.runningmap.usermodule.LoginFragment;
import com.garry.runningmap.usermodule.RegisterFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GaolengYan on 2018/1/19.
 */

public class LoginRegistViewPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentlist = new ArrayList<>();

    private List<String> list = new ArrayList<>();


    public LoginRegistViewPagerAdapter(FragmentManager fm) {
        super(fm);
        list.add("用户登陆");
        list.add("用户注册");
        fragmentlist.add(LoginFragment.newInstance());
        fragmentlist.add(RegisterFragment.newInstance());
    }

    @Override
    public Fragment getItem(int position) {
        System.out.println("getItem");
        return fragmentlist.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return list.get(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }
}
