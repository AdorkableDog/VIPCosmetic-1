package com.yj.cosmetics.ui.fragment;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.yj.cosmetics.R;
import com.yj.cosmetics.base.BaseFragment;
import com.yj.cosmetics.base.URLBuilder;
import com.yj.cosmetics.model.ClassifyEntity;
import com.yj.cosmetics.model.ClassifyProductEntity;
import com.yj.cosmetics.ui.activity.SearchActivity;
import com.yj.cosmetics.ui.adapter.ClassifyContentAdapter;
import com.yj.cosmetics.ui.adapter.ClassifyTitleAdapter;
import com.yj.cosmetics.util.LogUtils;
import com.yj.cosmetics.util.ToastUtils;
import com.yj.cosmetics.util.Utils;
import com.yj.cosmetics.widget.ProgressLayout;
import com.google.gson.Gson;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;


/**
 * Created by Suo on 2017/11/18.
 *
 * @TODO 分类界面
 */

public class ClassifyFrag extends BaseFragment {
	@BindView(R.id.recyclerView)
	RecyclerView titleRecyclerView;
	@BindView(R.id.xrecyclerView)
	XRecyclerView mRecyclerView;
	@BindView(R.id.progress_layout)
	ProgressLayout mProgressLayout;
	@BindView(R.id.frag_classify_rl_top)
	RelativeLayout rlTop;
	@BindView(R.id.frag_classify_head)
	View vHead;
	ClassifyTitleAdapter mTitleAdapter;
	ClassifyContentAdapter mContentAdapter;
	private GridLayoutManager gridLayoutManager;
	private List<ClassifyEntity.ClassifyItem> mTitle;
	List<ClassifyProductEntity.DataBean> data;
	String classifyId;
	private int pageNum = 1;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = createView(inflater.inflate(R.layout.fragment_classify, container, false));
		return view;
	}

	@Override
	protected void initData() {
		mTitle = new ArrayList<>();
		LinearLayoutManager titleLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
		titleRecyclerView.setLayoutManager(titleLayoutManager);
		mTitleAdapter = new ClassifyTitleAdapter(getActivity(), mTitle);
		titleRecyclerView.setAdapter(mTitleAdapter);
		gridLayoutManager = new GridLayoutManager(getActivity(), 2);
		mRecyclerView.setLayoutManager(gridLayoutManager);
		mContentAdapter = new ClassifyContentAdapter(getActivity(), data);
		mRecyclerView.setAdapter(mContentAdapter);
		mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
		mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallClipRotate);
		mTitleAdapter.setOnItemClickListener(new ClassifyTitleAdapter.SpendDetialClickListener() {
			@Override
			public void onItemClick(View view, int postion) {
				if (mTitleAdapter.mPosition == postion) {
					return;
				}
				classifyId = mTitle.get(postion).getClassify_id();
				mTitleAdapter.mPosition = postion;
				doAsyncGetContent();
				mContentAdapter.clearData();
				mTitleAdapter.notifyDataSetChanged();
				int windowHeight = getActivity().getWindowManager().getDefaultDisplay().getHeight();
				titleRecyclerView.smoothScrollBy(0, (int) (view.getY() + view.getHeight() + rlTop.getHeight() - windowHeight / 2));
			}
		});
		mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
			@Override
			public void onRefresh() {
				pageNum = 1;
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						doAsyncGetContent();
					}
				}, 500);
			}

			@Override
			public void onLoadMore() {
				pageNum++;
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						loadMoreData();
						mRecyclerView.setPullRefreshEnabled(false);
					}
				}, 500);
			}
		});
		doAsyncGetTitle();
		transTitle();
	}

	@TargetApi(21)
	private void transTitle() {
		if (Build.VERSION.SDK_INT >= 21) {
			vHead.setVisibility(View.VISIBLE);
		}
	}

	@OnClick({R.id.frag_classify_rl_search})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.frag_classify_rl_search:
				Intent intentSearch = new Intent(getActivity(), SearchActivity.class);
				startActivity(intentSearch);
				break;
		}
	}


	private void doAsyncGetTitle() {
		mProgressLayout.showContent();
		OkHttpUtils.post().url(URLBuilder.URLBaseHeader + "/phone/homePage/searchClassIfy")
				.tag(getActivity()).build().execute(new Utils.MyResultCallback<ClassifyEntity>() {
			@Override
			public ClassifyEntity parseNetworkResponse(Response response) throws Exception {
				String json = response.body().string().trim();
				return new Gson().fromJson(json, ClassifyEntity.class);
			}

			@Override
			public void onResponse(ClassifyEntity response) {
				if (response != null && response.getCode().equals(response.HTTP_OK)) {
					//返回值为200 说明请求成功
					if (response.getData() != null && response.getData() != null && response.getData().size() != 0) {
						mTitle.addAll(response.getData());
						mTitleAdapter.notifyDataSetChanged();
						classifyId = response.getData().get(0).getClassify_id();
						doAsyncGetContent();
					} else {
						mProgressLayout.showNone(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
							}
						});
					}
				} else {
					LogUtils.i("我挂了" + response.getMsg());
					mProgressLayout.showNetError(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							doAsyncGetTitle();
						}
					});
				}
			}

			@Override
			public void onError(Call call, Exception e) {
				super.onError(call, e);
				LogUtils.i("我故障了" + e);
				if (call.isCanceled()) {
					call.cancel();
				} else {

					mProgressLayout.showNetError(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							doAsyncGetTitle();
						}
					});
				}
			}
		});
	}

	private void doAsyncGetContent() {
		mProgressLayout.showContent();
		Map<String, String> map = new HashMap<>();
		map.put("classifyId", classifyId);
		map.put("pageNum", pageNum + "");
		OkHttpUtils.post().url(URLBuilder.URLBaseHeader + "/phone/homePage/searchProductList")
				.addParams("data", URLBuilder.format(map))
				.tag(getActivity()).build().execute(new Utils.MyResultCallback<ClassifyProductEntity>() {
			@Override
			public ClassifyProductEntity parseNetworkResponse(Response response) throws Exception {
				String json = response.body().string().trim();
				LogUtils.e("json的值" + json);
				return new Gson().fromJson(json, ClassifyProductEntity.class);
			}

			@Override
			public void onResponse(ClassifyProductEntity response) {
				if (response != null) {
					//返回值为200 说明请求成功
					if (response.getData() != null && response.getData().size() > 0) {
						data = response.getData();
						mContentAdapter.setData(data);
						mProgressLayout.showContent();
					} else {
						mProgressLayout.showNone(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
							}
						});
					}
				} else {
					LogUtils.i("我挂了" + response.getMsg());
					mProgressLayout.showNetError(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							doAsyncGetContent();
						}
					});
				}
				setRefreshComplete();
			}

			@Override
			public void onError(Call call, Exception e) {
				super.onError(call, e);
				LogUtils.i("我故障了" + e);
				if (call.isCanceled()) {
					call.cancel();
				} else {
					mProgressLayout.showNetError(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							doAsyncGetContent();
						}
					});
				}
			}
		});
	}

	private void loadMoreData() {
		mProgressLayout.showContent();
		Map<String, String> map = new HashMap<>();
//		map.put("classifyId", classifyId);
		map.put("pageNum", pageNum + "");
		OkHttpUtils.post().url(URLBuilder.URLBaseHeader + "/phone/homePage/searchProductList")
				.addParams("data", URLBuilder.format(map))
				.tag(this).build().execute(new Utils.MyResultCallback<ClassifyProductEntity>() {

			@Override
			public ClassifyProductEntity parseNetworkResponse(Response response) throws Exception {
				String json = response.body().string().trim();
				LogUtils.i("userOrders json的值" + json);
				return new Gson().fromJson(json, ClassifyProductEntity.class);
			}

			@Override
			public void onResponse(ClassifyProductEntity response) {
				if (response != null) {
					if (response.getData() != null) {
						if (response.getData().size() != 0) {
							data.addAll(response.getData());
							mContentAdapter.notifyDataSetChanged();
							mRecyclerView.loadMoreComplete();
						} else if (response.getData().size() == 0) {
							mRecyclerView.setNoMore(true);
							pageNum--;
						}
					}
					mProgressLayout.showContent();
				} else {
					ToastUtils.showToast(getActivity(), "异常 :)" + response.getMsg());
					pageNum--;
					mRecyclerView.loadMoreComplete();
				}
				setRefreshComplete();
			}

			@Override
			public void onError(Call call, Exception e) {
				super.onError(call, e);
				mRecyclerView.loadMoreComplete();
				mRecyclerView.setPullRefreshEnabled(true);
				if (call.isCanceled()) {
					LogUtils.i("我进入到加载更多cancel了");
					call.cancel();
				} else if (pageNum != 1) {
					LogUtils.i("加载更多的Log");
					ToastUtils.showToast(getActivity(), "网络故障,请稍后再试");
					pageNum--;
				}
			}
		});
	}

	public void setRefreshComplete() {
		if (mRecyclerView != null) {
			mRecyclerView.setPullRefreshEnabled(true);
			mRecyclerView.refreshComplete();
		}
	}

}
