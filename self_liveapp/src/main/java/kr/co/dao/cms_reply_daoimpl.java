package kr.co.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import kr.co.vo.cms_reply_vo;

@Repository
public class cms_reply_daoimpl implements cms_reply_dao {
	
	@Autowired
	private SqlSession sql;

	@Override
	public List<cms_reply_vo> read_reply(int cms_bno) throws Exception {
		// TODO Auto-generated method stub
		return sql.selectList("cms_reply.read_reply", cms_bno);
	}

	@Override
	public int write_reply(cms_reply_vo cms_reply_vo) throws Exception {
		// TODO Auto-generated method stub
		return sql.insert("cms_reply.write_reply", cms_reply_vo);
	}

}
