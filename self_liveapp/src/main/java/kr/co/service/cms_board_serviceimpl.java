package kr.co.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import kr.co.dao.cms_board_dao;
import kr.co.vo.SearchCriteria;
import kr.co.vo.cms_board_vo;
import kr.co.util.cms_file_utils;

@Service
public class cms_board_serviceimpl implements cms_board_service {
	
	@Autowired
	private cms_board_dao dao;
	
	@Resource(name = "cms_file_utils")
	private cms_file_utils cms_file_utils;

	@Override
	public int write(cms_board_vo cms_board_vo, MultipartHttpServletRequest mpRequest) throws Exception {
		// TODO Auto-generated method stub
		
		int result = dao.write(cms_board_vo);
		
		List<Map<String, Object>> list = cms_file_utils.parseInsertFileInfo(cms_board_vo, mpRequest);
		
		int size = list.size();
		
		for(int i = 0; i < size; i++) {
			dao.insert_file(list.get(i));
		}
		
		return result;
	}

	@Override
	public List<cms_board_vo> list(SearchCriteria scri) throws Exception {
		// TODO Auto-generated method stub
		return dao.list(scri);
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	@Override
	public cms_board_vo read(int cms_bno) throws Exception {
		// TODO Auto-generated method stub
		
		dao.board_hit(cms_bno);
		
		return dao.read(cms_bno);
	}

	@Override
	public int update(cms_board_vo cms_board_vo, String[] files
			, String[] fileNames, MultipartHttpServletRequest mpRequest) throws Exception {
		// TODO Auto-generated method stub
		
		int result = dao.update(cms_board_vo);
		
		List<Map<String, Object>> list = cms_file_utils.parseUpdateFileInfo(cms_board_vo, files, fileNames, mpRequest);
		Map<String, Object> tempMap = null;
		int size = list.size();
		
		for(int i=0; i<size; i++) {
			tempMap = list.get(i);
			
			if(tempMap.get("IS_NEW").equals("Y")) {
				dao.insert_file(tempMap);
			} else {
				dao.update_file(tempMap);
			}
		}
		
		return result;
	}

	@Override
	public int delete(int cms_bno) throws Exception {
		// TODO Auto-generated method stub
		return dao.delete(cms_bno);
	}

	@Override
	public int list_count(SearchCriteria scri) throws Exception {
		// TODO Auto-generated method stub
		return dao.list_count(scri);
	}

	@Override
	public List<Map<String, Object>> select_file_list(int cms_bno) throws Exception {
		// TODO Auto-generated method stub
		return dao.select_file_list(cms_bno);
	}

	@Override
	public Map<String, Object> select_file_info(Map<String, Object> map) throws Exception {
		// TODO Auto-generated method stub
		return dao.select_file_info(map);
	}

	@Override
	public int update_file(Map<String, Object> map) throws Exception {
		// TODO Auto-generated method stub
		return dao.update_file(map);
	}

	@Override
	public int board_hit(int cms_bno) throws Exception {
		// TODO Auto-generated method stub
		return dao.board_hit(cms_bno);
	}

}
