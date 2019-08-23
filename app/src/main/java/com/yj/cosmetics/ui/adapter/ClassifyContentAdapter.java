package com.yj.cosmetics.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yj.cosmetics.R;
import com.yj.cosmetics.base.URLBuilder;
import com.yj.cosmetics.model.ClassifyProductEntity;
import com.yj.cosmetics.ui.activity.goodDetail.GoodsDetailActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Suo on 2017/4/17.
 */

public class ClassifyContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private Context mContext;
	List<ClassifyProductEntity.DataBean> data;
	SpendDetialClickListener mItemClickListener;

	public ClassifyContentAdapter(Context mContext, List<ClassifyProductEntity.DataBean> data) {
		this.mContext = mContext;
		this.data = data;
	}

	public void setOnItemClickListener(SpendDetialClickListener listener) {
		this.mItemClickListener = listener;
	}


	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		ContentViewHolder contentViewHolder = new ContentViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_classify_content_goods, parent, false), mItemClickListener);
		return contentViewHolder;
	}

	@Override
	public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
		if (data.get(position) != null && data.size() > 0) {
			((ContentViewHolder) holder).tvTitle.setText(data.get(position).getProductName());
			((ContentViewHolder) holder).tvPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
			((ContentViewHolder) holder).tvContent.setText("￥" + data.get(position).getProductCurrent());
			((ContentViewHolder) holder).tvPrice.setText("￥" + data.get(position).getProductOrginal());
			//获取自定义的类实例
			Glide.with(mContext)
					.load(URLBuilder.getUrl(data.get(position).getProductListImg()))
					.asBitmap()
					.error(R.mipmap.default_goods)
					.centerCrop()
					.into(((ContentViewHolder) holder).ivProductImg);
			((ContentViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent intent = new Intent(mContext, GoodsDetailActivity.class);
					intent.putExtra("productId", data.get(position).getProductId());
					mContext.startActivity(intent);
				}
			});
			if (data.get(position).getIsVip().equals("1")) {
				((ContentViewHolder) holder).ivVip.setVisibility(View.VISIBLE);
			} else if (data.get(position).getIsVip().equals("0")) {
				((ContentViewHolder) holder).ivVip.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public int getItemCount() {
		if (data != null) {
			return data.size();
		}
		return 0;
	}


	public void setData(List<ClassifyProductEntity.DataBean> data) {
		this.data = data;
		notifyDataSetChanged();
	}

	public void clearData() {
		data.clear();
		notifyDataSetChanged();
	}


	class ContentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		@BindView(R.id.iv_product_img)
		ImageView ivProductImg;
		@BindView(R.id.tv_title)
		TextView tvTitle;
		@BindView(R.id.tv_content)
		TextView tvContent;
		@BindView(R.id.tv_price)
		TextView tvPrice;
		@BindView(R.id.iv_vip)
		ImageView ivVip;
		private SpendDetialClickListener mListener;

		public ContentViewHolder(View itemView, SpendDetialClickListener listener) {
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

	public interface SpendDetialClickListener {
		void onItemClick(View view, int postion);
	}
}