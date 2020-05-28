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

			String query1 = "select m from Member m";
			List<Member> members1 = entityManager.createQuery(query1, Member.class)
					.getResultList();

			for (Member m : members1) {
				System.out.println(m.getName());
				System.out.println(m.getTeam().getName()); // 여기서 Team은 프록시 객체
			}

			// 회원1, 팀A(SQL),
			// 회원2, 팀A(1차 캐시)
			// 회원3, 팀B(SQL)

			/**
			 * 회원 100명 -> 100개의 쿼리 (N + 1 문제)
			 */

			/**
			 * Fetch Join
			 *
			 * SQL 조인 종류 X.
			 * JPQL에서 성능 최적화를 위해 제공하는 기능.
			 * 연관된 엔티티나 컬렉션을 SQL 한 번에 함께 조회하는 기능.
			 */

			/**
			 * SELECT M.*, T.* FROM MEMBER M
			 * INNER JOIN TEAM T ON M.TEAM_ID = T.ID
			 */
			String query = "select m from Member m join fetch m.team";
			List<Member> members = entityManager.createQuery(query, Member.class)
					.getResultList();

			for (Member m : members) {
				System.out.println(m.getName());
				System.out.println(m.getTeam().getName()); // 여기서 Team은 프록시 객체가 아님.
			}

			/**
			 * 컬렉션 Fetch Join
			 * 일대다 join일 경우, distinct 필요.
			 *
			 * JPQL의 DISTINCT 2가지 기능 제공
			 * 1. SQL에 DISTINCT를 추가
			 * 2. 애플리케이션에서 엔티티 중복 제거
			 */
			String query2 = "select distinct t From Team t join fetch t.members";
			List<Team> teams = entityManager.createQuery(query2, Team.class)
					.getResultList();
			for (Team team : teams) {
				System.out.println(team.getName());
				for (Member member : team.getMembers()) {
					System.out.println(member);
				}
			}

			/**
			 * 일반 조인과 페치 조인의 차이
			 *
			 * 1. 일반 조인 실행시 연관된 엔티티를 함께 조회하지 않음.
			 * 2. 페치 조인을 사용할 때만 연관된 엔티티도 함께 조회(즉시 로딩)
			 *  = 페치 조인은 객체 그래프르 SQL 한번에 조회하는 개념
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
