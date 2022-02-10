package global.sesoc.library.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import global.sesoc.library.vo.Member;

/**
 * 회원 관련 DAO
 */
@Repository
public class MemberDAO {
	@Autowired
	SqlSession sqlSession;
	
	/**
	 * 회원 가입 처리
	 * @param member 사용자가 입력한 가입 정보
	 * @return 저장된 행 개수
	 */
	public int insert(Member member) {
		MemberMapper mapper = sqlSession.getMapper(MemberMapper.class);
		int result = 0;

		try {
			result = mapper.insertMember(member);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 회원 정보 검색
	 * @param id 검색할 회원 ID
	 * @return 검색결과를 담은 Member 객체
	 */
	public Member getMember(String id) {
		MemberMapper mapper = sqlSession.getMapper(MemberMapper.class);
		Member member = null;

		try {
			member = mapper.getMember(id);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return member;
	}
	
	/**
	 * 회원 정보 수정
	 * @param member 사용자가 입력한 수정 정보
	 * @return 수정된 행 개수
	 */
	public int updateMember(Member member) {
		MemberMapper mapper = sqlSession.getMapper(MemberMapper.class);
		int result = 0;

		try {
			result = mapper.updateMember(member);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
