package egovframework.egovwebpractice.mail.service.impl;

import egovframework.egovwebpractice.mail.service.MailAccountService;
import egovframework.egovwebpractice.mail.service.MailAccountVO;
import jakarta.annotation.Resource;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.egovframe.rte.fdl.crypto.EgovEnvCryptoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 표준프레임워크 서비스 규칙: EgovAbstractServiceImpl 을 상속하고 서비스 인터페이스를 구현한다. */
@Service("mailAccountService")
@Transactional(readOnly = true)
public class MailAccountServiceImpl extends EgovAbstractServiceImpl implements MailAccountService {

    private final MailAccountDAO mailAccountDAO;

    @Resource(name = "egovEnvCryptoService")
    private EgovEnvCryptoService cryptoService;

    public MailAccountServiceImpl(MailAccountDAO mailAccountDAO) {
        this.mailAccountDAO = mailAccountDAO;
    }

    @Override
    public MailAccountVO selectMailAccount(String userId) {
        MailAccountVO vo = mailAccountDAO.selectMailAccount(userId);
        if (vo != null) {
            // 화면/로그에 평문 비밀번호가 보이지 않도록 비워 둔다. 수신 시에만 복호화해서 쓴다.
            vo.setPassword("");
        }
        return vo;
    }

    @Override
    public MailAccountVO selectMailAccountWithPassword(String userId) {
        MailAccountVO vo = mailAccountDAO.selectMailAccount(userId);
        if (vo != null) {
            vo.setPassword(cryptoService.decrypt(vo.getEncryptedPassword()));
        }
        return vo;
    }

    @Override
    @Transactional
    public void saveMailAccount(MailAccountVO vo) {
        vo.setEncryptedPassword(cryptoService.encrypt(vo.getPassword()));
        MailAccountVO existing = mailAccountDAO.selectMailAccount(vo.getUserId());
        if (existing == null) {
            mailAccountDAO.insertMailAccount(vo);
        } else {
            mailAccountDAO.updateMailAccount(vo);
        }
    }
}
