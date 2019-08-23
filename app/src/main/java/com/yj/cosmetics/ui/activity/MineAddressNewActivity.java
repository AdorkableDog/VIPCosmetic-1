package com.yj.cosmetics.ui.activity;

import android.graphics.Color;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.OptionsPickerView;
import com.google.gson.Gson;
import com.yj.cosmetics.R;
import com.yj.cosmetics.base.BaseActivity;
import com.yj.cosmetics.base.Key;
import com.yj.cosmetics.base.URLBuilder;
import com.yj.cosmetics.model.JsonBean;
import com.yj.cosmetics.model.NormalEntity;
import com.yj.cosmetics.ui.MainActivity;
import com.yj.cosmetics.util.GetJsonDataUtil;
import com.yj.cosmetics.util.KeyBoardUtils;
import com.yj.cosmetics.util.LogUtils;
import com.yj.cosmetics.util.MatchUtils;
import com.yj.cosmetics.util.ToastUtils;
import com.yj.cosmetics.util.Utils;
import com.yj.cosmetics.widget.Dialog.CustomProgressDialog;
import com.yj.cosmetics.widget.Dialog.ValidateAddressDialogs;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Suo on 2017/4/28.
 */

public class MineAddressNewActivity extends BaseActivity { //implements TextWatcher, Inputtips.InputtipsListener, PoiSearch.OnPoiSearchListener {
	@BindView(R.id.address_new_address)
	TextView tvAddress;
	@BindView(R.id.address_new_cb)
	CheckBox cbCheck;
	/* @BindView(R.id.address_news_save)
	 Button btnSave;*/
	@BindView(R.id.address_new_etName)
	EditText etName;
	@BindView(R.id.address_new_tel)
	EditText etTel;
	@BindView(R.id.address_new_etDetial)
	AutoCompleteTextView etDetial;
	@BindView(R.id.title_rl_next)
	RelativeLayout rlNext;
	@BindView(R.id.title_tv_next)
	TextView tvNext;
	private String addressId;
	//	private int currentPage = 0;// 当前页面，从0开始计数
//	private PoiSearch.Query query;// Poi查询条件类
//	private PoiSearch poiSearch;// POI搜索
//	private PoiResult poiResult; // poi返回的结果
	private ArrayList<JsonBean> options1Items = new ArrayList<>();
	private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
	private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
	private Thread thread;
	private boolean isLoaded = false;
	private CustomProgressDialog loadingDialog;

	@Override
	protected int getContentView() {
		return R.layout.activity_mine_address_new;
	}

	@Override
	protected void initView() {
//        setEnable();
		loadingDialog = new CustomProgressDialog(this);
		rlNext.setVisibility(View.VISIBLE);
		tvNext.setTextColor(getResources().getColor(R.color.white));
		tvNext.setTextSize(13);
		tvNext.setText("保存");
		if (getIntent().getStringExtra("tag").equals("new")) {
			setTitleText("新建收货地址");
		} else {
			setTitleText("修改收货地址");
			addressId = getIntent().getStringExtra("addressId");
			etName.setText(getIntent().getStringExtra("name"));
			etTel.setText(getIntent().getStringExtra("tel"));
			tvAddress.setText(getIntent().getStringExtra("area"));
			etDetial.setText(getIntent().getStringExtra("detial"));
			if (getIntent().getStringExtra("default").equals("1")) {
				cbCheck.setChecked(true);
			} else {
				cbCheck.setChecked(false);
			}
		}
//		etDetial.addTextChangedListener(this);
		//给editText设置filter
		etName.setFilters(new InputFilter[]{inputFilter, new InputFilter.LengthFilter(20)});
		etDetial.setFilters(new InputFilter[]{inputFilter, new InputFilter.LengthFilter(70)});
	}

	@Override
	protected void initData() {
//		doSearchQuery();
		if (thread == null) {//如果已创建就不再重新创建子线程了
			thread = new Thread(new Runnable() {
				@Override
				public void run() {
					// 写子线程中的操作,解析省市区数据
					initJsonData();
				}
			});
			thread.start();
		}
	}

