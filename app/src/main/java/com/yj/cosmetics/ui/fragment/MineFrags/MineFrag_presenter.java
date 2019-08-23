package com.yj.cosmetics.ui.fragment.MineFrags;

import android.text.TextUtils;
import android.util.Log;

import com.yj.cosmetics.base.Constant;
import com.yj.cosmetics.base.Key;
import com.yj.cosmetics.base.URLBuilder;
import com.yj.cosmetics.model.MineEntity;
import com.yj.cosmetics.model.NormalEntity;
import com.yj.cosmetics.util.LogUtils;
import com.yj.cosmetics.util.UserUtils;
import com.yj.cosmetics.util.Utils;
import com.yj.cosmetics.widget.Dialog.CustomNormalContentDialog;
import com.google.gson.Gson;
import com.sobot.chat.api.model.Information;
import com.zhy.http.okhttp.OkHttpUtils;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/6/4 0004.
 */

public class MineFrag_presenter implements MineFrag_contract.Presenter {
	private MineFrag_contract.View mView = null;
	private UserUtils mUtils;
	private String serviceTel = null;
	private Information userInfo;

	public MineFrag_presenter(MineFrag_contract.View view) {
		this.mView = view;
	}

	@Override
	public void subscribe() {
		userInfo = new Information();
	}

	@Override
	public void unsubscribe() {
	}


	@Override
	public void doCustomServices() {
		//用户信息设置
		//设置用户自定义字段
		userInfo.setUseRobotVoice(false);//这个属性默认都是false。想使用需要付费。付费才可以设置为true。
		userInfo.setUid(mUtils.getUid());
		userInfo.setTel(mUtils.getTel());
		userInfo.setUname(mUtils.getUserName());

		if (mUtils.getAvatar() != null) {
			userInfo.setFace(URLBuilder.getUrl(mUtils.getAvatar()));//头像
		}
		SoftReference<String> appkeySR = new SoftReference<>(Constant.ZC_appkey);
		String appkey = appkeySR.get();
		if (!TextUtils.isEmpty(appkey)) {
			userInfo.setAppkey(appkey);
			mView.setSobotApi(userInfo);
		} else {
		}
	}

	@Override
	public void dismissDialog(CustomNormalContentDialog mDialog) {
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
	}

	@Override
	public void setServiceTel(String serviceTel) {
		this.serviceTel = serviceTel;
	}


	public void doAsyncGetData(UserUtils mUtils) {
		Map<String, String> map = new HashMap<>();
		map.put("userId", mUtils.getUid());
		LogUtils.i("zoneOrder 传输的值" + URLBuilder.format(map));
		OkHttpUtils.post().url(URLBuilder.URLBaseHeader + "/phone/user/zoneOrder").tag(this)
				.addParams(Key.data, URLBuilder.format(map))
				.build().execute(new Utils.MyResultCallback<MineEntity>() {

			@Override
			public void onError(Call call, Exception e) {
				super.onError(call, e);
				if (call.isCanceled()) {
					call.cancel();
				} else {
				}
			}

			@Override
			public MineEntity parseNetworkResponse(Response response) throws Exception {
				String json = response.body().string().trim();
				LogUtils.i("zoneOrder json的值" + json);
				return new Gson().fromJson(json, MineEntity.class);
			}

			@Override
			public void onResponse(MineEntity response) {
				if (response != null && response.HTTP_OK.equals(response.getCode())) {
					setData(response.getData());
				} else {
				}
			}
		});
	}


	public void setData(MineEntity.MineData data) {
		mView.setDatas(data);
	}

	public void doAsyncGetInfo(UserUtils mUtils) {
		this.mUtils = mUtils;
		Map<String, String> map = new HashMap<>();
		map.put("userId", mUtils.getUid());
		LogUtils.i("countMsg 传输的值" + URLBuilder.format(map));
		OkHttpUtils.post().url(URLBuilder.URLBaseHeader + "/phone/user/countMsg").tag(this)
				.addParams(Key.data, URLBuilder.format(map))
				.build().execute(new Utils.MyResultCallback<NormalEntity>() {

			@Override
			public void onError(Call call, Exception e) {
				super.onError(call, e);
				mView.setMineNewNum(false, null);
				if (call.isCanceled()) {
					call.cancel();
				} else {
				}
			}

			@Override
			public NormalEntity parseNetworkResponse(Response response) throws Exception {
				String json = response.body().string().trim();
				LogUtils.e("json的值" + json);
				return new Gson().fromJson(json, NormalEntity.class);
			}

			@Override
			public void onResponse(NormalEntity response) {
				if (response != null && response.HTTP_OK.equals(response.getCode())) {
					if (response.getData() != null && Float.parseFloat(response.getData().toString()) > 0) {
						mView.setMineNewNum(true, response);
					} else {
						mView.setMineNewNum(false, null);
					}
				} else {
					mView.setMineNewNum(false, null);
				}
			}
		});
	}
}
