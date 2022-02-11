package global.sesoc.library.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import global.sesoc.library.dao.BoardDAO;
import global.sesoc.library.util.FileService;
import global.sesoc.library.util.PageNavigator;
import global.sesoc.library.vo.Board;
import global.sesoc.library.vo.Reply;

/**
 * 寃뚯떆�뙋 肄섑듃濡ㅻ윭
 */
@Controller
@RequestMapping("board")
public class BoardController {
	private static final Logger logger = LoggerFactory.getLogger(BoardController.class);
	
	@Autowired
	BoardDAO dao;
	
	//寃뚯떆�뙋 愿��젴 �긽�닔媛믩뱾
	final int countPerPage = 10;			//�럹�씠吏��떦 湲� �닔
	final int pagePerGroup = 5;				//�럹�씠吏� �씠�룞 留곹겕瑜� �몴�떆�븷 �럹�씠吏� �닔
	final String uploadPath = "/boardfile";	//�뙆�씪 �뾽濡쒕뱶 寃쎈줈

	/**
	 * 湲��벐湲� �뤌 蹂닿린
	 */
	@RequestMapping (value="write", method=RequestMethod.GET)
	public String write() {
		return "boardjsp/writeForm";
	}
	
	/** 
	 * 湲� ���옣
	 */
	@RequestMapping (value="write", method=RequestMethod.POST)
	public String write(
			HttpSession session
			, Model model
			, Board board 
			, MultipartFile upload) {
		
		//�꽭�뀡�뿉�꽌 濡쒓렇�씤�븳 �궗�슜�옄�쓽 �븘�씠�뵒瑜� �씫�뼱�꽌 Board媛앹껜�쓽 �옉�꽦�옄 �젙蹂댁뿉 �꽭�똿
		String id = (String) session.getAttribute("loginId");
		board.setId(id);
		
		logger.info("���옣�븷 湲� �젙蹂� : {}", board);
		logger.debug("�뙆�씪 �젙蹂� : {}", upload.getContentType());
		logger.debug("�뙆�씪 �젙蹂� : {}", upload.getName());
		logger.debug("�뙆�씪 �젙蹂� : {}", upload.getOriginalFilename());
		logger.debug("�뙆�씪 �젙蹂� : {}", upload.getSize());
		logger.debug("�뙆�씪 �젙蹂� : {}", upload.isEmpty());
	
		//泥⑤��뙆�씪�씠 �엳�뒗 寃쎌슦 吏��젙�맂 寃쎈줈�뿉 ���옣�븯怨�, �썝蹂� �뙆�씪紐낃낵 ���옣�맂 �뙆�씪紐낆쓣 Board媛앹껜�뿉 �꽭�똿
		if (!upload.isEmpty()) {
			String savedfile = FileService.saveFile(upload, uploadPath);
			board.setOriginalfile(upload.getOriginalFilename());
			board.setSavedfile(savedfile);
		}
		
		dao.insertBoard(board);
		return "redirect:list";
	}
	
	/**
	 * 湲�紐⑸줉
	 */
	@RequestMapping (value="list", method=RequestMethod.GET)
	public String list(
			@RequestParam(value="page", defaultValue="1") int page
			, @RequestParam(value="searchText", defaultValue="") String searchText
			, Model model) {
		
		logger.debug("page: {}, searchText: {}", page, searchText);
		
		int total = dao.getTotal(searchText);			//�쟾泥� 湲� 媛쒖닔
		
		//�럹�씠吏� 怨꾩궛�쓣 �쐞�븳 媛앹껜 �깮�꽦
		PageNavigator navi = new PageNavigator(countPerPage, pagePerGroup, page, total); 
		
		//寃��깋�뼱�� �떆�옉 �쐞移�, �럹�씠吏��떦 湲� �닔瑜� �쟾�떖�븯�뿬 紐⑸줉 �씫湲�
		ArrayList<Board> boardlist = dao.listBoard(searchText, navi.getStartRecord(), navi.getCountPerPage());	
		
		//�럹�씠吏� �젙蹂� 媛앹껜�� 湲� 紐⑸줉, 寃��깋�뼱瑜� 紐⑤뜽�뿉 ���옣
		model.addAttribute("boardlist", boardlist);
		model.addAttribute("navi", navi);
		model.addAttribute("searchText", searchText);
		
		//return "boardjsp/boardList";
		return "viewer";
	}

