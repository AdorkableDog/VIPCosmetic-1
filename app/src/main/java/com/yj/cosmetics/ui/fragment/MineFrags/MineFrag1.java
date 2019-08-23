package com.yj.cosmetics.ui.fragment.MineFrags;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jude.rollviewpager.OnItemClickListener;
import com.jude.rollviewpager.RollPagerView;
import com.sobot.chat.SobotApi;
import com.sobot.chat.api.enumtype.SobotChatTitleDisplayMode;
import com.sobot.chat.api.model.Information;
import com.sobot.chat.utils.ZhiChiConstant;
import com.yj.cosmetics.R;
import com.yj.cosmetics.base.BaseFragment;
import com.yj.cosmetics.base.URLBuilder;
import com.yj.cosmetics.model.HomeEntity;
import com.yj.cosmetics.model.MineEntity;
import com.yj.cosmetics.model.NormalEntity;
import com.yj.cosmetics.model.QREntity;
import com.yj.cosmetics.ui.activity.MineAboutActivity;
import com.yj.cosmetics.ui.activity.MineAddressManageActivity;
import com.yj.cosmetics.ui.activity.MineCollectionActivity;
import com.yj.cosmetics.ui.activity.MineOrderActivity;
import com.yj.cosmetics.ui.activity.MinePersonalDataActivity;
import com.yj.cosmetics.ui.activity.MineSettingActivity;
import com.yj.cosmetics.ui.activity.NormalWebViewActivity;
import com.yj.cosmetics.ui.activity.QRCodeActivity;
import com.yj.cosmetics.ui.activity.mineAccount.MineAccount2Activity;
import com.yj.cosmetics.ui.adapter.HomeAdapter;
import com.yj.cosmetics.ui.adapter.NormalBannerAdapter;
import com.yj.cosmetics.util.AuthorUtils;
import com.yj.cosmetics.util.DensityUtil;
import com.yj.cosmetics.util.FileUtils;
import com.yj.cosmetics.util.IntentUtils;
import com.yj.cosmetics.util.ToastUtils;
import com.yj.cosmetics.util.Utils;
import com.yj.cosmetics.util.WindowDisplayManager;
import com.yj.cosmetics.widget.CustomHintView;
import com.yj.cosmetics.widget.Dialog.CustomNormalContentDialog;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Suo on 2017/11/18.
 *
 * @TODO 个人中心 2.0版本升级页面
 */

public class MineFrag1 extends BaseFragment implements MineFrag_contract.View {
	@BindView(R.id.tv_title)
	TextView tvTitle;
	@BindView(R.id.frag_mine_tv_info)
	TextView fragMineTvInfo;
	@BindView(R.id.rollPagerView)
	RollPagerView mRollPagerView;
	Unbinder unbinder;
	CustomNormalContentDialog mDialog;
	private MineFrag_contract.Presenter mineFragView = new MineFrag_presenter(this);

	/**
	 * 需要进行检测的权限数组
	 */
	private static final int PERMISSON_REQUESTCODE = 0;
	private boolean isNeedCheck = true;
	protected String[] needPermissions = {
			Manifest.permission.CALL_PHONE
	};

