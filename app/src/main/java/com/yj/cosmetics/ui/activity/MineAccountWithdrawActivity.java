package com.yj.cosmetics.ui.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yj.cosmetics.R;
import com.yj.cosmetics.base.BaseActivity;
import com.yj.cosmetics.base.Key;
import com.yj.cosmetics.base.URLBuilder;
import com.yj.cosmetics.model.AccountWithEntity;
import com.yj.cosmetics.util.LogUtils;
import com.yj.cosmetics.util.ToastUtils;
import com.yj.cosmetics.util.Utils;
import com.yj.cosmetics.widget.Dialog.CustomPostDialog;
import com.zhy.http.okhttp.OkHttpUtils;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Suo on 2018/3/15.
 * 已经修改
 */

public class MineAccountWithdrawActivity extends BaseActivity {
	@BindView(R.id.mine_account_withdraw_etmoney)
	EditText etMoney;
	@BindView(R.id.mine_account_withdraw_tv_money)
	TextView tvMoney;
	@BindView(R.id.et_alipay_name)
	EditText etAlipayName;
	@BindView(R.id.et_alipay_account)
	EditText etAlipayAccount;
	@BindView(R.id.withdraw_btn_confirm)
	Button btnConfirm;
	@BindView(R.id.scrollView)
	ScrollView mScrollView;
	@BindView(R.id.tv_cash)
	TextView tvCash;
	private CustomPostDialog postDialog;
	private boolean isScroll = false;
	private String userMoney, cashRate;
	private DecimalFormat df;
//	private KeyboardUtil keyboardUtil;

	@Override
	protected int getContentView() {
		return R.layout.activity_mine_account_withdraw;
	}

	@Override
	protected void initView() {
		setTitleText("提现");
		userMoney = getIntent().getStringExtra("userMoney");
		cashRate = getIntent().getStringExtra("cashRate");
		if (null != userMoney && !TextUtils.isEmpty(userMoney)) {
			tvMoney.setText(userMoney);
		}
		if (null != cashRate && !TextUtils.isEmpty(cashRate)) {
			tvCash.setText("提现费率：" + cashRate);
		}
	}

	@Override
	protected void initData() {
		etMoney.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				scrollVertical(mScrollView.getHeight());
				return false;
			}
		});
		etAlipayName.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				scrollVertical(mScrollView.getHeight());
				return false;
			}
		});
		etAlipayAccount.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				scrollVertical(mScrollView.getHeight());
				return false;
			}
		});
		EditLister();
