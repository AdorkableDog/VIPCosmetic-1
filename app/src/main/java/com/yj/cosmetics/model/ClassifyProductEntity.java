package com.yj.cosmetics.model;

import java.util.List;

public class ClassifyProductEntity {


	/**
	 * msg : 成功
	 * code : 200
	 * data : [{"productId":250,"productListImg":"https://wjf-imgs.oss-cn-qingdao.aliyuncs.com/upload/head/productImg/2019/08/06/1cf1ab77-7ace-4b98-8e5c-809f343317cd.png","productOrginal":100,"productCurrent":50,"productName":"自然堂"}]
	 */

	private String msg;
	private String code;
	private List<DataBean> data;

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public List<DataBean> getData() {
		return data;
	}

	public void setData(List<DataBean> data) {
		this.data = data;
	}

	public static class DataBean {
		/**
		 * productId : 250
		 * productListImg : https://wjf-imgs.oss-cn-qingdao.aliyuncs.com/upload/head/productImg/2019/08/06/1cf1ab77-7ace-4b98-8e5c-809f343317cd.png
		 * productOrginal : 100
		 * productCurrent : 50
		 * productName : 自然堂
		 */

		private String productId;
		private String productListImg;
		private String productOrginal;
		private String productCurrent;
		private String productName;
		private String isVip;

		public String getProductId() {
			return productId;
		}

		public void setProductId(String productId) {
			this.productId = productId;
		}

		public String getProductListImg() {
			return productListImg;
		}

		public void setProductListImg(String productListImg) {
			this.productListImg = productListImg;
		}

		public String getProductOrginal() {
			return productOrginal;
		}

		public void setProductOrginal(String productOrginal) {
			this.productOrginal = productOrginal;
		}

		public String getProductCurrent() {
			return productCurrent;
		}

		public void setProductCurrent(String productCurrent) {
			this.productCurrent = productCurrent;
		}

		public String getProductName() {
			return productName;
		}

		public void setProductName(String productName) {
			this.productName = productName;
		}

		public String getIsVip() {
			return isVip;
		}

		public void setIsVip(String isVip) {
			this.isVip = isVip;
		}
	}
}
