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


@Controller
@RequestMapping("board")
public class BoardController {
	private static final Logger logger = LoggerFactory.getLogger(BoardController.class);
	
	@Autowired
	BoardDAO dao;
	
	
	final int countPerPage = 10;			
	final int pagePerGroup = 5;			
	final String uploadPath = "/boardfile";	
	
	
	@RequestMapping (value="write", method=RequestMethod.GET)
	public String write() {
		return "boardjsp/writeForm";
	}
	
	
	@RequestMapping (value="write", method=RequestMethod.POST)
	public String write(
			HttpSession session
			, Model model
			, Board board 
			, MultipartFile upload) {
		
		
		String id = (String) session.getAttribute("loginId");
		board.setId(id);
		
		logger.info("占쏙옙占쎌삢占쎈막 疫뀐옙 占쎌젟癰귨옙 : {}", board);
		logger.debug("占쎈솁占쎌뵬 占쎌젟癰귨옙 : {}", upload.getContentType());
		logger.debug("占쎈솁占쎌뵬 占쎌젟癰귨옙 : {}", upload.getName());
		logger.debug("占쎈솁占쎌뵬 占쎌젟癰귨옙 : {}", upload.getOriginalFilename());
		logger.debug("占쎈솁占쎌뵬 占쎌젟癰귨옙 : {}", upload.getSize());
		logger.debug("占쎈솁占쎌뵬 占쎌젟癰귨옙 : {}", upload.isEmpty());
	
		
		if (!upload.isEmpty()) {
			String savedfile = FileService.saveFile(upload, uploadPath);
			board.setOriginalfile(upload.getOriginalFilename());
			board.setSavedfile(savedfile);
		}
		
		dao.insertBoard(board);
		return "redirect:list";
	}
	
	
	@RequestMapping (value="list", method=RequestMethod.GET)
	public String list(
			@RequestParam(value="page", defaultValue="1") int page
			, @RequestParam(value="searchText", defaultValue="") String searchText
			, Model model) {
		
		logger.debug("page: {}, searchText: {}", page, searchText);
		
		int total = dao.getTotal(searchText);			//占쎌읈筌ｏ옙 疫뀐옙 揶쏆뮇�땾
		
		
		PageNavigator navi = new PageNavigator(countPerPage, pagePerGroup, page, total); 
		
		
		ArrayList<Board> boardlist = dao.listBoard(searchText, navi.getStartRecord(), navi.getCountPerPage());	
		
		
		model.addAttribute("boardlist", boardlist);
		model.addAttribute("navi", navi);
		model.addAttribute("searchText", searchText);
		
		
		return "viewer";
	}

	
	@RequestMapping (value="read", method=RequestMethod.GET)
	public String read(int boardnum, Model model) {
		
		Board board = dao.getBoard(boardnum);
		if (board == null) {
			return "redirect:list";
		}
		
		
		ArrayList<Reply> replylist = dao.listReply(boardnum);
		
	
		model.addAttribute("board", board);
		model.addAttribute("replylist", replylist);
		
		return "boardjsp/boardRead";
	}
	
	
	@RequestMapping(value = "download", method = RequestMethod.GET)
	public String fileDownload(int boardnum, Model model, HttpServletResponse response) {
		Board board = dao.getBoard(boardnum);
		
		
		String originalfile = new String(board.getOriginalfile());
		try {
			response.setHeader("Content-Disposition", " attachment;filename="+ URLEncoder.encode(originalfile, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		
		String fullPath = uploadPath + "/" + board.getSavedfile();
		
	
		FileInputStream filein = null;
		ServletOutputStream fileout = null;
		
		try {
			filein = new FileInputStream(fullPath);
			fileout = response.getOutputStream();
			
			
			FileCopyUtils.copy(filein, fileout);
			
			filein.close();
			fileout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	
	@RequestMapping (value="delete", method=RequestMethod.GET)
	public String delete(HttpSession session, int boardnum) {
		String id = (String) session.getAttribute("loginId");
		
		
		Board board = new Board();
		board.setBoardnum(boardnum);
		board.setId(id);
		
	
		String savedfile = dao.getBoard(boardnum).getSavedfile();
		
	
		int result = dao.deleteBoard(board);
		
	
		if (result == 1 && savedfile != null) {
			FileService.deleteFile(uploadPath + "/" + savedfile);
		}
		
		return "redirect:list";
	}
	
	
	@RequestMapping (value="edit", method=RequestMethod.GET)
	public String editForm(HttpSession session, Model model, int boardnum) {
		
		Board board = dao.getBoard(boardnum);
		model.addAttribute("board", board);
		return "boardjsp/editForm";
	}
	
	
	@RequestMapping (value="edit", method=RequestMethod.POST)
	public String edit(
			HttpSession session
			, Board board
			, MultipartFile upload) {
		
		
		String id = (String) session.getAttribute("loginId");
		Board oldBoard = dao.getBoard(board.getBoardnum());
		if (oldBoard == null || !oldBoard.getId().equals(id)) {
			return "redirect:list";
		}
		
	
		board.setId(id);
		
		
		if (!upload.isEmpty()) {
			
			String savedfile = oldBoard.getSavedfile();
		
			if (savedfile != null) {
				FileService.deleteFile(uploadPath + "/" + savedfile);
			}
			
	
			savedfile = FileService.saveFile(upload, uploadPath);
			
		
			board.setOriginalfile(upload.getOriginalFilename());
			board.setSavedfile(savedfile);
		}
		
	
		dao.updateBoard(board);
	
		return "redirect:read?boardnum=" + board.getBoardnum();
	}
	
	
	@RequestMapping (value="replyWrite", method=RequestMethod.POST)
	public String replyWrite(
			Reply reply, 
			HttpSession session, 
			Model model) {
		
		
		String id = (String) session.getAttribute("loginId");
		reply.setId(id);
		
	
		dao.insertReply(reply);
		
		
		return "redirect:read?boardnum=" + reply.getBoardnum();
	}
	
	
	@RequestMapping (value="replyDelete", method=RequestMethod.GET)
	public String deleteReply(Reply reply, HttpSession session) {
		String id = (String) session.getAttribute("loginId");
		
	
		reply.setId(id);
		
		dao.deleteReply(reply);
		return "redirect:read?boardnum=" + reply.getBoardnum();
	}
	
	
	@RequestMapping (value="replyEdit", method=RequestMethod.POST)
	public String replyEdit(HttpSession session, Reply reply) {
		
		
		String id = (String) session.getAttribute("loginId");
		reply.setId(id);
		
		
		dao.updateReply(reply);
	
		return "redirect:read?boardnum=" + reply.getBoardnum();
	}
}
