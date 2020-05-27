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

			Member member1 = new Member(null, 23, team, MemberType.ADMIN);
			entityManager.persist(member1);

			Member member2 = new Member("관리자", 23, team, MemberType.ADMIN);
			entityManager.persist(member2);

			entityManager.flush();
			entityManager.clear();

			String query = "select " +
					"case when m.age <= 10 then '학생요금'" +
					"	  when m.age >= 60 then '경로요금'" +
					"	  else '일반요금'" +
					"end " +
					"from Member m";
			List<String> result = entityManager.createQuery(query, String.class)
					.getResultList();

			for (String s : result) {
				System.out.println(s);
			}

			String query1 = "select coalesce(m.name, '이름 없는 회원') as username from Member m";
			List<String> result1 = entityManager.createQuery(query1, String.class)
					.getResultList();

			for (String s : result1) {
				System.out.println(s);
			}

			String query2 = "select nullif(m.name, '관리자') from Member m";
			List<String> result2 = entityManager.createQuery(query2, String.class)
					.getResultList();

			for (String s : result2) {
				System.out.println(s);
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
