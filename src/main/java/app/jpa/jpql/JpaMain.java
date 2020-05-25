package app.jpa.jpql;

import app.jpa.jpql.domain.common.Address;
import app.jpa.jpql.domain.member.Member;
import app.jpa.jpql.domain.team.Team;
import app.jpa.jpql.dto.MemberDto;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

/**
 * JPQL
 * 엔티티 이름 사용, 테이블 이름이 아님(Member)
 */

public class JpaMain {

	public static void main(String[] args) {
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hello");
		EntityManager entityManager = entityManagerFactory.createEntityManager();

		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();

		try {
			Member member = new Member("name123", 23, null);
			entityManager.persist(member);

			entityManager.flush();
			entityManager.clear();

			/**
			 * 엔티티 프로젝션
			 * findMembers는 영속성 컨텍스트에서 관리가 될까?
			 * => yes
			 */
			List<Member> findMembers = entityManager.createQuery("select m from Member m", Member.class)
					.getResultList();

			Member findMember = findMembers.get(0);
			findMember.changeAge(20); // 변경이 된다 (Update 쿼리가 나감)

			/**
			 * 엔티티 프로젝션
			 */
			// select m.team from Member // 묵시적 조인
			List<Team> findTeams = entityManager.createQuery("select t from Member m join m.team t", Team.class)
					.getResultList(); // 명시적 조인

			/**
			 * 임베디드 타입 프로젝션
			 */
			List<Address> addresses = entityManager.createQuery("select o.address from Order o", Address.class)
					.getResultList();

			/**
			 * 스칼라 타입 프로젝션
			 *
			 * 1. Query타입으로 조회
			 * 2. Object[] 타입으로 조회
			 * 3. Dto 타입으로 조회
			 */
			List<MemberDto> memberDtos = entityManager.createQuery("select new app.jpa.jpql.dto.MemberDto(m.name, m.age) from Member m", MemberDto.class)
					.getResultList();
			for (MemberDto memberDto : memberDtos) {
				System.out.println(memberDto.getName());
				System.out.println(memberDto.getAge());
			}

			transaction.commit();
		} catch (Exception e) {
			entityManager.close();
			e.printStackTrace();
		} finally {
			entityManagerFactory.close();
		}
	}

}
