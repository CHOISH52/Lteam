package global.sesoc.library.controller;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import global.sesoc.library.dao.MemberDAO;
import global.sesoc.library.vo.Member;


@Controller
@RequestMapping("member")
public class MemberController {
	private static final Logger logger = LoggerFactory.getLogger(MemberController.class);
	
	@Autowired
	MemberDAO dao;
	
	
	@RequestMapping (value="join", method=RequestMethod.GET)
	public String joinForm(Model model) {
		return "memberjsp/joinForm";
	}

	
	@RequestMapping (value="join", method=RequestMethod.POST)
	public String join(Model model, Member member) {
		
		int result = dao.insert(member);
		if (result != 1) {
			return "memberjsp/joinForm";
		}
		return "redirect:/";
	}
	
	
	@RequestMapping (value="idcheck", method=RequestMethod.GET)
	public String idcheck(Model model) {
		return "memberjsp/idcheck";
	}

	
	@RequestMapping (value="idcheck", method=RequestMethod.POST)
	public String idcheck(Model model, String searchId) {
		Member member = dao.getMember(searchId);
		model.addAttribute("member", member);		//寃��깋 寃곌낵媛� �뾾�쑝硫� null
		model.addAttribute("searchId", searchId); 	//�궗�슜�옄媛� 寃��깋�븳 ID
		return "memberjsp/idcheck";
	}

	
	@RequestMapping (value="login", method=RequestMethod.GET)
	public String login() {
		return "memberjsp/loginForm";
	}

	
	@RequestMapping (value="login", method=RequestMethod.POST)
	public String login(String id, String password, Model model, HttpSession session) {
		Member member = dao.getMember(id);
		

		if (member != null && member.getPassword().equals(password)) {
			session.setAttribute("loginId", member.getId());
			session.setAttribute("loginName", member.getName());
			return "redirect:/";
		}
		
		else {
			model.addAttribute("errorMsg", "ID ");
			return "memberjsp/loginForm";
		}
	}
	

	@RequestMapping (value="logout", method=RequestMethod.GET)
	public String logout(HttpSession session) {
		session.removeAttribute("loginId");
		session.removeAttribute("loginName");
		return "redirect:/";
	}
	

	@RequestMapping (value="update", method=RequestMethod.GET)
	public String update(Model model, HttpSession session) {
		String loginId = (String) session.getAttribute("loginId");
		Member member = dao.getMember(loginId);
		model.addAttribute("member", member);
		return "memberjsp/updateForm";
	}

	
	@RequestMapping (value="update", method=RequestMethod.POST)
	public String update(Member member, Model model, HttpSession session) {
		String loginId = (String) session.getAttribute("loginId");
		member.setId(loginId);;
		logger.debug("�닔�젙 �뜲�씠�꽣 : {}", member);
		
		int res = dao.updateMember(member);
		if (res != 0) {
			return "redirect:/";
		}
		else {
			return "memberjsp/updateForm";
		}
	}
	
	@RequestMapping (value="index", method=RequestMethod.GET)
	public String index(Model model) {
		return "memberjsp/index";
	}

}
