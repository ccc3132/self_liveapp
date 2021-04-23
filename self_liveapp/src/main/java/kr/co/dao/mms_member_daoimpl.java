package kr.co.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import kr.co.vo.mms_member_vo;

@Repository
public class mms_member_daoimpl implements mms_member_dao {
	
	@Autowired
	private SqlSession sql;

	@Override
	public int register(mms_member_vo mms_member_vo) throws Exception {
		// TODO Auto-generated method stub
		return sql.insert("mms_member.register", mms_member_vo);
	}

	@Override
	public mms_member_vo login(mms_member_vo mms_member_vo) throws Exception {
		// TODO Auto-generated method stub
		return sql.selectOne("mms_member.login", mms_member_vo);
	}

	@Override
	public int memberupdate(mms_member_vo mms_member_vo) throws Exception {
		// TODO Auto-generated method stub
		return sql.update("mms_member.memberupdate", mms_member_vo);
	}

	@Override
	public int memberdelete(mms_member_vo mms_member_vo) throws Exception {
		// TODO Auto-generated method stub
		return sql.delete("mms_member.memberdelete", mms_member_vo);
	}

	@Override
	public int passchk(mms_member_vo mms_member_vo) throws Exception {
		// TODO Auto-generated method stub
		int result = sql.selectOne("mms_member.passchk", mms_member_vo);
		
		return result;
	}

	@Override
	public int idchk(mms_member_vo mms_member_vo) throws Exception {
		// TODO Auto-generated method stub
		int result = sql.selectOne("mms_member.idchk", mms_member_vo);
		
		return result;
	}

}
