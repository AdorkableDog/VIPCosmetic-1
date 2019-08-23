package com.yj.cosmetics.ui.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.yj.cosmetics.R;
import com.yj.cosmetics.base.LazyLoadFragment;
import com.yj.cosmetics.base.URLBuilder;
import com.yj.cosmetics.model.AccountListEntity;
import com.yj.cosmetics.model.ShareEntity;
import com.yj.cosmetics.ui.activity.HomeGoodsListActivity;
import com.yj.cosmetics.ui.activity.StoreFellInDetailActivity;
import com.yj.cosmetics.ui.activity.goodDetail.GoodsDetailActivity;
import com.yj.cosmetics.ui.activity.sotreList.StoreListActivity;
import com.yj.cosmetics.ui.adapter.MyAccListAdapter;
import com.yj.cosmetics.util.LogUtils;
import com.yj.cosmetics.util.ToastUtils;
import com.yj.cosmetics.util.Utils;
import com.yj.cosmetics.widget.Dialog.CustomProgressDialog;
import com.yj.cosmetics.widget.Dialog.QuickeOrderDialog;
import com.yj.cosmetics.widget.ProgressLayout;
import com.google.gson.Gson;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import cn.sharesdk.wechat.friends.Wechat;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2019/6/17 0017.
 */

public class MyAccountListFragment extends LazyLoadFragment {

	private int flag, pageNum = 1;
	private MyAccListAdapter shopListAdapter;
	private List<AccountListEntity.DataBean.ListBean> data = null;
	private List<AccountListEntity.DataBean.ListBean> dataList;
	@BindView(R.id.xrecyclerView)
	XRecyclerView mRecyclerView;
	@BindView(R.id.progress_layout)
	ProgressLayout mProgressLayout;
	private AccountListEntity.DataBean datas = null;
	private String urls;
	private MyFreeOrderFragment parentFragment;

	public static MyAccountListFragment instant(int flag) {
		MyAccountListFragment fragment = new MyAccountListFragment();
		fragment.flag = flag;
		return fragment;
	}

	@Override
	protected int setContentView() {
		return R.layout.layout_shop_list;
	}

	@Override
	protected void lazyLoad() {
		refresh();
	}

	@Override
	public void onResume() {
		super.onResume();
		mRecyclerView.refresh();
	}

	@Override
	protected void initView() {
		parentFragment = (MyFreeOrderFragment) getParentFragment();
	}

