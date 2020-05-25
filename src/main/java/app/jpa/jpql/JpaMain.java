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
			for (int i = 0; i < 30; i++) {
				Member member1 = new Member("name", i, null);
				entityManager.persist(member1);
			}

			entityManager.flush();
			entityManager.clear();

			List<Member> members = entityManager.createQuery("select m from Member m order by m.age desc", Member.class)
					.setFirstResult(0)
					.setMaxResults(10)
					.getResultList();

			System.out.println("size : " + members.size());
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
