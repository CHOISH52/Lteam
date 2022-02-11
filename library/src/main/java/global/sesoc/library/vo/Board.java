package global.sesoc.library.vo;


public class Board {
	private int boardnum;				
	private String id;					
	private String title;			
	private String contents;			
	private String inputdate;			
	private int hits;					
	private String originalfile;		
	private String savedfile;			
	 
	public Board() {
	}
	
	public int getBoardnum() {
		return boardnum;
	}
	public void setBoardnum(int boardnum) {
		this.boardnum = boardnum;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContents() {
		return contents;
	}
	public void setContents(String contents) {
		this.contents = contents;
	}
	public String getInputdate() {
		return inputdate;
	}
	public void setInputdate(String inputdate) {
		this.inputdate = inputdate;
	}
	public int getHits() {
		return hits;
	}
	public void setHits(int hits) {
		this.hits = hits;
	}
	public String getOriginalfile() {
		return originalfile;
	}
	public void setOriginalfile(String originalfile) {
		this.originalfile = originalfile;
	}
	public String getSavedfile() {
		return savedfile;
	}
	public void setSavedfile(String savedfile) {
		this.savedfile = savedfile;
	}
	
	@Override
	public String toString() {
		return "Board [boardnum=" + boardnum + ", id=" + id + ", title=" + title + ", contents=" + contents
				+ ", inputdate=" + inputdate + ", hits=" + hits + ", originalfile=" + originalfile + ", savedfile="
				+ savedfile + "]";
	}
}