//		keyboardUtil = new KeyboardUtil(MineAccountWithdrawActivity.this, false);
	}

	@OnClick({R.id.withdraw_btn_confirm, R.id.mine_account_withdraw_tv_all})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.withdraw_btn_confirm:
				if (btnConfirm.isEnabled()) {

					String name = etAlipayName.getText().toString().trim();
					if (TextUtils.isEmpty(name)) {
						ToastUtils.showToast(this, "请输入支付宝姓名");
						return;
					}
					String account = etAlipayAccount.getText().toString().trim();
					if (TextUtils.isEmpty(account)) {
						ToastUtils.showToast(this, "请输入支付宝账户");
						return;
					}
					String money = etMoney.getText().toString().trim();
					if (TextUtils.isEmpty(money)) {
						ToastUtils.showToast(this, "请输入要提现的金额");
						return;
					}
					if (money.contains(".") && money.substring(money.indexOf(".")).length() > 3) {
						//小数位大于2
						ToastUtils.showToast(this, "提现小数不得大于两位数,请重新输入");
						return;
					}
					float have = Float.parseFloat(userMoney);
					float want = Float.parseFloat(etMoney.getText().toString().trim());
					if (have < want) {
						ToastUtils.showToast(this, "余额不足,请重新输入");
						return;
					}


					if (!TextUtils.isEmpty(etMoney.getText().toString().trim()) && !etMoney.getText().toString().trim().equals("")) {
						//@TODO --------------------------
						if (!etMoney.getText().toString().trim().equals("0")
								&& !etMoney.getText().toString().trim().equals("0.0")
								&& !etMoney.getText().toString().trim().equals("0.")
								&& !etMoney.getText().toString().trim().equals("0.00")
								&& !etMoney.getText().toString().trim().equals("0.000")
						) {
							btnConfirm.setEnabled(false);
							doAsyncPost();
//						mPresenter.payWithAlipay(mUtils.getUid(), etInputMoney.getText().toString().trim(), sellersId, marketingType);
						} else {
							ToastUtils.showToast(MineAccountWithdrawActivity.this, "金额不能为0");
						}
					} else {
						ToastUtils.showToast(MineAccountWithdrawActivity.this, "请输入金额");
					}
				}else {
					ToastUtils.showToast(this, "请勿重复提现");
				}
				break;
			case R.id.mine_account_withdraw_tv_all:
				if (!TextUtils.isEmpty(userMoney)) {
					float bal = Float.parseFloat(userMoney);
					if (bal > 0) {
						etMoney.setText(userMoney);
					}
				}
				break;
		}
	}

	private void EditLister() {
//		checkbox.setChecked(true);
//		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		df = new DecimalFormat("###########0");
		etMoney.setFilters(new InputFilter[]{new InputFilter() {
			@Override
			public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
				if (source.equals(".") && dest.toString().length() == 0) {
					return "0.";
				}

				if (dest.toString().contains(".")) {
					int index = dest.toString().indexOf(".");
					int length = dest.toString().substring(index).length();
					if (source.equals(".")) {
						return "";
					}
				}
				return null;
			}
		}});

		etMoney.addTextChangedListener(new TextWatcher() {
			//			private Integer integer;
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
				if (start == 3) {
					if (charSequence.toString().length() == 4) {
						if (charSequence.toString().contains(".")) {
							return;
						}
					}
				}
//				boolean numeric = StringUtils.isNumeric(String.valueOf(charSequence));
//				if (numeric){
//					tvPay.setEnabled(true);
//				}else {
//					tvPay.setEnabled(false);
//				}
				if (charSequence.length() > 8) { //判断EditText中输入的字符数是不是已经大于6
					etMoney.setText(charSequence.toString().substring(0, 8)); //设置EditText只显示前面6位字符
					etMoney.setSelection(8);//让光标移至末端
//					ToastUtils.showToast(StorePayActivity.this, "输入字数已达上限");
					return;
				}


				if (!"".equals(charSequence.toString())) {
					String inputPic = charSequence.toString();
					Double aDouble = Double.valueOf(inputPic);
					String format = df.format(aDouble);
				} else {
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}


	private void doAsyncPost() {
		Map<String, String> map = new HashMap<>();
		map.put("userId", mUtils.getUid());
		map.put("alipayName", etAlipayName.getText().toString().trim());
		map.put("alipayAccount", etAlipayAccount.getText().toString().trim());
		map.put("money", etMoney.getText().toString().trim());
		LogUtils.e("changeName传输的值" + URLBuilder.format(map));
		OkHttpUtils.post().url(URLBuilder.URLBaseHeader + "/phone/user/saveCash")
				.addParams(Key.data, URLBuilder.format(map))
				.tag(this).build().execute(new Utils.MyResultCallback<AccountWithEntity>() {

			@Override
			public AccountWithEntity parseNetworkResponse(Response response) throws Exception {
				String json = response.body().string().trim();
				LogUtils.e("json的值" + json);
				return new Gson().fromJson(json, AccountWithEntity.class);
			}

			@Override
			public void onResponse(final AccountWithEntity response) {
				if (response != null && response.getCode().equals("200")) {
					//返回值为200 说明请求成功
					if (postDialog == null) {
						postDialog = new CustomPostDialog(MineAccountWithdrawActivity.this);
					}
					if (!postDialog.isShowing()) {
						postDialog.show();
					}
					btnConfirm.postDelayed(new Runnable() {
						@Override
						public void run() {
							Intent intent = new Intent(MineAccountWithdrawActivity.this, WithdrawalSuccessActivity.class);
							intent.putExtra("insertTime", response.getData().getInsertTime());
							intent.putExtra("money", response.getData().getMoney());
							startActivity(intent);
							finish();
						}
					}, 600);

				} else {
					ToastUtils.showToast(MineAccountWithdrawActivity.this, "请求失败 :)" + response.getMsg());
					btnConfirm.setEnabled(true);
				}
			}

			@Override
			public void onError(Call call, Exception e) {
				btnConfirm.setEnabled(true);
				super.onError(call, e);
				if (call.isCanceled()) {
					call.cancel();
				} else {
					ToastUtils.showToast(MineAccountWithdrawActivity.this, "网络故障,请稍后再试");
				}

			}
		});
	}

	/**
	 * 使滚动条滚动至指定位置（垂直滚动）
	 *
	 * @param to 滚动到的位置
	 */
	protected void scrollVertical(final int to) {
		if (!isScroll) {
			isScroll = true;
			mScrollView.postDelayed(new Runnable() {
				@Override
				public void run() {
					LogUtils.i("弹出后的高度是" + to);
					mScrollView.scrollTo(0, to);
					isScroll = false;
				}
			}, 500);
		}
	}

	private void dismissDialog2() {
		if (postDialog != null) {
			postDialog.dismiss();
			postDialog = null;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dismissDialog2();
		OkHttpUtils.getInstance().cancelTag(this);
	}

}
