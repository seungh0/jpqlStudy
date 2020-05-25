package app.jpa.jpql;

import app.jpa.jpql.domain.member.Member;
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
			Team team = new Team("team");
			entityManager.persist(team);

			Member member = new Member("team", 23, team);
			entityManager.persist(member);

			entityManager.flush();
			entityManager.clear();

			/**
			 * inner join
			 */
			String query1 = "select m from Member m inner join m.team t where t.name =  :teamName";
			List<Member> members1 = entityManager.createQuery(query1, Member.class)
					.setParameter("teamName", "team")
					.getResultList();

			members1.forEach(System.out::println);

			/**
			 * left outer join (left join)
			 */
			String query2 = "select m from Member m left outer join m.team t where t.name =  :teamName";
			List<Member> members2 = entityManager.createQuery(query2, Member.class)
					.setParameter("teamName", "team")
					.getResultList();

			members2.forEach(System.out::println);

			/**
			 * Setter Join
			 */
			String query3 = "select m from Member m left outer join m.team t where m.name = t.name";
			List<Member> members3 = entityManager.createQuery(query3, Member.class)
					.getResultList();

			members3.forEach(System.out::println);

			/**
			 * 조인 대상 필터링
			 */
			String query4 = "select m from Member m left join m.team t on t.name = :teamName";
			List<Member> members4 = entityManager.createQuery(query4, Member.class)
					.setParameter("teamName", "team")
					.getResultList();

			members4.forEach(System.out::println);

			/**
			 * 연관관계 없는 엔티티도 외부 조인이 가능하다 (JPA 2.1 이상)
			 */
			String query5 = "select m from Member m left join Team t on m.name = t.name";
			List<Member> members5 = entityManager.createQuery(query5, Member.class)
					.getResultList();

			members5.forEach(System.out::println);

			transaction.commit();
		} catch (Exception e) {
			entityManager.close();
			e.printStackTrace();
		} finally {
			entityManagerFactory.close();
		}
	}

}
