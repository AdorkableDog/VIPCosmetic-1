package com.yj.cosmetics.ui.activity.LeaveMes;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.yj.cosmetics.R;
import com.yj.cosmetics.base.BaseActivity;
import com.yj.cosmetics.base.URLBuilder;
import com.yj.cosmetics.listener.JudgeCompressListener;
import com.yj.cosmetics.model.JudgeGoodsData;
import com.yj.cosmetics.model.NormalEntity;
import com.yj.cosmetics.util.AuthorUtils;
import com.yj.cosmetics.util.LogUtils;
import com.yj.cosmetics.util.OkhttpFileUtils;
import com.yj.cosmetics.util.PicUtils;
import com.yj.cosmetics.util.ToastUtils;
import com.yj.cosmetics.util.Utils;
import com.yj.cosmetics.util.luban.Luban;
import com.yj.cosmetics.widget.RoundedImageView.RoundedImageView;
import com.zhy.http.okhttp.request.RequestCall;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.valuesfeng.picker.Picker;
import io.valuesfeng.picker.engine.GlideEngine;
import io.valuesfeng.picker.utils.PicturePickerUtils;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2019/5/18 0018.
 * <p>
 * 。留言界面
 */

public class LeaveMesActivity extends BaseActivity {
	@BindView(R.id.title_rl_next)
	RelativeLayout rlPost;
	@BindView(R.id.title_tv_next)
	TextView tvPost;
	@BindView(R.id.judge_show_content)
	EditText judgeShowContent;
	@BindView(R.id.input_text_num)
	TextView inputTextNum;
	@BindView(R.id.text_img_num_)
	TextView inputTextNums;
	@BindView(R.id.textView3)
	TextView textView3;
	@BindView(R.id.judge_offer1_rl1)
	RelativeLayout rl1;
	@BindView(R.id.judge_offer2_rl2)
	RelativeLayout rl2;
	@BindView(R.id.judge_offer3_rl3)
	RelativeLayout rl3;
	@BindView(R.id.judge_show_offer1close)
	ImageView ivClose1;
	@BindView(R.id.judge_show_offer2close)
	ImageView ivClose2;
	@BindView(R.id.judge_show_offer3close)
	ImageView ivClose3;
	@BindView(R.id.judge_show_offer1)
	RoundedImageView ivOffer1;
	@BindView(R.id.judge_show_offer2)
	RoundedImageView ivOffer2;
	@BindView(R.id.judge_show_offer3)
	RoundedImageView ivOffer3;
	@BindView(R.id.settlement_cart_cb1)
	CheckBox cartCb1;
	@BindView(R.id.settlement_cart_cb2)
	CheckBox cartCb2;
	@BindView(R.id.settlement_cart_cb3)
	CheckBox cartCb3;
	String userPhone;
	private int i, count, JudgeType;
	private static final List<String> imgPath = new ArrayList<>();
	private boolean close = true;
	private static ArrayList<File> mImage = new ArrayList<>();

	/**
	 * 判断是否需要检测，防止不停的弹框
	 */
	private boolean isNeedCheck = true;
	private static final int PERMISSON_REQUESTCODE = 0;
	/**
	 * 需要进行检测的权限数组
	 */
	protected String[] needPermissions = {
			Manifest.permission.CAMERA,
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.READ_EXTERNAL_STORAGE,
	};
	private Animation animation;
	List<JudgeGoodsData> mScaledData;

	@Override
	protected int getContentView() {
		return R.layout.activity_leave_mes;
	}

	@Override
	protected void initView() {
		setTitleText("留言反馈");
		rlPost.setVisibility(View.VISIBLE);
		tvPost.setText("提交");
	}

	@Override
	protected void initData() {
		mScaledData = new ArrayList<>();
		animation = AnimationUtils.loadAnimation(LeaveMesActivity.this, R.anim.judge_img_scale);
		rlPost.setEnabled(true);
		judgeShowContent.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				LogUtils.i("我onTextChange了" + charSequence);
				inputTextNum.setText(String.valueOf(judgeShowContent.getText().length()));
			}

