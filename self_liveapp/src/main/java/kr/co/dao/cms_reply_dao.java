package kr.co.dao;

import java.util.List;

import kr.co.vo.cms_reply_vo;

public interface cms_reply_dao {
	
	//댓글 조회
	public List<cms_reply_vo> read_reply(int cms_bno) throws Exception;
	
	//댓글 작성
	public int write_reply(cms_reply_vo cms_reply_vo) throws Exception;

}
