package egovframework.egovwebpractice.msg.web;

import egovframework.com.cmm.LoginVO;
import egovframework.com.cmm.util.EgovUserDetailsHelper;
import egovframework.egovwebpractice.msg.service.MessageVO;
import egovframework.egovwebpractice.msg.service.MessengerService;
import egovframework.com.utl.fcc.service.EgovStringUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 메신저(1:1 대화, 직접 개발 - 공통컴포넌트에 없는 모듈).
 * 실시간 push(WebSocket) 는 아직 없고, 화면 새로고침/재방문으로 갱신된다.
 */
@Controller
public class MessengerController {

    private final MessengerService messengerService;

    public MessengerController(MessengerService messengerService) {
        this.messengerService = messengerService;
    }

    private LoginVO currentUser() {
        return (LoginVO) EgovUserDetailsHelper.getAuthenticatedUser();
    }

    @GetMapping("/msg/EgovMessengerMain.do")
    public String main(Model model) throws Exception {
        LoginVO user = currentUser();
        model.addAttribute("conversationList", messengerService.selectConversationList(user.getUniqId()));
        return "egovframework/egovwebpractice/msg/EgovMessengerMain";
    }

    @GetMapping("/msg/EgovMessengerConversation.do")
    public String conversation(@RequestParam("partnerId") String partnerId,
            @RequestParam(value = "partnerNm", required = false) String partnerNm, Model model) throws Exception {
        LoginVO user = currentUser();
        model.addAttribute("partnerId", partnerId);
        model.addAttribute("partnerNm", EgovStringUtil.isNullToString(partnerNm));
        model.addAttribute("messageList", messengerService.selectConversation(user.getUniqId(), partnerId));
        return "egovframework/egovwebpractice/msg/EgovMessengerConversation";
    }

    @PostMapping("/msg/sendMessage.do")
    public String sendMessage(@ModelAttribute MessageVO messageVO,
            @RequestParam("partnerNm") String partnerNm) throws Exception {
        LoginVO user = currentUser();
        messageVO.setSenderId(user.getUniqId());
        messageVO.setSenderNm(user.getName());
        messageVO.setReceiverNm(partnerNm);
        messengerService.sendMessage(messageVO);
        return "redirect:/msg/EgovMessengerConversation.do?partnerId=" + messageVO.getReceiverId()
                + "&partnerNm=" + partnerNm;
    }
}
