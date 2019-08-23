package com.yj.cosmetics.ui.fragment;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yj.cosmetics.R;
import com.yj.cosmetics.base.BaseFragment;
import com.yj.cosmetics.ui.adapter.MineOrderTabAdapter;
import com.yj.cosmetics.util.TabUtils;
import com.yj.cosmetics.widget.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Administrator on 2019/6/15 0015.
 */

public class MyFreeOrderFragment extends BaseFragment {
	@BindView(R.id.tablayout)
	TabLayout tabLayout;
	@BindView(R.id.tv_mine_store_num)
	TextView tvStoreNum;
	@BindView(R.id.tv_mine_free_sayest)
	TextView tvMoney;
	@BindView(R.id.tv_account_pic)
	TextView tvAccountPic;
	@BindView(R.id.tv_total_store)
	TextView tvTotalStore;
	@BindView(R.id.viewpager)
	NoScrollViewPager mViewpager;
	private List<String> mTitle = new ArrayList<>();
	private List<Fragment> mFragment = new ArrayList<>();

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = createView(inflater.inflate(R.layout.fragment_account_list, container, false));
		return view;
	}

	@Override
	protected void initData() {
		mTitle.add("我的消费");
		mTitle.add("我的免单");
		for (int i = 0; i < mTitle.size(); i++) {
			mFragment.add(MyAccountListFragment.instant(i));
		}
		MineOrderTabAdapter adapter = new MineOrderTabAdapter(getChildFragmentManager(), mTitle, mFragment);
		mViewpager.setAdapter(adapter);
		//为TabLayout设置ViewPager
		tabLayout.setupWithViewPager(mViewpager);
		tabLayout.setTabsFromPagerAdapter(adapter);

		tabLayout.post(new Runnable() {
			@Override
			public void run() {
				TabUtils.setIndicator(tabLayout, 40, 40);
			}
		});
	}

	public void process(String str, String s, int type) {
		tvMoney.setText(str);
		tvStoreNum.setText(s);
		if (type == 0) {
			tvAccountPic.setText("累计消费（元）");
			tvTotalStore.setText("累计消费（单）");
		} else if (type == 1) {
			tvAccountPic.setText("累计免单（元）");
			tvTotalStore.setText("已免单（单）");
		}
	}
}