	public void refresh() {
		mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
			@Override
			public void onRefresh() {
				pageNum = 1;
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						doAsyncGetData(mUtils.getUid(), pageNum, flag);
					}
				}, 500);
			}

			@Override
			public void onLoadMore() {
				pageNum++;
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						mRecyclerView.setPullRefreshEnabled(false);
						loadMoreData(mUtils.getUid(), pageNum, flag);
					}
				}, 500);
			}
		});
		mRecyclerView.refresh();
	}


	@Override
	protected void initData() {
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
		linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		mRecyclerView.setLayoutManager(linearLayoutManager);
		mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
		mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallClipRotate);
		mRecyclerView.setLoadingMoreEnabled(true);
		shopListAdapter = new MyAccListAdapter(getContext(), data, flag);
		mRecyclerView.setAdapter(shopListAdapter);
		shopListAdapter.setOnItemClickListener(new MyAccListAdapter.onItemClickListener() {

			@Override
			public void onItemClick(View view, int postion) {
				if (flag == 0) {
					doAsyncGetShare(dataList.get(postion - 1).getOrderId(), dataList.get(postion - 1).getShopId());
				}
			}

			@Override
			public void onVIPClick(View view, int postion) {
				Intent intent = new Intent(getActivity(), HomeGoodsListActivity.class);
				intent.putExtra("orderId",dataList.get(postion - 1).getOrderId());
				intent.putExtra("isVip","1");
				startActivity(intent);
			}
		});
	}


	CustomProgressDialog mDialog;
	private ShareEntity.ShareData shareData;

	public void doAsyncGetShare(String orderId, String shopId) {
		Map<String, String> map = new HashMap<>();
		map.put("shopId", shopId);
		map.put("userId", mUtils.getUid());
		map.put("orderId", orderId);
		OkHttpUtils.post().url(URLBuilder.URLBaseHeader + "/phone/homePageTwo/share")
				.addParams("data", URLBuilder.format(map))
				.tag(this).build().execute(new Utils.MyResultCallback<ShareEntity>() {

			@Override
			public ShareEntity parseNetworkResponse(Response response) throws Exception {
				String json = response.body().string().trim();
				LogUtils.i("doAsyncGetShare json的值" + json);
				return new Gson().fromJson(json, ShareEntity.class);
			}

			@Override
			public void inProgress(float progress) {
				super.inProgress(progress);
				if (mDialog == null) {
					mDialog = new CustomProgressDialog(getActivity());
					if (!getActivity().isFinishing()) {
						mDialog.show();
					}
				} else {
					if (!getActivity().isFinishing()) {
						mDialog.show();
					}
				}
			}

			@Override
			public void onResponse(ShareEntity response) {
				disMissDialogs();
				if (response != null && response.getCode().equals(response.HTTP_OK)) {
					//返回值为200 说明请求成功
					if (response.getData() != null) {
						shareData = response.getData();
						QuickOrder();
					}
				} else {
					ToastUtils.showToast(getActivity(), "无法获取分享信息" + response.getMsg());
				}
			}

			@Override
			public void onError(Call call, Exception e) {
				super.onError(call, e);
				disMissDialogs();
				LogUtils.i("网络请求失败 " + e);
				if (call.isCanceled()) {
					call.cancel();
				} else {
					ToastUtils.showToast(getActivity(), "获取分享信息失败，请检查网络稍后再试");
				}
			}
		});
	}

	private void disMissDialogs() {
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
	}

	QuickeOrderDialog QuickeOrderDialog;

	//@TODO ---------
	private void QuickOrder() {
		if (QuickeOrderDialog == null) {
			QuickeOrderDialog = new QuickeOrderDialog(getActivity());
		}
		if (!QuickeOrderDialog.isShowing()) {
			QuickeOrderDialog.show();
		}


		QuickeOrderDialog.getTvContent().setText(URLBuilder.getUrl(shareData.getUrl()));
		QuickeOrderDialog.getTvCopyUrl().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
				ClipData mClipData = ClipData.newPlainText("content", QuickeOrderDialog.getTvContent().getText());
				cm.setPrimaryClip(mClipData);
				ToastUtils.showToast(getActivity(), "复制成功");
			}
		});

		QuickeOrderDialog.getTvFriend().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				showShare(Wechat.NAME);
			}
		});
		QuickeOrderDialog.getTvWeChat().setOnClickListener(new View.OnClickListener() {//微信好友
			@Override
			public void onClick(View v) {
//				showShare(WechatMoments.NAME);
			}
		});
	}

	/**
	 * @param userId
	 * @param pageNum
	 * @param flag
	 */
	public void doAsyncGetData(String userId, int pageNum, final int flag) {
		Map<String, String> map = new HashMap<>();
		map.put("userId", userId);
		map.put("pageNum", String.valueOf(pageNum));
		if (flag == 0) {
			urls = URLBuilder.billCash;
		} else {
			urls = URLBuilder.searchUserBill;
		}
		OkHttpUtils.post().url(URLBuilder.URLBaseHeader + urls)
				.addParams("data", URLBuilder.format(map))
				.tag(this).build().execute(new Utils.MyResultCallback<AccountListEntity>() {

			@Override
			public AccountListEntity parseNetworkResponse(Response response) throws Exception {
				String json = response.body().string().trim();
				AccountListEntity shopListEntity = new Gson().fromJson(json, AccountListEntity.class);
				LogUtils.i("doAsyncGetData json的值" + json);
				return shopListEntity;
			}

			@Override
			public void onResponse(AccountListEntity response) {
				if (response != null && response.getCode().equals(response.HTTP_OK)) {
					//返回值为200 说明请求成功
					if (response.getData() != null) {
						datas = response.getData();
						dataList = datas.getList();
						showContent(dataList, flag);
					} else {
						setNoneList();
					}
				} else {
					showNetError();
				}
				setRefreshComplete();
			}

			@Override
			public void onError(Call call, Exception e) {
				super.onError(call, e);
				showContent(null, flag);
				setRefreshComplete();
				if (call.isCanceled()) {
					call.cancel();
				} else {
				}
			}
		});
	}


	public void loadMoreData(String userId, int pageNum, final int flag) {
		Map<String, String> map = new HashMap<>();
		map.put("userId", userId);
		map.put("pageNum", String.valueOf(pageNum));
		if (flag == 0) {
			urls = URLBuilder.billCash;
		} else {
			urls = URLBuilder.searchUserBill;
		}
		OkHttpUtils.post().url(URLBuilder.URLBaseHeader + urls)
				.addParams("data", URLBuilder.format(map))
				.tag(this).build().execute(new Utils.MyResultCallback<AccountListEntity>() {

			@Override
			public AccountListEntity parseNetworkResponse(Response response) throws Exception {
				String json = response.body().string().trim();
				AccountListEntity shopListEntity = new Gson().fromJson(json, AccountListEntity.class);
				return shopListEntity;
			}

			@Override
			public void onResponse(AccountListEntity response) {
				if (response != null && response.getCode().equals(response.HTTP_OK)) {
					//返回值为200 说明请求成功
					if (response.getData() != null) {
						AccountListEntity.DataBean data = response.getData();
						if (data.getList() != null && data.getList().size() > 0) {
							dataList.addAll(data.getList());
							showContent(dataList, flag);
							setRefreshComplete();
						} else {
							setNoMore(true);
						}
					} else {
						setNoneList();
					}
				} else {
				}
			}

			@Override
			public void onError(Call call, Exception e) {
				super.onError(call, e);
				setRefreshComplete();
				if (call.isCanceled()) {
					call.cancel();
				} else {
				}
			}
		});
	}


	public void showContent(List<AccountListEntity.DataBean.ListBean> data, int type) {
		this.data = data;
		if (null != datas) {
			if (null != datas.getmMoney() && null != datas.getMcash()) {
				parentFragment.process(datas.getmMoney(), datas.getMcash(), type);
			}
			if (dataList != null && dataList.size() != 0) {
				mProgressLayout.showContent();
				shopListAdapter.setData(data);
			} else {
				setNoneList();
			}
		}
	}

	public void setRefreshComplete() {
		if (mRecyclerView != null) {
			mRecyclerView.setPullRefreshEnabled(true);
			mRecyclerView.refreshComplete();
		}
	}


	public void setNoneList() {
		mProgressLayout.showNone(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});//没有数据
	}


	public void showNetError() {
		mProgressLayout.showNetError(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				setLocInfo();
			}
		});//没有数据
	}

	public void setNoMore(boolean b) {
		mRecyclerView.refreshComplete();
		mRecyclerView.setNoMore(b);
		mRecyclerView.setPullRefreshEnabled(true);
		pageNum--;
	}
}
