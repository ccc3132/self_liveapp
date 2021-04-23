package kr.co.vo;

import java.util.Date;

public class mms_member_vo {
	
	private String mms_userid;
	private String mms_userpass;
	private String mms_username;
	private Date mms_regdate;
	
	public mms_member_vo() {}

	public String getMms_userid() {
		return mms_userid;
	}

	public void setMms_userid(String mms_userid) {
		this.mms_userid = mms_userid;
	}

	public String getMms_userpass() {
		return mms_userpass;
	}

	public void setMms_userpass(String mms_userpass) {
		this.mms_userpass = mms_userpass;
	}

	public String getMms_username() {
		return mms_username;
	}

	public void setMms_username(String mms_username) {
		this.mms_username = mms_username;
	}

	public Date getMms_regdate() {
		return mms_regdate;
	}

	public void setMms_regdate(Date mms_regdate) {
		this.mms_regdate = mms_regdate;
	}

	public mms_member_vo(String mms_userid, String mms_userpass, String mms_username, Date mms_regdate) {
		super();
		this.mms_userid = mms_userid;
		this.mms_userpass = mms_userpass;
		this.mms_username = mms_username;
		this.mms_regdate = mms_regdate;
	}

	@Override
	public String toString() {
		return "mms_member_vo [mms_userid=" + mms_userid + ", mms_userpass=" + mms_userpass + ", mms_username="
				+ mms_username + ", mms_regdate=" + mms_regdate + ", toString()=" + super.toString() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mms_regdate == null) ? 0 : mms_regdate.hashCode());
		result = prime * result + ((mms_userid == null) ? 0 : mms_userid.hashCode());
		result = prime * result + ((mms_username == null) ? 0 : mms_username.hashCode());
		result = prime * result + ((mms_userpass == null) ? 0 : mms_userpass.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		mms_member_vo other = (mms_member_vo) obj;
		if (mms_regdate == null) {
			if (other.mms_regdate != null)
				return false;
		} else if (!mms_regdate.equals(other.mms_regdate))
			return false;
		if (mms_userid == null) {
			if (other.mms_userid != null)
				return false;
		} else if (!mms_userid.equals(other.mms_userid))
			return false;
		if (mms_username == null) {
			if (other.mms_username != null)
				return false;
		} else if (!mms_username.equals(other.mms_username))
			return false;
		if (mms_userpass == null) {
			if (other.mms_userpass != null)
				return false;
		} else if (!mms_userpass.equals(other.mms_userpass))
			return false;
		return true;
	}

}
