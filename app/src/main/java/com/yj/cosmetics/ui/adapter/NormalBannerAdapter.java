package com.yj.cosmetics.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.LoopPagerAdapter;
import com.yj.cosmetics.R;
import com.yj.cosmetics.base.URLBuilder;
import com.yj.cosmetics.model.HomeEntity;
import com.yj.cosmetics.util.DensityUtil;
import com.yj.cosmetics.util.LogUtils;
import com.yj.cosmetics.widget.CornerTransform;
import com.yj.cosmetics.widget.RoundedCornersTransform;

import java.util.List;

/**
 * Created by Suo on 2017/8/19.
 */

public class NormalBannerAdapter extends LoopPagerAdapter {
	private int[] imgs = {R.drawable.artists_bg, R.drawable.artists_bg, R.drawable.artists_bg};
	private int count = imgs.length;

	private List<HomeEntity.HomeData.HomeBanner> mList;
	private Context context;


	public NormalBannerAdapter(RollPagerView viewPager, List<HomeEntity.HomeData.HomeBanner> mList, Context context) {
		super(viewPager);
		this.mList = mList;
		this.context = context;
	}


	@Override
	public View getView(ViewGroup container, final int position) {
		final int picNo = position + 1;

//		final ImageView view = new ImageView(container.getContext());

		View inflate = LayoutInflater.from(context).inflate(R.layout.layout_home_banner, null);
		ImageView view = inflate.findViewById(R.id.iv_banner_img);
		if (mList == null) {
			LogUtils.i("我在mList== null中");
//			view.setImageResource(imgs[position]);
			view.setScaleType(ImageView.ScaleType.CENTER_CROP);
//
//			view.setLayouview.setPadding(
//					DensityUtil.dip2px(context, 10),
//					DensityUtil.dip2px(context, 10),
//					DensityUtil.dip2px(context, 10),
//					DensityUtil.dip2px(context, 10)
//			);tParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		} else {
			LogUtils.i("我在有banner数据中" + mList.get(position).getBannerImg());
			view.setScaleType(ImageView.ScaleType.CENTER_CROP);
//			view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
			LogUtils.i(URLBuilder.URLBaseHeader + mList.get(position).getBannerImg());
			CornerTransform transformation = new CornerTransform(context, DensityUtil.dip2px(context, 10));
			//只是绘制左上角和右上角圆角
			transformation.setExceptCorner(false, false, false, false);
			Glide.with(context)
					.load(URLBuilder.getUrl(mList.get(position).getBannerImg()))
					.asBitmap()
					.skipMemoryCache(true)
//					.centerCrop()
					.error(R.mipmap.default_banner_empty)
					.transform(transformation)
					.into(view);
					/*.into(new SimpleTarget<GlideDrawable>() {
						@Override
						public void onResourceReady(GlideDrawable glideDrawable, GlideAnimation<? super GlideDrawable> glideAnimation) {
							view.setImageDrawable(glideDrawable);
						}

						@Override
						public void onLoadCleared(Drawable placeholder) {
							super.onLoadCleared(placeholder);
						}

						@Override
						public void onLoadFailed(Exception e, Drawable errorDrawable) {
							super.onLoadFailed(e, errorDrawable);
							LogUtils.i("我loadFailed了" + e);
							view.setImageResource(R.mipmap.default_banner_empty);
						}
					});*/
			LogUtils.i("我加载完banner了" + position);
		}
		return inflate;
	}

	@Override
	public int getRealCount() {
		if (mList == null) {
			return count;
		} else {
			return mList.size();
		}
	}
}
