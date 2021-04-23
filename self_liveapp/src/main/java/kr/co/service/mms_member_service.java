package kr.co.service;

import kr.co.vo.mms_member_vo;

public interface mms_member_service {
	
	//회원가입
	public int register(mms_member_vo mms_member_vo) throws Exception;
	
	//로그인
	public mms_member_vo login(mms_member_vo mms_member_vo) throws Exception;
	
	//회원정보 수정
	public int memberupdate(mms_member_vo mms_member_vo) throws Exception;
	
	//회원탈퇴
	public int memberdelete(mms_member_vo mms_member_vo) throws Exception;
	
	//패스워드 체크
	public int passchk(mms_member_vo mms_member_vo) throws Exception;
	
	//아이디 중복 체크
	public int idchk(mms_member_vo mms_member_vo) throws Exception;

}
