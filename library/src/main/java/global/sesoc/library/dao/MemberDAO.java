package global.sesoc.library.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import global.sesoc.library.vo.Member;


@Repository
public class MemberDAO {
	@Autowired
	SqlSession sqlSession;
	
	
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