	private String serviceTel;
	private String upintegral;
	private HomeEntity.HomeData data;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = createView(inflater.inflate(R.layout.fragment_mine1, container, false));
		unbinder = ButterKnife.bind(this, view);
		return view;
	}

	@Override
	protected void initData() {
		mineFragView.subscribe();
		regReceiver();
		doAsyncGetData();
		tvTitle.setText("菲梵仙子");
//		getDisPlayMetrics();
		mRollPagerView.setLayoutParams(WindowDisplayManager.getBannerHeight(getContext(), mRollPagerView));
		mRollPagerView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(int position) {
				if (!TextUtils.isEmpty(data.getBanner().get(position).getProductId())) {
					IntentUtils.IntentToGoodsDetial(getContext(), data.getBanner().get(position).getProductId());
				} else if (!TextUtils.isEmpty(data.getBanner().get(position).getBannerUrl()) && !data.getBanner().get(position).getBannerUrl().equals("null")) {
					Intent intent = new Intent(getContext(), NormalWebViewActivity.class);
					intent.putExtra("url", data.getBanner().get(position).getBannerUrl());
					intent.putExtra("title", "no");
					getContext().startActivity(intent);
				} else {
					return;
				}
			}
		});
		mRollPagerView.setHintView(new CustomHintView(getContext(), R.drawable.shape_round360_e83, R.drawable.shape_round360_e83_stroke0_5_trans,
				DensityUtil.dip2px(getContext(), getContext().getResources().getDimension(R.dimen.dis6))));
	}

	private int getDisPlayMetrics() {

		DisplayMetrics dm = new DisplayMetrics();
//获取屏幕信息
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels / 2;
		return screenWidth;
	}

	@Override
	public void setMineNewNum(boolean b, NormalEntity response) {
		if (b) {
			String text = response.getData().toString();
			fragMineTvInfo.setVisibility(View.VISIBLE);
			int i = (int) Float.parseFloat(text);
			if (i > 99) {
				fragMineTvInfo.setText("99");
			} else {
				fragMineTvInfo.setText(i + "");
			}

		} else {
			fragMineTvInfo.setVisibility(View.GONE);
		}
	}


	@Override
	public void onResume() {
		super.onResume();
		mineFragView.doAsyncGetInfo(mUtils);
	}


	@OnClick({R.id.frag_mine_info, R.id.frag_mine_account, R.id.frag_mine_order_all, R.id.frag_mine_md, R.id.frag_mine_address,
			R.id.frag_mine_collection, R.id.frag_mine_online_service, R.id.frag_mine_rl_setting, R.id.frag_mine_rl_about, R.id.rl_my_share})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.frag_mine_md://
				if (mUtils.isLogin()) {
					Intent intentAccount = new Intent(getActivity(), MinePersonalDataActivity.class);
					startActivity(intentAccount);
//					Intent intentFreeOrder = new Intent(getActivity(), MyFreeOrderActivity.class);
//					startActivity(intentFreeOrder);
				} else {
					IntentUtils.IntentToLogin(getActivity());
				}
				break;
			case R.id.frag_mine_info:
				if (mUtils.isLogin()) {
					IntentUtils.IntentToInfoCenter(getActivity());
				} else {
					IntentUtils.IntentToLogin(getActivity());
				}
				break;
			case R.id.frag_mine_account:
				if (mUtils.isLogin()) {
					Intent intentAccount = new Intent(getActivity(), MineAccount2Activity.class);
					startActivity(intentAccount);
				} else {
					IntentUtils.IntentToLogin(getActivity());
				}
				break;
			case R.id.frag_mine_order_all://我的订单
				if (mUtils.isLogin()) {
					Intent intentAll = new Intent(getActivity(), MineOrderActivity.class);
					intentAll.putExtra("page", 0);
					startActivity(intentAll);
				} else {
					IntentUtils.IntentToLogin(getActivity());
				}
				break;

			case R.id.frag_mine_address:
				if (mUtils.isLogin()) {
					Intent intentAddress = new Intent(getActivity(), MineAddressManageActivity.class);
					startActivity(intentAddress);
				} else {
					IntentUtils.IntentToLogin(getActivity());
				}
				break;
			case R.id.frag_mine_collection://我的收藏
				if (mUtils.isLogin()) {

					Intent intentSetting = new Intent(getActivity(), MineCollectionActivity.class);
					startActivity(intentSetting);
				} else {
					IntentUtils.IntentToLogin(getActivity());
				}
				break;
			case R.id.frag_mine_online_service://在线客服
