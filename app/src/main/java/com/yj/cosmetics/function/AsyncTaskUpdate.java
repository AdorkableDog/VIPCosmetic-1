package com.yj.cosmetics.function;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.yj.cosmetics.base.URLBuilder;
import com.yj.cosmetics.model.UpdateEntity;
import com.yj.cosmetics.util.LogUtils;
import com.yj.cosmetics.util.Utils;
import com.yj.cosmetics.util.VersionUtils;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2019/8/9 0009.
 */

public class AsyncTaskUpdate {


	private void doAsyncTaskUpdate() {
		OkHttpUtils.post()
				.url(URLBuilder.URLBaseHeader + "/phone/user/appVersion")
				.tag(this)
				.build()
				.execute(new Utils.MyResultCallback<UpdateEntity>() {
					@Override
					public void onError(Call call, Exception e) {
						super.onError(call, e);
					}

					@Override
					public UpdateEntity parseNetworkResponse(Response response) throws Exception {
						String json = response.body().string();
						LogUtils.i("doAsyncTaskUpdate --- json的值" + json);
						return new Gson().fromJson(json, UpdateEntity.class);
					}

					@Override
					public void onResponse(UpdateEntity response) {

					}
				});
	}



/*	private static final int PERMISSON_REQUESTCODE = 0;
	*//**
	 * @since 2.5.0
	 *//*
	private void checkPermissions(Context context,String... permissions) {
		List<String> needRequestPermissonList = findDeniedPermissions(context,permissions);
		if (null != needRequestPermissonList && needRequestPermissonList.size() > 0) {
			ActivityCompat.requestPermissions((Activity) context, needRequestPermissonList.toArray(new String[needRequestPermissonList.size()]), PERMISSON_REQUESTCODE);
		}
		if (needRequestPermissonList.size() == PERMISSON_REQUESTCODE) {
			//版本有更新
			if (!TextUtils.isEmpty(newVersion)) {
				if (VersionUtils.compareVersions(oldVersion, newVersion) == 1) {
					*//**
					 * 如果后台返回的apk大小值为0，说明后台没有apk安装包。此时不去弹出更新提示框，直接跳转到首页
					 *//*
					if (Integer.parseInt(data.getAppLen()) > 0) {
						showUpdateDialog();
//						showDialog("为保证您的正常使用,请允许相应权限");
					} else {
						changePage();
					}
				} else {
					changePage();
				}
			} else {
				changePage();
			}
		}
//		else {
//			changePage();
//		}
	}*/

	/**
	 * 获取权限集中需要申请权限的列表
	 *
	 * @param permissions
	 * @return
	 * @since 2.5.0
	 */
	private List<String> findDeniedPermissions(Context context,String[] permissions) {
		List<String> needRequestPermissonList = new ArrayList<>();
		for (String perm : permissions) {
			if (ContextCompat.checkSelfPermission(context, perm) != PackageManager.PERMISSION_GRANTED || ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, perm)) {
				needRequestPermissonList.add(perm);
			}
		}
		return needRequestPermissonList;
	}




}
