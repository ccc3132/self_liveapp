package kr.co.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.dao.mms_member_dao;
import kr.co.vo.mms_member_vo;

@Service
public class mms_member_serviceimpl implements mms_member_service {

	@Autowired
	private mms_member_dao dao;

	@Override
	public int register(mms_member_vo mms_member_vo) throws Exception {
		// TODO Auto-generated method stub
		return dao.register(mms_member_vo);
	}

	@Override
	public mms_member_vo login(mms_member_vo mms_member_vo) throws Exception {
		// TODO Auto-generated method stub
		return dao.login(mms_member_vo);
	}

	@Override
	public int memberupdate(mms_member_vo mms_member_vo) throws Exception {
		// TODO Auto-generated method stub
		return dao.memberupdate(mms_member_vo);
	}

	@Override
	public int memberdelete(mms_member_vo mms_member_vo) throws Exception {
		// TODO Auto-generated method stub
		return dao.memberdelete(mms_member_vo);
	}

	@Override
	public int passchk(mms_member_vo mms_member_vo) throws Exception {
		// TODO Auto-generated method stub
		int result = dao.passchk(mms_member_vo);
		return result;
	}

	@Override
	public int idchk(mms_member_vo mms_member_vo) throws Exception {
		// TODO Auto-generated method stub
		int result = dao.idchk(mms_member_vo);
		
		return result;
	}
	
}
