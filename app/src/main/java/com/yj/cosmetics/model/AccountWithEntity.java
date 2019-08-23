package com.yj.cosmetics.model;

public class AccountWithEntity {

	/**
	 * msg : 成功
	 * code : 200
	 * data : [{"insertTime":"2019-08-05 16:13:06","backUserName":"123456","money":10,"backUserId":138,"backUserHeadImg":"/weChat/head.png","userId":139}]
	 */

	private String msg;
	private String code;
	/**
	 * data : {"insertTime":"2019-08-06 17:01:42","money":"12.0"}
	 */

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
		 * insertTime : 2019-08-06 17:01:42
		 * money : 12.0
		 */

		private String insertTime;
		private String money;

		public String getInsertTime() {
			return insertTime;
		}

		public void setInsertTime(String insertTime) {
			this.insertTime = insertTime;
		}

		public String getMoney() {
			return money;
		}

		public void setMoney(String money) {
			this.money = money;
		}
	}
}
