package kr.co.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import kr.co.vo.SearchCriteria;
import kr.co.vo.cms_board_vo;

@Repository
public class cms_board_daoimpl implements cms_board_dao {
	
	@Autowired
	private SqlSession sql;

	@Override
	public int write(cms_board_vo cms_board_vo) throws Exception {
		// TODO Auto-generated method stub
		return sql.insert("cms_board.write", cms_board_vo);
	}

	@Override
	public List<cms_board_vo> list(SearchCriteria scri) throws Exception {
		// TODO Auto-generated method stub
		return sql.selectList("cms_board.list", scri);
	}

	@Override
	public cms_board_vo read(int cms_bno) throws Exception {
		// TODO Auto-generated method stub
		return sql.selectOne("cms_board.read", cms_bno);
	}

	@Override
	public int update(cms_board_vo cms_board_vo) throws Exception {
		// TODO Auto-generated method stub
		return sql.update("cms_board.update", cms_board_vo);
	}

	@Override
	public int delete(int cms_bno) throws Exception {
		// TODO Auto-generated method stub
		return sql.delete("cms_board.delete", cms_bno);
	}

	@Override
	public int list_count(SearchCriteria scri) throws Exception {
		// TODO Auto-generated method stub
		return sql.selectOne("cms_board.list_count", scri);
	}

	@Override
	public int insert_file(Map<String, Object> map) throws Exception {
		// TODO Auto-generated method stub
		return sql.insert("cms_board.insert_file", map);
	}

	@Override
	public List<Map<String, Object>> select_file_list(int cms_bno) throws Exception {
		// TODO Auto-generated method stub
		return sql.selectList("cms_board.select_file_list", cms_bno);
	}

	@Override
	public Map<String, Object> select_file_info(Map<String, Object> map) throws Exception {
		// TODO Auto-generated method stub
		return sql.selectOne("cms_board.select_file_info", map);
	}

	@Override
	public int update_file(Map<String, Object> map) throws Exception {
		// TODO Auto-generated method stub
		return sql.update("cms_board.update_file", map);
	}

	@Override
	public int board_hit(int cms_bno) throws Exception {
		// TODO Auto-generated method stub
		return sql.update("cms_board.board_hit", cms_bno);
	}

}