	@OnClick({R.id.address_new_area, R.id.address_new_checkrl/*,R.id.address_news_save*/, R.id.title_rl_next})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.address_new_area:
				KeyBoardUtils.hintKb(this);
				if (isLoaded) {
					ShowPickerView();
				}
				break;
			case R.id.address_new_checkrl:
				cbCheck.setChecked(!cbCheck.isChecked());
				break;
			case R.id.title_rl_next:
				if (TextUtils.isEmpty(etName.getText().toString().trim())) {
					ToastUtils.showToast(MineAddressNewActivity.this, "请输入收货人");
					return;
				}
				if (TextUtils.isEmpty(etTel.getText().toString().trim())) {
					ToastUtils.showToast(MineAddressNewActivity.this, "请输入收货电话");
					return;
				}
				if (TextUtils.isEmpty(tvAddress.getText().toString().trim())) {
					ToastUtils.showToast(MineAddressNewActivity.this, "请选择收货地区");
					return;
				}
				if (TextUtils.isEmpty(etDetial.getText().toString().trim())) {
					ToastUtils.showToast(MineAddressNewActivity.this, "请输入收货详细地址");
					return;
				}
				if (!MatchUtils.isValidPhoneNumber(etTel.getText().toString().trim())) {
					ToastUtils.showToast(this, "请输入正确的手机号码");
				} else {
					//手机号格式正确
					doAsyncEditAddress();
				}
				break;
		}
	}

	ValidateAddressDialogs deleteDialog;

	private void doAsyncEditAddress() {
		Map<String, String> map = new HashMap<>();
		if (!TextUtils.isEmpty(addressId)) {
			map.put("addressId", addressId);
		}
		map.put("usersId", mUtils.getUid());
		map.put("receiverName", etName.getText().toString().trim());
		map.put("receiverTel", etTel.getText().toString().trim());
		map.put("addressArea", tvAddress.getText().toString());
		map.put("addressDetail", etDetial.getText().toString());
		/*if(!TextUtils.isEmpty("postCode")){
		    map.put("postCode",etPost.getText().toString().trim());
        }*/
		if (cbCheck.isChecked()) {
			map.put("addressDefault", "1");
		} else {
			map.put("addressDefault", "2");
		}
		OkHttpUtils.post().url(URLBuilder.URLBaseHeader + "/phone/user/addUpdateUserAddress").tag(this)
				.addParams(Key.data, URLBuilder.format(map))
				.build().execute(new Utils.MyResultCallback<NormalEntity>() {
			@Override
			public void inProgress(float progress) {
				super.inProgress(progress);
				if (loadingDialog == null) {
					loadingDialog = new CustomProgressDialog(MineAddressNewActivity.this);
					if (!isFinishing()) {
						loadingDialog.show();
					}
				} else {
					if (!isFinishing()) {
						loadingDialog.show();
					}
				}
			}

			@Override
			public void onError(Call call, Exception e) {
				LogUtils.i("网络故障" + e.getMessage());
				if (call.isCanceled()) {
					call.cancel();
				} else {
					ToastUtils.showToast(MineAddressNewActivity.this, "网络故障,请稍后再试");
				}
				dismissDialog2();

				super.onError(call, e);
			}

			@Override
			public NormalEntity parseNetworkResponse(Response response) throws Exception {
				String json = response.body().string().trim();
				LogUtils.i("addUpdateUserAddress json的值" + json);
				return new Gson().fromJson(json, NormalEntity.class);
			}

			@Override
			public void onResponse(NormalEntity response) {
				if (isFinishing()) {
					return;
				}
				dismissDialog2();
				if (response.getCode().equals(response.HTTP_OK)) {
					ToastUtils.showToast(MineAddressNewActivity.this, "地址保存成功");
					rlNext.postDelayed(new Runnable() {
						@Override
						public void run() {
							finish();
						}
					}, 400);
				} else {
					ToastUtils.showToast(MineAddressNewActivity.this, response.getCode() + " :) " + response.getMsg());
				}
			}
		});
	}

	InputFilter inputFilter = new InputFilter() {
		Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
				Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

		@Override
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
			Matcher emojiMatcher = emoji.matcher(source);
			if (emojiMatcher.find()) {
				ToastUtils.showLong(MineAddressNewActivity.this, "不支持输入表情");
				return "";
			}
			return null;
		}
	};

	private void initJsonData() {//解析数据
		/**
		 * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
		 * 关键逻辑在于循环体
		 *
		 * */
		String JsonData = new GetJsonDataUtil().getJson(this, "province.json");//获取assets目录下的json文件数据
		ArrayList<JsonBean> jsonBean = parseData(JsonData);//用Gson 转成实体
		/**
		 * 添加省份数据
		 *
		 * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
		 * PickerView会通过getPickerViewText方法获取字符串显示出来。
		 */
		options1Items = jsonBean;
		for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
			ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
			ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）
			for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {//遍历该省份的所有城市
				String CityName = jsonBean.get(i).getCityList().get(c).getName();
				CityList.add(CityName);//添加城市
				ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表
				//如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
				if (jsonBean.get(i).getCityList().get(c).getArea() == null
						|| jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
					City_AreaList.add("");
				} else {
					for (int d = 0; d < jsonBean.get(i).getCityList().get(c).getArea().size(); d++) {//该城市对应地区所有数据
						String AreaName = jsonBean.get(i).getCityList().get(c).getArea().get(d);
						City_AreaList.add(AreaName);//添加该城市所有地区数据
					}
				}
				Province_AreaList.add(City_AreaList);//添加该省所有地区数据
			}
			/**
			 * 添加城市数据
			 */
			options2Items.add(CityList);
			/**
			 * 添加地区数据
			 */
			options3Items.add(Province_AreaList);
		}
		isLoaded = true;
	}

	public ArrayList<JsonBean> parseData(String result) {//Gson 解析
		ArrayList<JsonBean> detail = new ArrayList<>();
		try {
			JSONArray data = new JSONArray(result);
			Gson gson = new Gson();
			for (int i = 0; i < data.length(); i++) {
				JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
				detail.add(entity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return detail;
	}

	private void ShowPickerView() {// 弹出选择器
		OptionsPickerView pvOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {

			@Override
			public void onOptionsSelect(int options1, int options2, int options3, View v) {
				String tx = options1Items.get(options1).getPickerViewText() + " " +
						options2Items.get(options1).get(options2) + " " +
						options3Items.get(options1).get(options2).get(options3);
				tvAddress.setText(tx);
			}
		})
				.setTitleText("城市选择")
				.setDividerColor(Color.BLACK)
				.setTextColorCenter(Color.BLACK) //设置选中项文字颜色
				.setContentTextSize(20)
				.build();
        /*pvOptions.setPicker(options1Items);//一级选择器
        pvOptions.setPicker(options1Items, options2Items);//二级选择器*/
		pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
		pvOptions.show();
	}

	private void dismissDialog() {
		if (deleteDialog != null) {
			deleteDialog.dismiss();
			deleteDialog = null;
		}
	}

	private void dismissDialog2() {
		if (loadingDialog != null) {
			loadingDialog.dismiss();
			loadingDialog = null;
		}
	}

	@Override
	protected void onDestroy() {
		dismissDialog2();
		dismissDialog();
		OkHttpUtils.getInstance().cancelTag(this);
		super.onDestroy();
	}
}