	/**
	 * 湲� �씫湲�
	 * @param boardnum �씫�쓣 湲�踰덊샇
	 * @return �빐�떦 湲� �젙蹂�
	 */
	@RequestMapping (value="read", method=RequestMethod.GET)
	public String read(int boardnum, Model model) {
		//�쟾�떖�맂 湲� 踰덊샇濡� �빐�떦 湲��젙蹂� �씫湲�
		Board board = dao.getBoard(boardnum);
		if (board == null) {
			return "redirect:list";
		}
		
		//�빐�떦 湲��뿉 �떖由� 由ы뵆紐⑸줉 �씫湲�
		ArrayList<Reply> replylist = dao.listReply(boardnum);
		
		//蹂몃Ц湲��젙蹂댁� 由ы뵆紐⑸줉�쓣 紐⑤뜽�뿉 ���옣
		model.addAttribute("board", board);
		model.addAttribute("replylist", replylist);
		
		return "boardjsp/boardRead";
	}
	
	/**
	 * �뙆�씪 �떎�슫濡쒕뱶
	 * @param boardnum �뙆�씪�씠 泥⑤��맂 湲� 踰덊샇
	 */
	@RequestMapping(value = "download", method = RequestMethod.GET)
	public String fileDownload(int boardnum, Model model, HttpServletResponse response) {
		Board board = dao.getBoard(boardnum);
		
		//�썝�옒�쓽 �뙆�씪紐�
		String originalfile = new String(board.getOriginalfile());
		try {
			response.setHeader("Content-Disposition", " attachment;filename="+ URLEncoder.encode(originalfile, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		//���옣�맂 �뙆�씪 寃쎈줈
		String fullPath = uploadPath + "/" + board.getSavedfile();
		
		//�꽌踰꾩쓽 �뙆�씪�쓣 �씫�쓣 �엯�젰 �뒪�듃由쇨낵 �겢�씪�씠�뼵�듃�뿉寃� �쟾�떖�븷 異쒕젰�뒪�듃由�
		FileInputStream filein = null;
		ServletOutputStream fileout = null;
		
		try {
			filein = new FileInputStream(fullPath);
			fileout = response.getOutputStream();
			
			//Spring�쓽 �뙆�씪 愿��젴 �쑀�떥 �씠�슜�븯�뿬 異쒕젰
			FileCopyUtils.copy(filein, fileout);
			
			filein.close();
			fileout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 湲� �궘�젣
	 */
	@RequestMapping (value="delete", method=RequestMethod.GET)
	public String delete(HttpSession session, int boardnum) {
		String id = (String) session.getAttribute("loginId");
		
		//�궘�젣�븷 湲� 踰덊샇�� 蹂몄씤 湲��씤吏� �솗�씤�븷 濡쒓렇�씤�븘�씠�뵒
		Board board = new Board();
		board.setBoardnum(boardnum);
		board.setId(id);
		
		//泥⑤��맂 �뙆�씪�씠 �엳�뒗吏� 癒쇱� �솗�씤
		String savedfile = dao.getBoard(boardnum).getSavedfile();
		
		//湲� �궘�젣
		int result = dao.deleteBoard(board);
		
		//湲� �궘�젣 �꽦怨� and 泥⑤��맂 �뙆�씪�씠 �엳�뒗 寃쎌슦 �뙆�씪�룄 �궘�젣
		if (result == 1 && savedfile != null) {
			FileService.deleteFile(uploadPath + "/" + savedfile);
		}
		
		return "redirect:list";
	}
	
	/**
	 * 湲� �닔�젙 �뤌�쑝濡� �씠�룞
	 */
	@RequestMapping (value="edit", method=RequestMethod.GET)
	public String editForm(HttpSession session, Model model, int boardnum) {
		
		Board board = dao.getBoard(boardnum);
		model.addAttribute("board", board);
		return "boardjsp/editForm";
	}
	
	/**
	 * 湲� �닔�젙 泥섎━
	 * @param board �닔�젙�븷 湲� �젙蹂�
	 */
	@RequestMapping (value="edit", method=RequestMethod.POST)
	public String edit(
			HttpSession session
			, Board board
			, MultipartFile upload) {
		
		//�닔�젙�븷 湲��씠 濡쒓렇�씤�븳 蹂몄씤 湲��씤吏� �솗�씤
		String id = (String) session.getAttribute("loginId");
		Board oldBoard = dao.getBoard(board.getBoardnum());
		if (oldBoard == null || !oldBoard.getId().equals(id)) {
			return "redirect:list";
		}
		
		//�닔�젙�븷 �젙蹂댁뿉 濡쒓렇�씤 �븘�씠�뵒 ���옣
		board.setId(id);
		
		//�닔�젙 �떆 �깉濡� 泥⑤��븳 �뙆�씪�씠 �엳�쑝硫� 湲곗〈 �뙆�씪�쓣 �궘�젣�븯怨� �깉濡� �뾽濡쒕뱶
		if (!upload.isEmpty()) {
			//湲곗〈 湲��뿉 泥⑤��맂 �뙆�씪�쓽 �떎�젣 ���옣�맂 �씠由�
			String savedfile = oldBoard.getSavedfile();
			//湲곗〈 �뙆�씪�씠 �엳�쑝硫� �궘�젣
			if (savedfile != null) {
				FileService.deleteFile(uploadPath + "/" + savedfile);
			}
			
			//�깉濡� �뾽濡쒕뱶�븳 �뙆�씪 ���옣
			savedfile = FileService.saveFile(upload, uploadPath);
			
			//�닔�젙 �젙蹂댁뿉 �깉濡� ���옣�맂 �뙆�씪紐낃낵 �썝�옒�쓽 �뙆�씪紐� ���옣
			board.setOriginalfile(upload.getOriginalFilename());
			board.setSavedfile(savedfile);
		}
		
		//湲� �닔�젙 泥섎━
		dao.updateBoard(board);
		//�썝�옒�쓽 湲��씫湲� �솕硫댁쑝濡� �씠�룞 
		return "redirect:read?boardnum=" + board.getBoardnum();
	}
	
	/**
	 * 由ы뵆 ���옣 泥섎━
	 */
	@RequestMapping (value="replyWrite", method=RequestMethod.POST)
	public String replyWrite(
			Reply reply, 
			HttpSession session, 
			Model model) {
		
		//�꽭�뀡�뿉�꽌 濡쒓렇�씤�븳 �궗�슜�옄�쓽 �븘�씠�뵒瑜� �씫�뼱�꽌 Reply媛앹껜�쓽 �옉�꽦�옄 �젙蹂댁뿉 �꽭�똿
		String id = (String) session.getAttribute("loginId");
		reply.setId(id);
		
		//由ы뵆 �젙蹂대�� DB�뿉 ���옣
		dao.insertReply(reply);
		
		//�씫�뜕 寃뚯떆湲�濡� �릺�룎�븘 媛�
		return "redirect:read?boardnum=" + reply.getBoardnum();
	}
	
	/**
	 * 由ы뵆 �궘�젣
	 */
	@RequestMapping (value="replyDelete", method=RequestMethod.GET)
	public String deleteReply(Reply reply, HttpSession session) {
		String id = (String) session.getAttribute("loginId");
		
		//�궘�젣�븷 湲� 踰덊샇�� 蹂몄씤 湲��씤吏� �솗�씤�븷 濡쒓렇�씤�븘�씠�뵒
		reply.setId(id);
		
		dao.deleteReply(reply);
		return "redirect:read?boardnum=" + reply.getBoardnum();
	}
	
	/**
	 * 由ы뵆 �닔�젙 泥섎━
	 * @param reply �닔�젙�븷 由ы뵆 �젙蹂�
	 */
	@RequestMapping (value="replyEdit", method=RequestMethod.POST)
	public String replyEdit(HttpSession session, Reply reply) {
		
		//�궘�젣�븷 由ы뵆 �젙蹂댁� 蹂몄씤 湲��씤吏� �솗�씤�븷 濡쒓렇�씤�븘�씠�뵒
		String id = (String) session.getAttribute("loginId");
		reply.setId(id);
		
		//由ы뵆  �닔�젙 泥섎━
		dao.updateReply(reply);
		//�썝�옒�쓽 湲��씫湲� �솕硫댁쑝濡� �씠�룞 
		return "redirect:read?boardnum=" + reply.getBoardnum();
	}
}
