package kr.co.controller;

import java.io.File;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.co.service.cms_board_service;
import kr.co.service.cms_reply_service;
import kr.co.vo.PageMaker;
import kr.co.vo.SearchCriteria;
import kr.co.vo.cms_board_vo;
import kr.co.vo.cms_reply_vo;

@Controller
public class cms_board_controller {
	
	private final static Logger logger = LoggerFactory.getLogger(cms_board_controller.class);
	
	@Autowired
	private cms_board_service board_service;
	
	@Autowired
	private cms_reply_service reply_service;
	
	//게시글 작성 화면
	@RequestMapping(value = "/dms_board/dms_0010_write_view", method = RequestMethod.GET)
	public void dms_0010_write_view() throws Exception {
		logger.info("dms_0010_write_view");
	}
	
	//게시글 작성
	@RequestMapping(value = "/dms_board/dms_0010_write", method = RequestMethod.POST)
	public String dms_0010_write(cms_board_vo cms_board_vo
			, MultipartHttpServletRequest mprequest) throws Exception {
		logger.info("dms_0010_write");
		
		board_service.write(cms_board_vo, mprequest);
		
		return "redirect:/dms_board/dms_0010_list";
	}
	
	//게시글 목록 조회
	@RequestMapping(value = "/dms_board/dms_0010_list", method = RequestMethod.GET)
	public String dms_0010_list(Model model, @ModelAttribute("scri") SearchCriteria scri) throws Exception {
		logger.info("dms_0010_list");
		
		PageMaker pageMaker = new PageMaker();
		pageMaker.setCri(scri);
		pageMaker.setTotalCount(board_service.list_count(scri));
		
		model.addAttribute("pageMaker", pageMaker);
		model.addAttribute("list", board_service.list(scri));
		
		return "dms_board/dms_0010_list";
	}
	
	//게시글 조회
	@RequestMapping(value = "/dms_board/dms_0010_read_view", method = RequestMethod.GET)
	public String dms_0010_read_view(cms_board_vo cms_board_vo, Model model
			, @ModelAttribute("scri") SearchCriteria scri) throws Exception {
		logger.info("dms_0010_read_view");
		
		model.addAttribute("read", board_service.read(cms_board_vo.getCms_bno()));
		model.addAttribute("scri", scri);
		
		List<cms_reply_vo> replyList = reply_service.read_reply(cms_board_vo.getCms_bno());
		model.addAttribute("replyList", replyList);
		
		List<Map<String, Object>> cms_file_list = board_service.select_file_list(cms_board_vo.getCms_bno());
		model.addAttribute("file", cms_file_list);
		
		return "dms_board/dms_0010_read_view";
	}
	
	//게시글 수정 화면
	@RequestMapping(value = "/dms_board/dms_0010_update_view", method = RequestMethod.GET)
	public String dms_0010_update_view (cms_board_vo cms_board_vo, Model model
			, @ModelAttribute("scri") SearchCriteria scri) throws Exception {
		logger.info("dms_0010_update_view");
		
		model.addAttribute("update", board_service.read(cms_board_vo.getCms_bno()));
		model.addAttribute("scri", scri);
		
		List<Map<String, Object>> fileList = board_service.select_file_list(cms_board_vo.getCms_bno());
		model.addAttribute("file", fileList);
		
		return "dms_board/dms_0010_update_view";
	}
	
	//게시글 수정
	@RequestMapping(value = "/dms_board/dms_0010_update", method = RequestMethod.POST)
	public String dms_0010_update(cms_board_vo cms_board_vo, RedirectAttributes rttr
			, @ModelAttribute("scri") SearchCriteria scri
			, @RequestParam(value="fileNoDel[]") String[] files
			, @RequestParam(value="fileNameDel[]") String[] fileNames
			, MultipartHttpServletRequest mpRequest) throws Exception {
		logger.info("dms_0010_update");
		
		board_service.update(cms_board_vo, files, fileNames, mpRequest);
		
		rttr.addAttribute("page", scri.getPage());
		rttr.addAttribute("perPageNum", scri.getPerPageNum());
		rttr.addAttribute("searchType", scri.getSearchType());
		rttr.addAttribute("keyword", scri.getKeyword());
		
		return "redirect:/dms_board/dms_0010_list";
	}
	
	//게시글 삭제
	@RequestMapping(value = "/dms_board/dms_0010_delete", method = RequestMethod.POST)
	public String dms_0010_delete(cms_board_vo cms_board_vo, RedirectAttributes rttr
			, @ModelAttribute("scri") SearchCriteria scri) throws Exception {
		logger.info("dms_0010_delete");
		
		board_service.delete(cms_board_vo.getCms_bno());
		
		rttr.addAttribute("page", scri.getPage());
		rttr.addAttribute("perPageNum", scri.getPerPageNum());
		rttr.addAttribute("searchType", scri.getSearchType());
		rttr.addAttribute("keyword", scri.getKeyword());
		
		return "redirect:/dms_board/dms_0010_list";
	}
	
	//파일다운
	@RequestMapping(value = "/file_down")
	public void file_down(@RequestParam Map<String, Object> map
			, HttpServletResponse response) throws Exception {
		logger.info("file_down");
		
		Map<String, Object> resultMap = board_service.select_file_info(map);
		String storedFileName = (String) resultMap.get("cms_stored_file_name");
		String originalFileName = (String) resultMap.get("cms_org_file_name");
		
		//파일을 저장했던 위치에서 첨부파일을 읽어 byte[]형식으로 변환
		byte file_byte[] = org.apache.commons.io.FileUtils.readFileToByteArray(new File("C:\\workspace\\file\\" + storedFileName));
	
		response.setContentType("application/octet-stream");
		response.setContentLength(file_byte.length);
		response.setHeader("Content-Disposition", "attachment; fileName=\"" + URLEncoder.encode(originalFileName, "UTF-8") + "\";");
		response.getOutputStream().write(file_byte);
		response.getOutputStream().flush();
		response.getOutputStream().close();
		
	}
	
	//답글작성 화면
	@RequestMapping(value = "/dms_reply/dms_0010_reply_view", method = RequestMethod.GET)
	public void dms_0010_reply_view(cms_board_vo cms_board_vo, Model model
			, @ModelAttribute("scri") SearchCriteria scri) throws Exception {
		logger.info("dms_0010_reply_view");
		
		model.addAttribute("list", cms_board_vo);
	}
	
	//답글작성
	@RequestMapping(value = "/dms_reply/dms_0010_write", method = RequestMethod.POST)
	public String dms_0010_write(cms_reply_vo cms_reply_vo, RedirectAttributes rttr
			, @ModelAttribute("scri") SearchCriteria scri) throws Exception {
		logger.info("dms_0010_write");

		reply_service.write_reply(cms_reply_vo);
		
		rttr.addAttribute("cms_bno", cms_reply_vo.getCms_bno());
		rttr.addAttribute("page", scri.getPage());
		rttr.addAttribute("perPageNum", scri.getPerPageNum());
		rttr.addAttribute("searchType", scri.getSearchType());
		rttr.addAttribute("keyword", scri.getKeyword());
		
		return "redirect:/dms_board/dms_0010_list";
	}

}