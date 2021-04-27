package kr.co.controller;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.co.service.mms_member_service;
import kr.co.vo.mms_member_vo;

@Controller
public class mms_member_controller {
	
	private final static Logger logger = LoggerFactory.getLogger(cms_board_controller.class);
	
	@Autowired
	private mms_member_service member_service;
	
	@Autowired
	private BCryptPasswordEncoder pwd_encoder;
	
	//회원가입 GET
	@RequestMapping(value = "/dms_member/dms_0010_register", method = RequestMethod.GET)
	public void get_dms_0010_register() throws Exception {
		logger.info("get_dms_0010_register");
	}
	
	//회원가입 POST
	@RequestMapping(value = "/dms_member/dms_0010_register", method = RequestMethod.POST)
	public String post_dms_0010_register(mms_member_vo mms_member_vo) throws Exception {
		logger.info("post_dms_0010_register");
		
		int result = member_service.idchk(mms_member_vo);
		
		try {
			if(result == 1) {
				return "/dms_member/dms_0010_register";
			} else if(result == 0) {
				String inputPass = mms_member_vo.getMms_userpass();
				String pwd = pwd_encoder.encode(inputPass);
				mms_member_vo.setMms_userpass(pwd);
				
				member_service.register(mms_member_vo);
			}
		} catch(Exception e) {
			throw new RuntimeException();
		}
		
		return "redirect:/main";
	}
	
	//로그인
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(mms_member_vo mms_member_vo, HttpSession session
			, RedirectAttributes rttr) throws Exception {
		logger.info("login");
		
		session.getAttribute("member");
		mms_member_vo login = member_service.login(mms_member_vo);
		boolean pwdMatch = pwd_encoder.matches(mms_member_vo.getMms_userpass(), login.getMms_userpass());
		
		if(login != null && pwdMatch == true) {
			session.setAttribute("member", login);
		} else {
			session.setAttribute("member", null);
			rttr.addFlashAttribute("msg", false);
		}
		
		return "main";
	}
	
	//로그아웃
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(HttpSession session) throws Exception {
		logger.info("logout");
		
		session.invalidate();
		
		return "redirect:/main";
	}
	
	//회원정보수정 뷰
	@RequestMapping(value = "/dms_member/dms_0010_memberupdate_view", method = RequestMethod.GET)
	public String dms_0010_memberupdate_view() throws Exception {
		logger.info("dms_0010_memberupdate_view");
		
		return "/dms_member/dms_0010_memberupdate_view";
	}
	
	//회원정보수정
	@RequestMapping(value = "/dms_member/dms_0010_memberupdate", method = RequestMethod.POST)
	public String dms_0010_memberupdate(mms_member_vo mms_member_vo, HttpSession session) throws Exception {
		logger.info("dms_0010_memberupdate");
		
		member_service.memberupdate(mms_member_vo);
		
		session.invalidate();
		
		return "redirect:/main";
	}
	
	//마이페이지 뷰
	@RequestMapping(value = "/dms_member/dms_0010_member_view", method = RequestMethod.GET)
	public String dms_0010_member_view() throws Exception {
		logger.info("dms_0010_member_view");
		
		return "/dms_member/dms_0010_member_view";
	}
	
	//회원탈퇴 뷰
	@RequestMapping(value = "/dms_member/dms_0010_memberdelete_view", method = RequestMethod.GET)
	public String dms_0010_memberdelete_view() throws Exception {
		logger.info("dms_0010_memberdelete_view");
		
		return "/dms_member/dms_0010_memberdelete_view";
	}
	
	//회원탈퇴
	@RequestMapping(value = "/dms_member/dms_0010_memberdelete", method = RequestMethod.POST)
	public String dms_0010_memberdelete(mms_member_vo mms_member_vo, HttpSession session
			, RedirectAttributes rttr) throws Exception {
		logger.info("dms_0010_memberdelete");
		
		member_service.memberdelete(mms_member_vo);
		
		session.invalidate();
		
		return "redirect:/main";
	}
	
	//패스워드 체크
	@ResponseBody
	@RequestMapping(value = "/dms_member/passchk", method = RequestMethod.POST)
	public boolean passchk(mms_member_vo mms_member_vo) throws Exception {
		logger.info("passchk");
		
		mms_member_vo login = member_service.login(mms_member_vo);
		boolean pwdChk = pwd_encoder.matches(mms_member_vo.getMms_userpass(), login.getMms_userpass());
		
		return pwdChk;
	}
	
	//아이디 체크
	@ResponseBody
	@RequestMapping(value = "/dms_member/idchk", method = RequestMethod.POST)
	public int idchk(mms_member_vo mms_member_vo) throws Exception {
		logger.info("idchk");
		
		int result = member_service.idchk(mms_member_vo);
		
		return result;
	}

}