			@Override
			public void afterTextChanged(Editable editable) {
			}
		});
	}


	@OnClick({R.id.judge_show_offer1,
			R.id.judge_show_offer1close,
			R.id.judge_show_offer2,
			R.id.judge_show_offer2close,
			R.id.judge_show_offer3,
			R.id.judge_show_offer3close,
			R.id.title_rl_next,
			R.id.settlement_cart_cb1,
			R.id.settlement_cart_cb2,
			R.id.settlement_cart_cb3
	})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.judge_show_offer1close:
				if (close) {
					close = false;
					rl1.startAnimation(animation);
					i = 0;
					setAnimationListener();
				}
				break;
			case R.id.judge_show_offer2close:
				if (close) {
					close = false;
					rl2.startAnimation(animation);
					i = 1;
					setAnimationListener();
				}
				break;
			case R.id.judge_show_offer3close:
				if (close) {
					close = false;
					rl3.startAnimation(animation);
					i = 2;
					setAnimationListener();
				}
				break;
			case R.id.judge_show_offer1:
				//开启权限
				if (new AuthorUtils(LeaveMesActivity.this).checkPermissions(needPermissions)) {
					//权限校验成功之后，开启本地文件夹和摄像头
					if (imgPath.size() < 1) {
						doUpdateAvatar();
					}
				} else {
					ToastUtils.showToast(LeaveMesActivity.this, "请开启您的权限");
				}
				break;
			case R.id.judge_show_offer2:
				if (imgPath.size() < 2) {
					doUpdateAvatar();
				}
				break;
			case R.id.judge_show_offer3:
				if (imgPath.size() < 3) {
					doUpdateAvatar();
				}
				break;
			case R.id.title_rl_next:

				if (JudgeType == 0) {
					Toast.makeText(this, "请选择反馈类型", Toast.LENGTH_SHORT).show();
					break;
				}

				if (TextUtils.isEmpty(judgeShowContent.getText().toString())) {
					ToastUtils.showToast(LeaveMesActivity.this, "请输入反馈内容");
					return;
				}
				if (judgeShowContent.getText().toString().length() < 10) {
					ToastUtils.showToast(LeaveMesActivity.this, "问题内容不少于10个字");
					return;
				}
				doAsyncPostData(LeaveMesActivity.this, imgPath);
				break;
			case R.id.settlement_cart_cb1:
				JudgeType = 1;
				cartCb1.setChecked(true);
				cartCb2.setChecked(false);
				cartCb3.setChecked(false);
				break;
			case R.id.settlement_cart_cb2:
				JudgeType = 2;
				cartCb1.setChecked(false);
				cartCb2.setChecked(true);
				cartCb3.setChecked(false);
				break;
			case R.id.settlement_cart_cb3:
				JudgeType = 3;
				cartCb1.setChecked(false);
				cartCb2.setChecked(false);
				cartCb3.setChecked(true);
				break;
		}
	}


	private void setAnimationListener() {
		animation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				switch (i) {
					case 0:
						if (imgPath.size() == 3) {
							imgPath.remove(0);
							glideImg();
						} else {
							imgPath.remove(0);
							glideImg();
							if (imgPath.size() == 0) {
								ivOffer1.setImageResource(R.drawable.shape_corner_aa_stoke0_5_ed_radius2);
								ivClose1.setVisibility(View.GONE);
								rl2.setVisibility(View.GONE);
							}
						}
						break;
					case 1:
						imgPath.remove(1);
						glideImg();
						break;
					case 2:
						imgPath.remove(2);
						glideImg();
						break;
				}
				close = true;
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
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
					doUpdateAvatar();
				}
				break;
		}
	}


	private void doUpdateAvatar() {
		Picker.from(this).count(3 - imgPath.size()).enableCamera(true).setEngine(new GlideEngine()).forResult(100);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case 100:
				if (resultCode == Activity.RESULT_OK) {
					List<Uri> mSelected = PicturePickerUtils.obtainResult(data);
					switch (requestCode) {
						case 100:
							for (int i = 0; i < mSelected.size(); i++) {
								String path = PicUtils.getPicPath(this, mSelected.get(i));
								imgPath.add(path);
//								showImg(PicUtils.getPicPath(this, mSelected.get(i)));
							}
							glideImg();
							break;
					}
				}
				break;
		}
	}


	public void glideImg() {
		inputTextNums.setText(String.valueOf(imgPath.size()));
		for (int i = 0; i < imgPath.size(); i++) {
			switch (i) {
				case 0:
					Glide.with(LeaveMesActivity.this).load(imgPath.get(i)).into(ivOffer1);
					ivClose1.setVisibility(View.VISIBLE);
					if (imgPath.size() <= 1) {
						rl2.setVisibility(View.VISIBLE);
						ivOffer2.setImageResource(R.drawable.shape_corner_aa_stoke0_5_ed_radius2);
						rl3.setVisibility(View.GONE);
						ivClose2.setVisibility(View.GONE);
						ivClose3.setVisibility(View.GONE);
					}
					break;
				case 1:
					rl2.setVisibility(View.VISIBLE);
					ivClose2.setVisibility(View.VISIBLE);
					Glide.with(LeaveMesActivity.this).load(imgPath.get(1)).into(ivOffer2);
					rl3.setVisibility(View.VISIBLE);
					ivOffer3.setImageResource(R.drawable.shape_corner_aa_stoke0_5_ed_radius2);
					if (imgPath.size() <= 2) {
						ivClose3.setVisibility(View.GONE);
					}
					break;
				case 2:
					ivClose3.setVisibility(View.VISIBLE);
					Glide.with(LeaveMesActivity.this).load(imgPath.get(2)).into(ivOffer3);
					break;

			}
		}
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		imgPath.clear();
	}


	public void doAsyncReq() {
		doAsyncPost(mUtils.getUid(), judgeShowContent.getText().toString(), null);
	}


	public void doAsyncPostData(Context context, List<String> imgPath) {
		if (imgPath.size() == 0) {
			rlPost.setEnabled(false);
			doAsyncReq();
			return;
		}

		for (int j = 0; j < imgPath.size(); j++) {
			count++;
			showImg(context, imgPath.get(j));
		}
	}

	private void showImg(Context context, final String path) {
		JudgeGoodsData data = new JudgeGoodsData();
		data.setImg(new File(path));
		Luban.get(context)
				.load(new File(path))
				.putGear(Luban.THIRD_GEAR)
				.setJudgeCompressListener(new JudgeCompressListener() {
					@Override
					public void onStart() {
					}

					@Override
					public void onSuccess(File file) {
					}

					@Override
					public void onError(Throwable e) {
//						rlNext.setEnabled(true);
//						ToastUtils.showToast(OpinionActivity.this, "图片已损坏,请重新选取");
					}

					@Override
					public void onJudgeSuccess(JudgeGoodsData data) {
						JudgeSuccess(data, count);
					}
				}).launchJudge(data);
	}


	public void doAsyncPost(String userId, String judgeContent, ArrayList<File> mImage) {
		Map<String, ArrayList<File>> imgMap = new HashMap<>();
		Map<String, String> map = new HashMap<>();
		map.put("userId", userId);
		map.put("feedDirection", String.valueOf(JudgeType));
		map.put("feedContent", judgeContent);

		if (mImage != null) {
			for (int j = 0; j < mImage.size(); j++) {
				imgMap.put("1025", mImage);
			}
		}
		LogUtils.i("传输的值" + URLBuilder.format(map));
		RequestCall params = OkhttpFileUtils.post().url(URLBuilder.URLBaseHeader + URLBuilder.userFeedback)
				.addParams("data", URLBuilder.format(map))
				.addJudgeFiles("files", imgMap)
				.tag(this).build();

		params.execute(new Utils.MyResultCallback<NormalEntity>() {
			@Override
			public void onBefore(Request request) {
				super.onBefore(request);

			/*	if (mDialog == null) {
					mDialog = new CustomProgressDialog(MyProblemActivity.this);
					if (!isFinishing()) {
						mDialog.show();
					}
				} else {
					if (!isFinishing()) {
						mDialog.show();
					}
				}*/

			}

			@Override
			public NormalEntity parseNetworkResponse(Response response) throws Exception {
				String json = response.body().string().trim();
				return new Gson().fromJson(json, NormalEntity.class);
			}

			@Override
			public void onResponse(NormalEntity response) {
				if (response != null && response.getCode().equals(response.HTTP_OK)) {
					//返回值为200 说明请求成功
//					resultIntent.putExtra("refresh", "refresh");
					reqSuccess();
				} else {
				}
			}

			@Override
			public void onError(Call call, Exception e) {
				super.onError(call, e);
				rlPost.setEnabled(true);
				if (call.isCanceled()) {
					call.cancel();
				} else {
//					ToastUtils.showToast(MyProblemActivity.this, "网络故障,请稍后再试");
				}
			}
		});
	}

	public void JudgeSuccess(JudgeGoodsData data, int count) {
		mScaledData.add(data);
		if (mScaledData.size() == count) {
			Collections.sort(mScaledData);
			for (int i = 0; i < mScaledData.size(); i++) {
				mImage.add(mScaledData.get(i).getScaledImg());
			}
			count = 0;
			doAsyncPost(mUtils.getUid(), judgeShowContent.getText().toString(), mImage);
		}
	}

	public void reqSuccess() {
		ToastUtils.showToast(LeaveMesActivity.this, "您的意见建议已提交.");
		rlPost.setEnabled(true);
		rlPost.postDelayed(new Runnable() {
			@Override
			public void run() {
				finish();
			}
		}, 400);
	}
}
