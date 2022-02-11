package global.sesoc.library.dao;

import global.sesoc.library.vo.Member;


public interface MemberMapper {
	
	public int insertMember(Member member);
	
	public Member getMember(String id);

	public int updateMember(Member member);
}
