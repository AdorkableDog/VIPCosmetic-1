package com.yj.cosmetics.model;

public class MoneyEntity {

	/**
	 * msg : 成功
	 * code : 200
	 * data : {"orderMoneyAll":0,"orderId":3,"orderNum":"20190821111217684700","shopId":1}
	 */

	private String msg;
	private String code;
	private DataBean data;

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

	public DataBean getData() {
		return data;
	}

	public void setData(DataBean data) {
		this.data = data;
	}

	public static class DataBean {
		/**
		 * orderMoneyAll : 0
		 * orderId : 3
		 * orderNum : 20190821111217684700
		 * shopId : 1
		 */

		private int orderMoneyAll;
		private String orderId;
		private String orderNum;
		private int shopId;

		public int getOrderMoneyAll() {
			return orderMoneyAll;
		}

		public void setOrderMoneyAll(int orderMoneyAll) {
			this.orderMoneyAll = orderMoneyAll;
		}

		public String getOrderId() {
			return orderId;
		}

		public void setOrderId(String orderId) {
			this.orderId = orderId;
		}

		public String getOrderNum() {
			return orderNum;
		}

		public void setOrderNum(String orderNum) {
			this.orderNum = orderNum;
		}

		public int getShopId() {
			return shopId;
		}

		public void setShopId(int shopId) {
			this.shopId = shopId;
		}
	}
}
