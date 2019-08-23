package com.yj.cosmetics.model;

import java.util.List;

public class ClassifyEntity {
	private String code;
	private String msg;
	private List<ClassifyItem> data;
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

	public List<ClassifyItem> getData() {
		return data;
	}

	public void setData(List<ClassifyItem> data) {
		this.data = data;
	}

	public class ClassifyItem {
		private String classify_id;
		private String classify_name;
		private String classify_img;

		public String getClassify_id() {
			return classify_id;
		}

		public void setClassify_id(String classify_id) {
			this.classify_id = classify_id;
		}

		public String getClassify_img() {
			return classify_img;
		}

		public void setClassify_img(String classify_img) {
			this.classify_img = classify_img;
		}

		public String getClassify_name() {
			return classify_name;
		}

		public void setClassify_name(String classify_name) {
			this.classify_name = classify_name;
		}
	}
}
