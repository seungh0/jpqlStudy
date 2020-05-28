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
			 * JPQL에서 엔티티를 직접 사용하면, SQL에서 해당 엔티티의 기본 키 값을 사용
			 *
			 * [JPQL]
			 * select count(m.ind) from Member m // 엔티티의 아이디를 사용
			 * select count(m) from Member m // 엔티티를 직접 사용
			 *
			 * [SQL]
			 * select count(m.id) as cnt from Member m
			 */

			String query = "select m From Member m where m = :member";
			Member findMember = entityManager.createQuery(query, Member.class)
					.setParameter("member", member1)
					.getSingleResult();

			System.out.println(findMember);
			// 식별자(id)를 비교

			String query1 = "select m From Member m where m.id = :memberId";
			Member findMember1 = entityManager.createQuery(query1, Member.class)
					.setParameter("memberId", member1.getId())
					.getSingleResult();

			System.out.println(findMember1);
			// 이 역시 당연히 식별자(id)로 비교)

			/**
			 * 엔티티 직접 사용 - FK
			 */
			String query2 = "select m from Member m where m.team = :team";
			// select m.* from Member m wheer m.team_id = ?
			List<Member> memberList = entityManager.createQuery(query2, Member.class)
					.setParameter("team", teamA)
					.getResultList();

			memberList.forEach(System.out::println);

			transaction.commit();
		} catch (Exception e) {
			entityManager.close();
			e.printStackTrace();
		} finally {
			entityManagerFactory.close();
		}
	}

}
