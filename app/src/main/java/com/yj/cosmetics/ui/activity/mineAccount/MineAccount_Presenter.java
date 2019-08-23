package com.yj.cosmetics.ui.activity.mineAccount;

import android.util.Log;
import android.view.View;

import com.yj.cosmetics.base.Key;
import com.yj.cosmetics.base.URLBuilder;
import com.yj.cosmetics.model.AccordMoneyEntity;
import com.yj.cosmetics.model.AccountEntity;
import com.yj.cosmetics.util.LogUtils;
import com.yj.cosmetics.util.UserUtils;
import com.yj.cosmetics.util.Utils;
import com.yj.cosmetics.widget.ProgressLayout;
import com.google.gson.Gson;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/6/5 0005.
 */

public class MineAccount_Presenter implements MineAccount_contract.Presenter {

	private MineAccount_contract.View mView = null;
	private UserUtils mUtils;

	public MineAccount_Presenter(MineAccount_contract.View view) {
		this.mView = view;
	}

	@Override
	public void subscribe() {

	}

	@Override
	public void unsubscribe() {

	}

	@Override
	public void doAsyncGetData(UserUtils mUtils) {
		this.mUtils = mUtils;
		Map<String, String> map = new HashMap<>();
		map.put("userId", mUtils.getUid());
		OkHttpUtils.post().url(URLBuilder.URLBaseHeader + "/phone/user/userMoney").tag(this)
				.addParams(Key.data, URLBuilder.format(map))
				.build().execute(new Utils.MyResultCallback<AccordMoneyEntity>() {
			@Override
			public void onBefore(Request request) {
				super.onBefore(request);
				mView.initDialog();
			}

			@Override
			public void onError(Call call, Exception e) {
				super.onError(call, e);
				if (call.isCanceled()) {
					call.cancel();
				} else {
					mView.showToast(null);
				}
				mView.dismissDialog();

			}

			@Override
			public AccordMoneyEntity parseNetworkResponse(Response response) throws Exception {
				String json = response.body().string().trim();
				return new Gson().fromJson(json, AccordMoneyEntity.class);
			}

			@Override
			public void onResponse(AccordMoneyEntity response) {
				if (response != null) {
					mView.setDatas(response.getData());
				} else {
					mView.showToast(response.getMsg());
				}
				mView.dismissDialog();
			}
		});
	}

	@Override
	public void doRefreshData(int type, int pageNum, final ProgressLayout mProgressLayout,
	                          final XRecyclerView mRecyclerView,
	                          final List<AccountEntity.AccountData> mList) {
		mProgressLayout.showContent();
		Map<String, String> map = new HashMap<>();
		map.put("userId", mUtils.getUid());
		map.put("pageNum", pageNum + "");
		String url = "";
		if (type==0){
			url = URLBuilder.URLBaseHeader + "/phone/user/userMoneyBack";
		}else if (type==1){
			url = URLBuilder.URLBaseHeader + "/phone/user/userCashList";
		}
		OkHttpUtils.post().url(url).tag(this)
				.addParams(Key.data, URLBuilder.format(map))
				.build().execute(new Utils.MyResultCallback<AccountEntity>() {

			@Override
			public void onError(Call call, Exception e) {
				super.onError(call, e);
				mRecyclerView.refreshComplete();
				if (call.isCanceled()) {
					call.cancel();
				} else {
					mProgressLayout.showNetError(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							if (mList != null && !mList.isEmpty()) {
								mList.clear();
								mView.notifyDataSetChanged();

							}
							mRecyclerView.refresh();
						}
					});
				}
			}

			@Override
			public AccountEntity parseNetworkResponse(Response response) throws Exception {
				String json = response.body().string().trim();
				return new Gson().fromJson(json, AccountEntity.class);
			}

			@Override
			public void onResponse(AccountEntity response) {

				if (response != null && response.HTTP_OK.equals(response.getCode())) {
					if (response.getData().size() != 0) {
						mList.clear();
						mList.addAll(response.getData());
						mView.notifyDataSetChanged();
						mProgressLayout.showContent();
					} else if (response.getData().size() == 0) {
						mProgressLayout.showNone(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
							}
						});
					}
				} else {
//                    ToastUtils.showToast(MineAccountProfitActivity.this,"请求失败：）"+response.getMsg());
					mProgressLayout.showNetError(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							if (mList != null && !mList.isEmpty()) {
								mList.clear();
								mView.notifyDataSetChanged();
							}
							mRecyclerView.refresh();
						}
					});
				}
				mRecyclerView.refreshComplete();
			}
		});
	}

	@Override
	public void doRequestData(int type, final int pageNum, final ProgressLayout mProgressLayout,
	                          final XRecyclerView mRecyclerView,
	                          final List<AccountEntity.AccountData> mList) {
		Map<String, String> map = new HashMap<>();
		map.put("userId", mUtils.getUid());
		map.put("pageNum", pageNum + "");
		String url = "";
		if (type==0){
			url = URLBuilder.URLBaseHeader + "/phone/user/userMoneyBack";
		}else if (type==1){
			url = URLBuilder.URLBaseHeader + "/phone/user/userCashList";
		}
		OkHttpUtils.post().url(url).tag(this)
				.addParams("data", URLBuilder.format(map))
				.build().execute(new Utils.MyResultCallback<AccountEntity>() {
			@Override
			public AccountEntity parseNetworkResponse(Response response) throws Exception {
				String json = response.body().string().trim();
				return new Gson().fromJson(json, AccountEntity.class);
			}

			@Override
			public void onResponse(AccountEntity info) {
				if (info != null && info.HTTP_OK.equals(info.getCode())) {
					if (info.getData().size() != 0) {
						mList.addAll(info.getData());
						mView.notifyDataSetChanged();
						mRecyclerView.setPullRefreshEnabled(true);
						mRecyclerView.loadMoreComplete();
					} else if (info.getData().size() == 0) {
						mRecyclerView.setNoMore(true);
						mRecyclerView.setPullRefreshEnabled(true);
						mView.pageNumReduce();

					}
					mProgressLayout.showContent();
				} else {
					mView.showToast("网络异常");
					mView.pageNumReduce();
					mRecyclerView.setPullRefreshEnabled(true);
					mRecyclerView.loadMoreComplete();
				}
			}

			@Override
			public void onError(Call call, Exception e) {
				super.onError(call, e);
				mRecyclerView.loadMoreComplete();
				mRecyclerView.setPullRefreshEnabled(true);
				if (call.isCanceled()) {
					LogUtils.i("我进入到加载更多cancel了");
					call.cancel();
				} else {
					if (pageNum != 1) {
						mView.showToast("网络故障,请稍后再试");
						mView.pageNumReduce();
					}
				}
			}
		});
	}
}
