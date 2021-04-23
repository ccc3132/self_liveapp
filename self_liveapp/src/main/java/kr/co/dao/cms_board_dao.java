package kr.co.dao;

import java.util.List;
import java.util.Map;

import kr.co.vo.SearchCriteria;
import kr.co.vo.cms_board_vo;

public interface cms_board_dao {
	
	//게시글 작성
	public int write(cms_board_vo cms_board_vo) throws Exception;
	
	//게시글 목록 조회
	public List<cms_board_vo> list(SearchCriteria scri) throws Exception;
	
	//게시글 조회
	public cms_board_vo read(int cms_bno) throws Exception;
	
	//게시글 수정
	public int update(cms_board_vo cms_board_vo) throws Exception;
	
	//게시글 삭제
	public int delete(int cms_bno) throws Exception;
	
	//게시글 총 갯수
	public int list_count(SearchCriteria scri) throws Exception;
	
	//파일등록
	public int insert_file(Map<String, Object> map) throws Exception;
	
	//파일조회
	public List<Map<String, Object>> select_file_list(int cms_bno) throws Exception;

	//파일다운
	public Map<String, Object> select_file_info(Map<String, Object> map) throws Exception;
	
	//파일수정
	public int update_file(Map<String, Object> map) throws Exception;
	
	//조회수
	public int board_hit(int cms_bno) throws Exception;
	
}
