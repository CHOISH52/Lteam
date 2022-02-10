package global.sesoc.library.dao;

import global.sesoc.library.vo.Member;

/**
 * 회원 관련 Mybatis 사용 메서드
 */
public interface MemberMapper {
	//회원 정보 저장
	public int insertMember(Member member);
	//회원 정보 검색
	public Member getMember(String id);
	//회원 정보 수정
	public int updateMember(Member member);
}
