package egovframework.egovwebpractice.common.service.impl;

import egovframework.egovwebpractice.common.service.SystemCheckVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SystemCheckMapper {

    SystemCheckVO selectSystemCheck();
}
