package kr.co.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.dao.cms_reply_dao;
import kr.co.vo.cms_reply_vo;

@Service
public class cms_reply_serviceimpl implements cms_reply_service {
	
	@Autowired
	private cms_reply_dao dao;

	@Override
	public List<cms_reply_vo> read_reply(int cms_bno) throws Exception {
		// TODO Auto-generated method stub
		return dao.read_reply(cms_bno);
	}

	@Override
	public int write_reply(cms_reply_vo cms_reply_vo) throws Exception {
		// TODO Auto-generated method stub
		return dao.write_reply(cms_reply_vo);
	}

}
