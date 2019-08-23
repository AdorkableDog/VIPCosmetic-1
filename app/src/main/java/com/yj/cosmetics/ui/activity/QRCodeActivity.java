package com.yj.cosmetics.ui.activity;


import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.sobot.chat.core.HttpUtils;
import com.yj.cosmetics.R;
import com.yj.cosmetics.base.BaseActivity;
import com.yj.cosmetics.base.URLBuilder;
import com.yj.cosmetics.model.AccountListEntity;
import com.yj.cosmetics.model.QREntity;
import com.yj.cosmetics.ui.adapter.MineRefundListAdapter;
import com.yj.cosmetics.util.DensityUtil;
import com.yj.cosmetics.util.LogUtils;
import com.yj.cosmetics.util.ToastUtils;
import com.yj.cosmetics.util.Utils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2019/8/6 0006.
 */

public class QRCodeActivity extends BaseActivity {
	@BindView(R.id.title_tv)
	TextView tvTitle;

	@BindView(R.id.tv_qr_code_)
	TextView tvCode;

	@BindView(R.id.tv_app_downLoad)
	TextView tvAppDownLoad;
//	@BindView(R.id.view_center)
//	View viewCenter;

	@BindView(R.id.rl_rule_1)
	RelativeLayout rlRule1;

	@BindView(R.id.all_background)
	RelativeLayout bg;

	@BindView(R.id.tv_num0)
	TextView tvRuleNum0;

	@BindView(R.id.tv_num1)
	TextView tvRuleNum1;

	@BindView(R.id.tv_num2)
	TextView tvRuleNum2;

	@BindView(R.id.tv_num3)
	TextView tvRuleNum3;

	@BindView(R.id.tv_invitation_code)
	TextView tvInvitCode;

	@BindView(R.id.rl_center_content)
	RelativeLayout rlCenter;

	@BindView(R.id.iv_qr_code)
	ImageView ivQRCode;

	@BindView(R.id.iv_bg)
	ImageView ivBg;
	private QREntity myQREntity;
//	private RelativeLayout.LayoutParams params;
	//	tv_invitation_code

	@Override
	protected int getContentView() {
		return R.layout.activity_qr_code;
	}

	@Override
	protected void initView() {
		tvTitle.setText("我的二维码");
	}


	@Override
	protected void initData() {
//		myQREntity = (QREntity.DataBean) getIntent().getParcelableExtra("data");
		doAsyncGetData(mUtils.getUid());
//		params = (RelativeLayout.LayoutParams) bg.getLayoutParams();
//		params.addRule(RelativeLayout.BELOW,R.id.tl_qr_code_img);
//		rlCenter.setLayoutParams(params);

	}


	@OnClick({R.id.title_ll_back, R.id.tv_invitation_code})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.title_ll_back:
				finish();
				break;
			case R.id.tv_invitation_code:
				downloadFile(myQREntity.getData().getShareUrl());
				break;
		}
	}


	private void setData(QREntity myQREntity) {
		this.myQREntity = myQREntity;
		if (myQREntity.getData().getUserCode() != null && !myQREntity.getData().getUserCode().isEmpty()) {
			tvCode.setText(myQREntity.getData().getUserCode());
			tvAppDownLoad.setText("您的邀请码");
			tvInvitCode.setText("分享邀请码");
			rlRule1.setVisibility(View.GONE);
			tvRuleNum1.setText("1.");
			tvRuleNum2.setText("2.");
			tvRuleNum3.setText("3.");
			ivBg.setBackgroundDrawable(getResources().getDrawable(R.mipmap.bg_regist_gift));/*;getResources.(R.mipmap.img_bj));*/

//			params.setMargins(0, 0, 0, 0);
//			rlCenter.setLayoutParams(params);
		} else {
			tvAppDownLoad.setText("App下载二维码");
			tvInvitCode.setText("立即下载");
			rlRule1.setVisibility(View.VISIBLE);
			tvRuleNum0.setText("1.");
			tvRuleNum1.setText("2.");
			tvRuleNum2.setText("3.");
			tvRuleNum3.setText("4.");
			ivBg.setBackgroundDrawable(getResources().getDrawable(R.mipmap.bg_download_regist));
//			params.setMargins(0, 0, 0, 0);
//			rlCenter.setLayoutParams(params);
		}

		Glide.with(this)
				.load(URLBuilder.URLBaseHeader + URLBuilder.qrCode)
				.centerCrop()
				.into(ivQRCode);


	}


	private File DownQRImg() {
		/**
		 * 创建路径的时候一定要用[/],不能使用[\],但是创建文件夹加文件的时候可以使用[\].
		 * [/]符号是Linux系统路径分隔符,而[\]是windows系统路径分隔符 Android内核是Linux.
		 */
		String target = getApplicationContext().getExternalFilesDir("downImg").getPath();// 保存到app的包名路径下
		File destDir = new File(target);
		if (!destDir.exists()) {// 判断文件夹是否存在
			destDir.mkdirs();
		}
		return destDir;
	}


	public void downloadFile(String shareUrl) {
		final String ImgName = shareUrl.split("/")[shareUrl.split("/").length - 1];
		OkHttpUtils.get()
				.url(shareUrl)
				.build()
				.execute(new FileCallBack(DownQRImg().getAbsolutePath(), ImgName) {
					@Override
					public void inProgress(float v, long l) {
					}

					@Override
					public void onError(Call call, Exception e) {
						ToastUtils.show("分享图片下载失败.");
					}

					@Override
					public void onResponse(File file) {
						// 其次把文件插入到系统图库
						try {
							MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), ImgName, null);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
						sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(file.getPath()))));
						ToastUtils.show("分享图片已保存到手机.");
					}
				});
	}


	/**
	 * @param userId 获取 二维码相关的信息
	 */
	public void doAsyncGetData(String userId) {
		Map<String, String> map = new HashMap<>();
		map.put("userId", userId);
		OkHttpUtils.post().url(URLBuilder.URLBaseHeader + URLBuilder.userShare)
				.addParams("data", URLBuilder.format(map))
				.tag(this).build().execute(new Utils.MyResultCallback<QREntity>() {

			@Override
			public QREntity parseNetworkResponse(Response response) throws Exception {
				String json = response.body().string().trim();
				QREntity shopListEntity = new Gson().fromJson(json, QREntity.class);
				return shopListEntity;
			}

			@Override
			public void onResponse(QREntity response) {
				if (response != null && response.getCode().equals(response.HTTP_OK)) {
					//返回值为200 说明请求成功
					if (response.getData() != null) {
						setData(response);
					} else {
						ToastUtils.show("您还没有下单，下单之后才能分享哦.");
					}
				} else {
				}
			}

			@Override
			public void onError(Call call, Exception e) {
				super.onError(call, e);
				if (call.isCanceled()) {
					call.cancel();
				} else {

				}
			}
		});
	}


}
