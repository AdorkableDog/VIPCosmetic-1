package com.yj.cosmetics.ui.activity.mineAccount;

import com.yj.cosmetics.base.BasePresenter;
import com.yj.cosmetics.base.BaseView;
import com.yj.cosmetics.model.AccordMoneyEntity;
import com.yj.cosmetics.model.AccountEntity;
import com.yj.cosmetics.util.UserUtils;
import com.yj.cosmetics.widget.ProgressLayout;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.List;

/**
 * Created by Administrator on 2018/6/5 0005.
 */

public interface MineAccount_contract {

	interface View extends BaseView {

		void initDialog();

		void showToast(String o);

		void setDatas(AccordMoneyEntity.DataBean data);

		void dismissDialog();

		void notifyDataSetChanged();


		void pageNumReduce();
	}

	interface Presenter extends BasePresenter {


		void doAsyncGetData(UserUtils mUtils);

		void doRefreshData(int type ,int pageNum, ProgressLayout mProgressLayout, XRecyclerView mRecyclerView, List<AccountEntity.AccountData> mList);

		void doRequestData(int type ,int pageNum, ProgressLayout mProgressLayout, XRecyclerView mRecyclerView, List<AccountEntity.AccountData> mList);
	}
}
