package kr.co.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.co.service.mms_member_service;
import kr.co.vo.mms_member_vo;

@Controller
public class dms_main_controller {
	
	private final static Logger logger = LoggerFactory.getLogger(cms_board_controller.class);

	@Autowired
	private mms_member_service member_service;
	
	@RequestMapping(value = "/main", method = RequestMethod.GET)
	public String main(mms_member_vo mms_member_vo, HttpServletRequest req
			, RedirectAttributes rttr) throws Exception {
		logger.info("main");
		
		HttpSession session = req.getSession();
		mms_member_vo login = member_service.login(mms_member_vo);
		
		if(login == null) {
			session.setAttribute("member", null);
			rttr.addFlashAttribute("msg", false);
		} else {
			session.setAttribute("member", login);
		}
		
		return "main";
	}
	
}
