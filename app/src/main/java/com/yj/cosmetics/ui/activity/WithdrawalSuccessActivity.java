package com.yj.cosmetics.ui.activity;


import android.view.View;
import android.widget.TextView;

import com.yj.cosmetics.R;
import com.yj.cosmetics.base.BaseActivity;

import butterknife.BindView;

public class WithdrawalSuccessActivity extends BaseActivity {

	@BindView(R.id.tv_money)
	TextView tvMoney;
	@BindView(R.id.tv_time)
	TextView tvTime;
	@BindView(R.id.tv_logout)
	TextView tvLogout;

	String insertTime, money;

	@Override
	protected int getContentView() {
		return R.layout.activity_with_success;
	}

	@Override
	protected void initView() {
		setTitleText("提现成功");
		hideTitle();
	}

	@Override
	protected void initData() {
		insertTime = getIntent().getStringExtra("insertTime");
		money = getIntent().getStringExtra("money");
		tvMoney.setText("￥" + money);
		tvTime.setText(insertTime);
		tvLogout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
