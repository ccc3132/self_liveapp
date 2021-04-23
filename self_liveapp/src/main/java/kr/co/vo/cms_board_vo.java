package kr.co.vo;

import java.util.Date;

public class cms_board_vo {
	
	private int cms_bno;
	private int cms_hit;
	private String cms_title;
	private String cms_content;
	private String cms_writer;
	private Date cms_regdate;
	
	public cms_board_vo() {}

	public int getCms_bno() {
		return cms_bno;
	}

	public void setCms_bno(int cms_bno) {
		this.cms_bno = cms_bno;
	}

	public int getCms_hit() {
		return cms_hit;
	}

	public void setCms_hit(int cms_hit) {
		this.cms_hit = cms_hit;
	}

	public String getCms_title() {
		return cms_title;
	}

	public void setCms_title(String cms_title) {
		this.cms_title = cms_title;
	}

	public String getCms_content() {
		return cms_content;
	}

	public void setCms_content(String cms_content) {
		this.cms_content = cms_content;
	}

	public String getCms_writer() {
		return cms_writer;
	}

	public void setCms_writer(String cms_writer) {
		this.cms_writer = cms_writer;
	}

	public Date getCms_regdate() {
		return cms_regdate;
	}

	public void setCms_regdate(Date cms_regdate) {
		this.cms_regdate = cms_regdate;
	}

	public cms_board_vo(int cms_bno, int cms_hit, String cms_title, String cms_content, String cms_writer,
			Date cms_regdate) {
		super();
		this.cms_bno = cms_bno;
		this.cms_hit = cms_hit;
		this.cms_title = cms_title;
		this.cms_content = cms_content;
		this.cms_writer = cms_writer;
		this.cms_regdate = cms_regdate;
	}

	@Override
	public String toString() {
		return "cms_board_vo [cms_bno=" + cms_bno + ", cms_hit=" + cms_hit + ", cms_title=" + cms_title
				+ ", cms_content=" + cms_content + ", cms_writer=" + cms_writer + ", cms_regdate=" + cms_regdate
				+ ", toString()=" + super.toString() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + cms_bno;
		result = prime * result + ((cms_content == null) ? 0 : cms_content.hashCode());
		result = prime * result + cms_hit;
		result = prime * result + ((cms_regdate == null) ? 0 : cms_regdate.hashCode());
		result = prime * result + ((cms_title == null) ? 0 : cms_title.hashCode());
		result = prime * result + ((cms_writer == null) ? 0 : cms_writer.hashCode());
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
		cms_board_vo other = (cms_board_vo) obj;
		if (cms_bno != other.cms_bno)
			return false;
		if (cms_content == null) {
			if (other.cms_content != null)
				return false;
		} else if (!cms_content.equals(other.cms_content))
			return false;
		if (cms_hit != other.cms_hit)
			return false;
		if (cms_regdate == null) {
			if (other.cms_regdate != null)
				return false;
		} else if (!cms_regdate.equals(other.cms_regdate))
			return false;
		if (cms_title == null) {
			if (other.cms_title != null)
				return false;
		} else if (!cms_title.equals(other.cms_title))
			return false;
		if (cms_writer == null) {
			if (other.cms_writer != null)
				return false;
		} else if (!cms_writer.equals(other.cms_writer))
			return false;
		return true;
	}

}
