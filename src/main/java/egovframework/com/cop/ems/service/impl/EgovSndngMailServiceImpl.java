package egovframework.com.cop.ems.service.impl;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.commons.mail2.core.EmailException;
import org.apache.commons.mail2.jakarta.EmailAttachment;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import egovframework.com.cop.ems.service.EgovMultiPartEmail;
import egovframework.com.cop.ems.service.EgovSndngMailService;
import egovframework.com.cop.ems.service.SndngMailVO;
import jakarta.annotation.Resource;

/**
 * 메일 솔루션과 연동해서 이용해서 메일을 보내는 서비스 구현 클래스
 * @since 2011.09.09
 * @version 1.0
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      수정자          수정내용
 *  -------    --------    ---------------------------
 *  2011.09.09  서준식       최초 작성
 *  2011.12.06  이기하       메일 첨부파일이 기능 추가
 *  2013.05.23  이기하       메일 첨부파일이 없을 때 로직 추가
 *
 *  </pre>
 */
@Service("egovSndngMailService")
public class EgovSndngMailServiceImpl extends EgovAbstractServiceImpl implements EgovSndngMailService {

	@Resource(name = "egovMultiPartEmail")
	private EgovMultiPartEmail egovMultiPartEmail;

	/** SndngMailRegistDAO */
	@Resource(name = "sndngMailRegistDAO")
	private SndngMailRegistDAO sndngMailRegistDAO;

	private static final Logger LOGGER = LoggerFactory.getLogger(EgovSndngMailServiceImpl.class);

	// SMTP 호스트가 설정되지 않았거나 응답이 없을 때 Commons Email 의 소켓 타임아웃 설정이
	// 실제로는 지켜지지 않아 요청이 몇 분씩 멈추는 현상이 있었다(2026.11 브라우저 점검 중 발견).
	// 라이브러리 내부 동작에 기대지 않고 별도 스레드 + 하드 타임아웃으로 강제 차단한다.
	private static final long SEND_TIMEOUT_SECONDS = 20;
	private static final ExecutorService MAIL_EXECUTOR = Executors.newCachedThreadPool(r -> {
		Thread t = new Thread(r, "egov-mail-send");
		t.setDaemon(true);
		return t;
	});

	/**
	 * 메일을 발송한다
	 * @param vo SndngMailVO
	 * @return boolean
	 */
	@Override
	public boolean sndngMail(SndngMailVO sndngMailVO) {

		String recptnPerson = (sndngMailVO.getRecptnPerson() == null) ? "" : sndngMailVO.getRecptnPerson(); // 수신자
		String subject = (sndngMailVO.getSj() == null) ? "" : sndngMailVO.getSj(); // 메일제목
		String emailCn = (sndngMailVO.getEmailCn() == null) ? "" : sndngMailVO.getEmailCn(); // 메일내용
		String atchmnFileNm = (sndngMailVO.getOrignlFileNm() == null) ? "" : sndngMailVO.getOrignlFileNm(); // 첨부파일이름
		String atchmnFilePath = (sndngMailVO.getFileStreCours() == null) ? "" : sndngMailVO.getFileStreCours(); // 첨부파일경로

		try {
			EmailAttachment attachment = new EmailAttachment();
			// 첨부파일이 있을 때
			boolean hasAttachment = atchmnFileNm != null && !atchmnFileNm.isEmpty() && atchmnFilePath != null && !atchmnFilePath.isEmpty();
			if (hasAttachment) {
				// 첨부할 attachment 정보를 생성합니다
				attachment.setPath(atchmnFilePath);
				attachment.setDisposition(EmailAttachment.ATTACHMENT);
				attachment.setDescription("첨부파일입니다");
				//attachment.setName(new String(atchmnFileNm.getBytes("UTF-8"),"latin1")); // 구버전의 경우 필요
				attachment.setName(atchmnFileNm);
			}

			Callable<String> sendTask = hasAttachment
					? () -> egovMultiPartEmail.send(recptnPerson, subject, emailCn, attachment)
					: () -> egovMultiPartEmail.send(recptnPerson, subject, emailCn);
			sendWithHardTimeout(sendTask);
		} catch (EmailException ex) {
			sndngMailVO.setSndngResultCode("F"); // 발송결과 실패
			sndngMailRegistDAO.updateSndngMail(sndngMailVO); // 발송상태를 DB에 업데이트 한다.
			LOGGER.error("Sending Mail Exception : {} [failure when sending the message]", ex.getMessage(), ex);
			return false;
		}

		return true;
	}

	/** SMTP 연결이 멈춰 있어도 SEND_TIMEOUT_SECONDS 안에는 반드시 돌아오게 강제한다. */
	private void sendWithHardTimeout(Callable<String> sendTask) throws EmailException {
		Future<String> future = MAIL_EXECUTOR.submit(sendTask);
		try {
			future.get(SEND_TIMEOUT_SECONDS, TimeUnit.SECONDS);
		} catch (TimeoutException te) {
			future.cancel(true);
			throw new EmailException("메일 서버(" + SEND_TIMEOUT_SECONDS + "초) 연결에 실패했습니다. SMTP 설정을 확인하세요.");
		} catch (ExecutionException ee) {
			Throwable cause = ee.getCause();
			if (cause instanceof EmailException) {
				throw (EmailException) cause;
			}
			throw new EmailException("메일 발송 중 오류가 발생했습니다.", cause);
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
			throw new EmailException("메일 발송이 중단되었습니다.", ie);
		}
	}

}
