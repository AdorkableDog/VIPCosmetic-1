package com.yj.cosmetics.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.yj.cosmetics.base.LazyLoadFragment;
import com.yj.cosmetics.base.URLBuilder;
import com.yj.cosmetics.R;
import com.yj.cosmetics.model.AccountListEntity;
import com.yj.cosmetics.ui.activity.StoreFellInDetailActivity;
import com.yj.cosmetics.ui.adapter.MyAccListAdapter;
import com.yj.cosmetics.util.LogUtils;
import com.yj.cosmetics.util.Utils;
import com.yj.cosmetics.widget.ProgressLayout;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2019/6/17 0017.
 */

public class MyAccountListFrags extends LazyLoadFragment {
	private int flag, pageNum = 1;
	private MyAccListAdapter shopListAdapter;
	private List<AccountListEntity.DataBean.ListBean> data = null;
	private List<AccountListEntity.DataBean.ListBean> dataList;
	@BindView(R.id.xrecyclerView)
	XRecyclerView mRecyclerView;
	@BindView(R.id.progress_layout)
	ProgressLayout mProgressLayout;
	private AccountListEntity.DataBean datas;
	private String urls;

	// 2.1 定义用来与外部activity交互，获取到宿主activity
	private FragmentInteraction listerner;

	// 1 定义了所有activity必须实现的接口方法
	public interface FragmentInteraction {
		void process(String str, String s, int type);
	}

	// 当FRagmen被加载到activity的时候会被回调
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		if (activity instanceof FragmentInteraction) {
			listerner = (FragmentInteraction) activity; // 2.2 获取到宿主activity并赋值
		} else {
			throw new IllegalArgumentException("activity must implements FragmentInteraction");
		}
	}

	//把传递进来的activity对象释放掉
	@Override
	public void onDetach() {
		super.onDetach();
		listerner = null;
	}


	public static MyAccountListFrags instant(int flag) {
		MyAccountListFrags fragment = new MyAccountListFrags();
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
	protected void initView() {

	}


	private void refresh() {
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
					Intent intent = new Intent(getContext(), StoreFellInDetailActivity.class);
					intent.putExtra("orderDescId", datas.getList().get(postion - 1).getOrderDescId());
					intent.putExtra("shopId", datas.getList().get(postion - 1).getShopId());
					intent.putExtra("shopName", datas.getList().get(postion - 1).getShopName());
					getContext().startActivity(intent);
				}
			}

			@Override
			public void onVIPClick(View view, int postion) {

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
				LogUtils.e("json的值1==" + json);
				AccountListEntity shopListEntity = new Gson().fromJson(json, AccountListEntity.class);
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
//				String homejson1 = FileUtils.loadFromLocal(1);
				if (call.isCanceled()) {
					call.cancel();
				} else {
//					showNetError();
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
				LogUtils.e("json的值2==" + json);
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
//					mView.showNetError();
				}
			}

			@Override
			public void onError(Call call, Exception e) {
				super.onError(call, e);
				setRefreshComplete();
//				String homejson1 = FileUtils.loadFromLocal(1);
				if (call.isCanceled()) {
					call.cancel();
				} else {
//					mView.showNetError();
				}
			}
		});
	}


	public void showContent(List<AccountListEntity.DataBean.ListBean> data, int type) {
		this.data = data;
		if (datas.getmMoney() != null && datas.getMcash() != null) {
			listerner.process(datas.getmMoney(), datas.getMcash(), type);
		}
		if (dataList != null && dataList.size() != 0) {
			mProgressLayout.showContent();
			shopListAdapter.setData(data);
		}else {
			setNoneList();
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
		mRecyclerView.setNoMore(true);
		mRecyclerView.setPullRefreshEnabled(true);
		pageNum--;
	}
}
