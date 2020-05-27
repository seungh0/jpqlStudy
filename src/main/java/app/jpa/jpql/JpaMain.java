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
			Team team = new Team("team");
			entityManager.persist(team);

			Member member = new Member("team", 23, team, MemberType.ADMIN);
			entityManager.persist(member);

			entityManager.flush();
			entityManager.clear();

			String query = "select m.name, 'Hello', TRUE from Member m where m.type = :type";
			List<Object[]> result = entityManager.createQuery(query)
					.setParameter("type", MemberType.ADMIN)
					.getResultList();

			for (Object[] objects : result) {
				System.out.println(objects[0]);
				System.out.println(objects[1]);
				System.out.println(objects[2]);
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
