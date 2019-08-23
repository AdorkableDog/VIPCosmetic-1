package com.yj.cosmetics.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yj.cosmetics.R;
import com.yj.cosmetics.base.URLBuilder;
import com.yj.cosmetics.model.AccountEntity;
import com.yj.cosmetics.widget.MaterialRippleView;
import com.yj.cosmetics.widget.RoundedImageView.RoundedImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Suo on 2017/4/17.
 */

public class AccountProfitAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private Context mContext;
	List<AccountEntity.AccountData> mList;
	ProfitDetialClickListener mItemClickListener;

	public AccountProfitAdapter(Context mContext, List<AccountEntity.AccountData> mList) {
		this.mContext = mContext;
		this.mList = mList;
	}

	public void setOnItemClickListener(ProfitDetialClickListener listener) {
		this.mItemClickListener = listener;
	}


	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		ProfitViewHolder profitViewHolder;
		profitViewHolder = new ProfitViewHolder(MaterialRippleView.create(LayoutInflater
				.from(mContext).inflate(R.layout.item_mine_account_profit, parent, false)), mItemClickListener);
		return profitViewHolder;
	}

	@Override
	public int getItemViewType(int position) {
		return 0;
	}

	@Override
	public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
		if (holder instanceof ProfitViewHolder) {
			if (null == mList.get(position).getBackUserId()) {
				((ProfitViewHolder) holder).llWithdrawal.setVisibility(View.GONE);
				((ProfitViewHolder) holder).llCash.setVisibility(View.VISIBLE);
				if (!TextUtils.isEmpty(mList.get(position).getCashMoney())) {
					((ProfitViewHolder) holder).tvCashMoney.setText("￥" + mList.get(position).getCashMoney());
				}
				if (!TextUtils.isEmpty(mList.get(position).getAlipayAccount())) {
					((ProfitViewHolder) holder).tvAlipayName.setText(mList.get(position).getAlipayAccount());
				}
				if (!TextUtils.isEmpty(mList.get(position).getCashState())) {
					if (mList.get(position).getCashState().equals("0")){
						((ProfitViewHolder) holder).tvCashState.setText("等待审核");
						((ProfitViewHolder) holder).tvCashState.setTextColor(mContext.getResources().getColor(R.color.CE8_3C_3C));
					}else if (mList.get(position).getCashState().equals("1")){
						((ProfitViewHolder) holder).tvCashState.setText("审核通过");
						((ProfitViewHolder) holder).tvCashState.setTextColor(mContext.getResources().getColor(R.color.C66_66_66));
					}else if (mList.get(position).getCashState().equals("2")){
						((ProfitViewHolder) holder).tvCashState.setText("审核驳回");
						((ProfitViewHolder) holder).tvCashState.setTextColor(mContext.getResources().getColor(R.color.CED_08_3C));
					}
				}
				if (!TextUtils.isEmpty(mList.get(position).getInsertTime())) {
					((ProfitViewHolder) holder).tvInsertTime.setText(mList.get(position).getInsertTime());
				}
			} else {
				((ProfitViewHolder) holder).llWithdrawal.setVisibility(View.VISIBLE);
				((ProfitViewHolder) holder).llCash.setVisibility(View.GONE);
				if (!TextUtils.isEmpty(mList.get(position).getBackUserHeadImg())) {

					Glide.with(mContext)
							.load(URLBuilder.getUrl(mList.get(position).getBackUserHeadImg()))
							.asBitmap()
							.fitCenter()
							.error(R.mipmap.default_avatar)
							.into(((ProfitViewHolder) holder).ivHeader);
				}
				if (!TextUtils.isEmpty(mList.get(position).getInsertTime())) {
					((ProfitViewHolder) holder).tvTime.setText(mList.get(position).getInsertTime());
				}
				if (!TextUtils.isEmpty(mList.get(position).getBackUserName())) {
					((ProfitViewHolder) holder).tvUserName.setText(mList.get(position).getBackUserName());
				}
				if (!TextUtils.isEmpty(mList.get(position).getMoney())) {
					((ProfitViewHolder) holder).tvMoney.setText("￥" + mList.get(position).getMoney());
				}
			}
		}
	}

	@Override
	public int getItemCount() {
		return mList.size();
	}

	class ProfitViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		@BindView(R.id.ll_withdrawal)
		LinearLayout llWithdrawal;
		@BindView(R.id.ll_cash)
		LinearLayout llCash;
		@BindView(R.id.iv_header)
		RoundedImageView ivHeader;
		@BindView(R.id.tv_user_name)
		TextView tvUserName;
		@BindView(R.id.tv_time)
		TextView tvTime;
		@BindView(R.id.tv_money)
		TextView tvMoney;
		@BindView(R.id.tv_cash_money)
		TextView tvCashMoney;
		@BindView(R.id.tv_alipay_name)
		TextView tvAlipayName;
		@BindView(R.id.tv_insert_time)
		TextView tvInsertTime;
		@BindView(R.id.tv_cash_state)
		TextView tvCashState;
		private ProfitDetialClickListener mListener;

		public ProfitViewHolder(View itemView, ProfitDetialClickListener listener) {
			super(itemView);
			ButterKnife.bind(this, itemView);
			this.mListener = listener;
			itemView.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			if (mListener != null) {
				mListener.onItemClick(v, getPosition());
			}
		}
	}

	public interface ProfitDetialClickListener {
		void onItemClick(View view, int postion);
	}
}