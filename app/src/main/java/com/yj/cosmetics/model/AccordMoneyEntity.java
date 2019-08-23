package com.yj.cosmetics.model;

public class AccordMoneyEntity {

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


		private String userMoney;
		private String userCash;
		private String cashRate;

		public String getUserMoney() {
			return userMoney;
		}

		public void setUserMoney(String userMoney) {
			this.userMoney = userMoney;
		}

		public String getUserCash() {
			return userCash;
		}

		public void setUserCash(String userCash) {
			this.userCash = userCash;
		}

		public String getCashRate() {
			return cashRate;
		}

		public void setCashRate(String cashRate) {
			this.cashRate = cashRate;
		}
	}
}