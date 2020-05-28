package app.jpa.jpql;

import app.jpa.jpql.domain.member.Member;
import app.jpa.jpql.domain.member.MemberType;
import app.jpa.jpql.domain.team.Team;

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
			Team teamA = new Team("teamA");
			entityManager.persist(teamA);

			Team teamB = new Team("teamB");
			entityManager.persist(teamB);

			Member member1 = new Member("member1", 23, teamA, MemberType.ADMIN);
			entityManager.persist(member1);

			Member member2 = new Member("member2", 25, teamA, MemberType.ADMIN);
			entityManager.persist(member2);

			Member member3 = new Member("member3", 27, teamB, MemberType.USER);
			entityManager.persist(member3);

			entityManager.flush();
			entityManager.clear();

			/**
			 * Named 쿼리 - 정적 쿼리
			 *
			 * - 미래 정의해서 이름을 부여해두고 사용하는 JPQL
			 * - 정적 쿼리
			 * - 어노테이션, XML에 정의
			 * - 애플리케이션 로딩 시점에 초기화 후 재사용!!!
			 * - 애플리케이션 로딩 시점에 쿼리를 검증!!!
			 */

			List<Member> members = entityManager.createNamedQuery("Member.findByName", Member.class)
					.setParameter("name", "member2")
					.getResultList();

			members.forEach(System.out::println);

			transaction.commit();
		} catch (Exception e) {
			entityManager.close();
			e.printStackTrace();
		} finally {
			entityManagerFactory.close();
		}
	}

}
