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

			/**
			 * 페치 조인의 한계
			 * 1. 페치 조인 대상에는 별칭을 줄 수 없다.
			 * ex) select t From Team t join fetch t.members as m where m.name =....
			 * => 데이터 정합성 문제
			 *
			 * 2. 둘 이상의 컬렉션은 페치 조인할 수 없다. 1: n : n
			 * => 데이터 정합성 문제
			 *
			 * 3. 컬렉션을 페치 조인하면 페이징  API(setFirstResult, setMaxResults)를 사용할 수 없다.
			 */


			/**
			 * 3번문제 예시
			 */
			String query3 = "select t From Team t";
			List<Team> team1 = entityManager.createQuery(query3, Team.class)
					.setFirstResult(0)
					.setMaxResults(2)
					.getResultList();

			for (Team team : team1) {
				System.out.println(team.getName());
				System.out.println(team.getMembers().size());

				for (Member member : team.getMembers()) {
					System.out.println(member);
				}

				// Team 불러오는 쿼리 1
				// Team1의 Member 불러오는 쿼리 1
				// Team2의 Member 불러오는 쿼리 1
				// => 성능 안좋음
			}

			/**
			 * 해결방법 Batch Size 지정 (Team.members = @BatchSize(size = 100)
			 *
			 * select members where member.teamId in (1, 2)
			 */

			/**
			 * 정리
			 * 1. 연관된 엔티티들을 SQL 한번으로 조회 - 성능 최적화
			 * 2. 엔티티에 직접 적용하는 글로벌 로딩 전략보다 우선함
			 * @OntToMany(fetch = FetchType.LAZY) // 글로벌 로딩 전략 보다 fetch 조인을 진행.
			 * 3. 실무에서 글로벌 로딩 전략은 모두 지연로딩
			 * 4. 최적화가 필요한 곳은 페치 조인 적용 (N+1 문제가 발생하는 곳에 fetch Join 적용)
			 *
			 * 5. 모든 것을 페치 조인으로 해결 할 수 는 없음
			 * 6. 페치 조인은 객체 그래프를 유지할 때 사용하면 효과적
			 * 7. 여러 테이블을 조인해서 엔티티가 가진 모양이 아닌 전혀 다른 결과를 내야하면, 페치 조인보다는 일반 조인을
			 * 사용하고, 필요한 데이터들만 조회해서 DTO로 반환하는 것이 효과적.
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
