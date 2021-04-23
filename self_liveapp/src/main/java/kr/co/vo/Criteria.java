package kr.co.vo;

public class Criteria {
	
	private int page;
	private int pageStart;
	private int perPageNum;
	private int rowStart;
	private int rowEnd;

	public Criteria() {
		this.page = 1;
		this.perPageNum = 10;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		if(page <= 0) {
			this.page = 1;
			return;
		}
		
		this.page = page;
	}

	public int getPageStart() {
		return (this.page - 1) * perPageNum;
	}

	public void setPageStart(int pageStart) {
		this.pageStart = pageStart;
	}

	public int getPerPageNum() {
		return perPageNum;
	}

	public void setPerPageNum(int perPageNum) {
		if(perPageNum <= 0 || perPageNum > 100) {
			this.perPageNum = 10;
			return;
		}
		
		this.perPageNum = perPageNum;
	}

	public int getRowStart() {
		rowStart = ((page - 1) * perPageNum) + 1;
		
		return rowStart;
	}

	public void setRowStart(int rowStart) {
		this.rowStart = rowStart;
	}

	public int getRowEnd() {
		rowEnd = rowStart + perPageNum - 1;
		
		return rowEnd;
	}

	public void setRowEnd(int rowEnd) {
		this.rowEnd = rowEnd;
	}

	public Criteria(int page, int pageStart, int perPageNum, int rowStart, int rowEnd) {
		super();
		this.page = page;
		this.pageStart = pageStart;
		this.perPageNum = perPageNum;
		this.rowStart = rowStart;
		this.rowEnd = rowEnd;
	}

	@Override
	public String toString() {
		return "Criteria [page=" + page + ", pageStart=" + pageStart + ", perPageNum=" + perPageNum + ", rowStart="
				+ rowStart + ", rowEnd=" + rowEnd + ", toString()=" + super.toString() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + page;
		result = prime * result + pageStart;
		result = prime * result + perPageNum;
		result = prime * result + rowEnd;
		result = prime * result + rowStart;
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
		Criteria other = (Criteria) obj;
		if (page != other.page)
			return false;
		if (pageStart != other.pageStart)
			return false;
		if (perPageNum != other.perPageNum)
			return false;
		if (rowEnd != other.rowEnd)
			return false;
		if (rowStart != other.rowStart)
			return false;
		return true;
	}
	
}