//				if (mUtils.isLogin()) {
////					Intent intentCoupon = new Intent(getActivity(), MineCouponActivity.class);
////					startActivity(intentCoupon);
//
//					mineFragView.doCustomServices();
//				} else {
//					IntentUtils.IntentToLogin(getActivity());
//				}
				doAsyncGetNumber();
				break;
			case R.id.frag_mine_rl_setting:
				if (mUtils.isLogin()) {
					Intent intentSetting = new Intent(getActivity(), MineSettingActivity.class);
					startActivity(intentSetting);
				} else {
					IntentUtils.IntentToLogin(getActivity());
				}
				break;
			case R.id.frag_mine_rl_about:
				if (mUtils.isLogin()) {
					Intent intentAbout = new Intent(getActivity(), MineAboutActivity.class);
					startActivity(intentAbout);
				} else {
					IntentUtils.IntentToLogin(getActivity());
				}
				break;
			case R.id.rl_my_share:
				if (mUtils.isLogin()) {
					Intent intentQRShare = new Intent(getActivity(), QRCodeActivity.class);
					startActivity(intentQRShare);
				} else {
					IntentUtils.IntentToLogin(getActivity());
				}
				break;
		}
	}



	private void doAsyncGetNumber() {
		Map<String, String> map = new HashMap<>();
		OkHttpUtils.post().url(URLBuilder.URLBaseHeader + "/phone/homePage/seachTel")
				.addParams("data", URLBuilder.format(map))
				.tag(getActivity()).build().execute(new Utils.MyResultCallback<String>() {

			@Override
			public String parseNetworkResponse(Response response) throws Exception {
				String json = response.body().string().trim();
				return json;
			}

			@Override
			public void onResponse(String response) {
				try {
					JSONObject jsonObject = new JSONObject(response);
					serviceTel = jsonObject.getString("msg");
					setCallDialog();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(Call call, Exception e) {
				super.onError(call, e);
			}
		});
	}


	private void setCallDialog() {
		if (mDialog == null) {
			mDialog = new CustomNormalContentDialog(getActivity());
		}

		if (TextUtils.isEmpty(serviceTel)) {
			ToastUtils.showToast(getActivity(), "无法获取联系电话，请检查网络稍后再试");
			return;
		}
		if (!mDialog.isShowing()) {
			mDialog.show();
		}
		mDialog.getTvTitle().setText("客服热线");
		if (!TextUtils.isEmpty(serviceTel)) {
			mDialog.getTvContent().setText("拨打" + serviceTel + "热线，联系官方客服");
		} else if (!TextUtils.isEmpty(mUtils.getServiceTel())) {
			mDialog.getTvContent().setText("拨打" + mUtils.getServiceTel() + "热线，联系官方客服");
		}
		mDialog.getTvConfirm().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (new AuthorUtils(getContext()).checkPermissions(needPermissions)) {
					setActionCall(serviceTel);
				}
				dismissDialog();
			}
		});
		mDialog.getTvCancel().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dismissDialog();
			}
		});
	}

	public void dismissDialog() {
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
	}

	private void doAsyncGetData() {
		Map<String, String> map = new HashMap<>();
		OkHttpUtils.post().url(URLBuilder.URLBaseHeader + "/phone/homePage/searchBanner")
				.addParams("data", URLBuilder.format(map))
				.tag(getActivity()).build().execute(new Utils.MyResultCallback<HomeEntity>() {

			@Override
			public HomeEntity parseNetworkResponse(Response response) throws Exception {
				String json = response.body().string().trim();
				HomeEntity homeEntity = new Gson().fromJson(json, HomeEntity.class);
				if (homeEntity.getCode() != null && homeEntity.getCode().equals(homeEntity.HTTP_OK)) {
					FileUtils.save2Local(1, json);
				}
				return homeEntity;
			}

			@Override
			public void onResponse(HomeEntity response) {
				if (response != null && response.getCode().equals(response.HTTP_OK)) {
					//返回值为200 说明请求成功
					if (response.getData() != null) {
						data = response.getData();
						mRollPagerView.setHintView(new CustomHintView(getContext(),
								R.drawable.bga_banner_selector_point_solid,
								R.drawable.bga_banner_selector_point_solids,
								0));
						mRollPagerView.setHintPadding(
								0,
								0,
								getDisPlayMetrics() - DensityUtil.dip2px(getContext(), 18),
								(int) getContext().getResources().getDimension(R.dimen.dis20)
						);
						NormalBannerAdapter mBannerAdapter = new NormalBannerAdapter(mRollPagerView, data.getBanner(), getContext());
						mRollPagerView.setAdapter(mBannerAdapter);
					}
				}
			}

			@Override
			public void onError(Call call, Exception e) {
				super.onError(call, e);
			}
		});
	}


	private MyReceiver receiver;//广播

	private void regReceiver() {
		//注册广播获取新收到的信息和未读消息数
		if (receiver == null) {
			receiver = new MyReceiver();
		}
		IntentFilter filter = new IntentFilter();
		filter.addAction(ZhiChiConstant.sobot_unreadCountBrocast);
		getActivity().registerReceiver(receiver, filter);
	}

	//设置广播获取新收到的信息和未读消息数
	class MyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			int noReadNum = intent.getIntExtra("noReadCount", 0);
			String content = intent.getStringExtra("content");
			//未读消息数
			if (noReadNum != 0) {
//				tvCoustomNum.setVisibility(View.VISIBLE);
//				tvCoustomNum.setText(noReadNum + "");
			}
			//新消息内容
			com.sobot.chat.utils.LogUtils.i("新消息内容:" + content);
		}
	}


	private void setActionCall(String serviceTel) {
		//拨打电话
		if (serviceTel != null) {
			Intent callIntent = new Intent();
			callIntent.setAction(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse("tel:" + serviceTel));
			startActivity(callIntent);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode) {
			case PERMISSON_REQUESTCODE:
				if (!AuthorUtils.verifyPermissions(grantResults)) {
					isNeedCheck = false;
				} else {
					isNeedCheck = true;
				}
				if (isNeedCheck) {
					setActionCall(serviceTel);
				}
				break;
		}
	}


	@Override
	public void setSobotApi(Information userInfo) {
		//设置标题显示模式
		SobotApi.setChatTitleDisplayMode(getActivity().getApplicationContext(), SobotChatTitleDisplayMode.values()[0], "");
		//设置是否开启消息提醒
		SobotApi.setNotificationFlag(getActivity().getApplicationContext(), true, R.mipmap.logo, R.mipmap.logo);
		SobotApi.hideHistoryMsg(getActivity().getApplicationContext(), 0);
		SobotApi.setEvaluationCompletedExit(getActivity().getApplicationContext(), false);
		SobotApi.startSobotChat(getActivity(), userInfo);
	}

	@Override
	public void setDatas(MineEntity.MineData data) {
		preferencesUtil.setValue("userType", data.getUserType());
		mineFragView.setServiceTel(data.getServiceTel());
		if (!TextUtils.isEmpty(data.getUpintegral())) {
			upintegral = data.getUpintegral();
		}
		if (!TextUtils.isEmpty(data.getServiceTel())) {
			mUtils.saveServiceTel(data.getServiceTel());
		}
	}


	@Override
	public void showToast(String s) {
		ToastUtils.showToast(getActivity(), s);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mineFragView.dismissDialog(mDialog);
		getActivity().unregisterReceiver(receiver);
	}
}
