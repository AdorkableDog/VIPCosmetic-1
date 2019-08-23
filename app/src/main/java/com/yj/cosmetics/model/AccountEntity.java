package com.yj.cosmetics.model;

import java.util.List;

public class AccountEntity {

	private String code;
	private String msg;
	private List<AccountData> data;
	public final String HTTP_OK = "200";

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public List<AccountData> getData() {
		return data;
	}

	public void setData(List<AccountData> data) {
		this.data = data;
	}

	public class AccountData {
		String userId; //用户id
		String backUserId;//返利用户Id
		String backUserName;    //返利用户昵称
		String backUserHeadImg;//返利用户头像
		String money;//返利金额
		String insertTime;//时间
		String alipayAccount; //账户
		String alipayName; //姓名
		String cashMoney;//提下金额
		String cashState; //状态

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public String getBackUserId() {
			return backUserId;
		}

		public void setBackUserId(String backUserId) {
			this.backUserId = backUserId;
		}

		public String getBackUserName() {
			return backUserName;
		}

		public void setBackUserName(String backUserName) {
			this.backUserName = backUserName;
		}

		public String getBackUserHeadImg() {
			return backUserHeadImg;
		}

		public void setBackUserHeadImg(String backUserHeadImg) {
			this.backUserHeadImg = backUserHeadImg;
		}

		public String getMoney() {
			return money;
		}

		public void setMoney(String money) {
			this.money = money;
		}

		public String getInsertTime() {
			return insertTime;
		}

		public void setInsertTime(String insertTime) {
			this.insertTime = insertTime;
		}

		public String getAlipayAccount() {
			return alipayAccount;
		}

		public void setAlipayAccount(String alipayAccount) {
			this.alipayAccount = alipayAccount;
		}

		public String getAlipayName() {
			return alipayName;
		}

		public void setAlipayName(String alipayName) {
			this.alipayName = alipayName;
		}

		public String getCashMoney() {
			return cashMoney;
		}

		public void setCashMoney(String cashMoney) {
			this.cashMoney = cashMoney;
		}

		public String getCashState() {
			return cashState;
		}

		public void setCashState(String cashState) {
			this.cashState = cashState;
		}
	}
}
