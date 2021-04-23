package kr.co.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import kr.co.vo.cms_board_vo;

@Component("cms_file_utils")
public class cms_file_utils {
	
	private static final String filePath = "C:\\workspace\\file\\";
	
	public static String getRandomString() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	public List<Map<String, Object>> parseInsertFileInfo(cms_board_vo cms_board_vo, MultipartHttpServletRequest mprequest) throws Exception {
		
		Iterator<String> iterator = mprequest.getFileNames();
		
		MultipartFile multipartFile = null;
		String originalFileName = null;
		String originalFileExtension = null;
		String storedFileName = null;
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> listMap = null;
		
		int cms_bno = cms_board_vo.getCms_bno();
		
		File file = new File(filePath);
		
		if(file.exists() == false) {
			file.mkdirs();
		}
		
		while(iterator.hasNext()) {
			multipartFile = mprequest.getFile(iterator.next());
			
			if(multipartFile.isEmpty() == false) {
				originalFileName = multipartFile.getOriginalFilename();
				originalFileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
				storedFileName = getRandomString() + originalFileExtension;
				
				file = new File(filePath + storedFileName);
				multipartFile.transferTo(file);
				listMap = new HashMap<String, Object>();
				listMap.put("cms_bno", cms_bno);
				listMap.put("cms_org_file_name", originalFileName);
				listMap.put("cms_stored_file_name", storedFileName);
				listMap.put("cms_file_size", multipartFile.getSize());
				list.add(listMap);
			}
		}
		
		return list;
	}
	
	public List<Map<String, Object>> parseUpdateFileInfo(cms_board_vo cms_board_vo, String[] files
		, String[] fileNames, MultipartHttpServletRequest mpRequest) throws Exception {
		
		Iterator<String> iterator = mpRequest.getFileNames();
		
		MultipartFile multipartFile = null;
		String originalFileName = null;
		String originalFileExtension = null;
		String storedFileName = null;
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> listMap = null;
		
		int cms_bno = cms_board_vo.getCms_bno();
		
		while(iterator.hasNext()) {
			multipartFile = mpRequest.getFile(iterator.next());
			
			if(multipartFile.isEmpty() == false) {
				originalFileName = multipartFile.getOriginalFilename();
				originalFileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
				storedFileName = getRandomString() + originalFileExtension;
				
				multipartFile.transferTo(new File(filePath + storedFileName));
				listMap = new HashMap<String, Object>();
				listMap.put("IS_NEW", "Y");
				listMap.put("cms_bno", cms_bno);
				listMap.put("cms_org_file_name", originalFileName);
				listMap.put("cms_stored_file_name", storedFileName);
				listMap.put("cms_file_size", multipartFile.getSize());
				list.add(listMap);
			}
		}
		
		if(files != null && fileNames != null) {
			for(int i=0; i<fileNames.length; i++) {
				listMap = new HashMap<String, Object>();
				listMap.put("IS_NEW", "N");
				listMap.put("cms_file_no", files[i]);
				list.add(listMap);
			}
		}
		
		return list;
	}
	
}
