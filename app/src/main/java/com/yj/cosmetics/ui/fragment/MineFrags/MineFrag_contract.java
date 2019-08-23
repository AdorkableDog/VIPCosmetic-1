package com.yj.cosmetics.ui.fragment.MineFrags;

import com.yj.cosmetics.base.BasePresenter;
import com.yj.cosmetics.base.BaseView;
import com.yj.cosmetics.model.MineEntity;
import com.yj.cosmetics.model.NormalEntity;
import com.yj.cosmetics.util.UserUtils;
import com.yj.cosmetics.widget.Dialog.CustomNormalContentDialog;
import com.sobot.chat.api.model.Information;

/**
 * Created by Administrator on 2018/6/4 0004.
 */

public interface MineFrag_contract {

	interface View extends BaseView {

		void setMineNewNum(boolean b, NormalEntity response);

		void setDatas(MineEntity.MineData data);

		void showToast(String s);

		void setSobotApi(Information userInfo);
	}

	interface Presenter extends BasePresenter {

		void setServiceTel(String serviceTel);

		void doCustomServices();

		void doAsyncGetInfo(UserUtils mUtils);

		void dismissDialog(CustomNormalContentDialog mDialog);
	}

}
