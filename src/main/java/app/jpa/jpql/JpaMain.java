package app.jpa.jpql;

import app.jpa.jpql.domain.member.Member;
import app.jpa.jpql.domain.member.MemberType;
import app.jpa.jpql.domain.team.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.Collection;
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

			Member member = new Member("member1", 23, team, MemberType.ADMIN);
			entityManager.persist(member);

			Member member2 = new Member("member2", 25, team, MemberType.ADMIN);
			entityManager.persist(member2);

			entityManager.flush();
			entityManager.clear();

			/**
			 * 상태 필드(state filed): 경로 탐색의 끝, 탐색X
			 */
			String query = "select m.name From Member m";

			List<String> result = entityManager.createQuery(query, String.class)
					.getResultList();
			result.forEach(System.out::println);

			/**
			 * 단일 값 연관 경로 : 묵시적 내부 조인(inner join) 발생,  탑색 O
			 */
			String query1 = "select m.team.name From Member m"; // 묵시적 내부 조인 => 쿼리 튜닝이 어려움 => 최대한 안쓰도록.
			List<String> result1 = entityManager.createQuery(query1, String.class)
					.getResultList();
			result1.forEach(System.out::println);

			/**
			 * 컬렉션 값 연관 경로: 묵시적 내부 조인 발생, 탐색X
			 */
			String query2 = "select t.members From Team t";
			List<Collection> result2 = entityManager.createQuery(query2, Collection.class)
					.getResultList();
			System.out.println(result2);

			String query3 = "select m.name From Team t join t.members m";
			List<String> result3 = entityManager.createQuery(query3, String.class)
					.getResultList();
			result3.forEach(System.out::println);

			/**
			 * 1. 명시적 조인 : join 키워드를 직접 사용
			 * select m from Member m join m.team t
			 *
			 * 2. 묵시적 조인: 경로 표현식에 의해 묵시적으로 SQl 조인 발생(내부 조인만 가능)
			 * select m.team from Member m
			 *
			 * 결과적으로 묵시적 내부 조인을 쓰지 말자!! (쿼리 튜닝도 어렵고.. 보기도 힘듬)
			 * => 명시적 내부 조인을 사용합시다.
			 */

			transaction.commit();
		} catch (Exception e) {
			entityManager.close();
			e.printStackTrace();
		} finally {
			entityManagerFactory.close();
		}
	}

}
