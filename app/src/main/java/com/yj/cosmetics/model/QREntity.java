package com.yj.cosmetics.model;

import android.os.Parcel;
import android.os.Parcelable;

public class QREntity implements Parcelable {
	/**
	 * msg : 成功
	 * code : 200
	 * data : {"shareUrl":"https://wjf-imgs.oss-cn-qingdao.aliyuncs.com/upload/sharePicture/2be24ebc-112a-4886-a8c7-615d3d375435.jpg","userCode":"123456"}
	 */

	public String msg;
	public String code;
	public DataBean data;
	public final String HTTP_OK ="200";

	protected QREntity(Parcel in) {
		msg = in.readString();
		code = in.readString();
	}

	public static final Creator<QREntity> CREATOR = new Creator<QREntity>() {
		@Override
		public QREntity createFromParcel(Parcel in) {
			return new QREntity(in);
		}

		@Override
		public QREntity[] newArray(int size) {
			return new QREntity[size];
		}
	};


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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(msg);
		dest.writeString(code);
	}

	public static class DataBean  implements  Parcelable{
		/**
		 * shareUrl : https://wjf-imgs.oss-cn-qingdao.aliyuncs.com/upload/sharePicture/2be24ebc-112a-4886-a8c7-615d3d375435.jpg
		 * userCode : 123456
		 */

		private String shareUrl;
		private String userCode;

		protected DataBean(Parcel in) {
			shareUrl = in.readString();
			userCode = in.readString();
		}

		public static final Creator<DataBean> CREATOR = new Creator<DataBean>() {
			@Override
			public DataBean createFromParcel(Parcel in) {
				return new DataBean(in);
			}

			@Override
			public DataBean[] newArray(int size) {
				return new DataBean[size];
			}
		};

		public String getShareUrl() {
			return shareUrl;
		}

		public void setShareUrl(String shareUrl) {
			this.shareUrl = shareUrl;
		}

		public String getUserCode() {
			return userCode;
		}

		public void setUserCode(String userCode) {
			this.userCode = userCode;
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(shareUrl);
			dest.writeString(userCode);
		}
	}
}
