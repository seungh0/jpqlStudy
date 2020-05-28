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
			 * 벌크 연산
			 *
			 * 재고가 10개 미만인 모든 상품의 가격을 10% 상승하려면?
			 * - JPA 변경 감지 기능으로 실행하려면 너무 많은 SQL 실행
			 * 1. 재고가 10개 미만인 상품을 리스트로 조회한다.
			 * 2. 상품 엔티티의 가격을 10% 증가한다.
			 * 3. 트랜잭션 커밋 시점에 변경감지가 동작한다
			 *
			 * => 변경된 데이터가 100개라면 100번의 UPDATE SQL이 실행된다.
			 *
			 * UPDATE, DELETE 모두 지원
			 */

			// FLUSH 자동 호출 (JPQL 호출시 flush() 자동)
			int resultCounts = entityManager.createQuery("update Member m set m.age = 20")
					.executeUpdate();
			System.out.println(resultCounts);

			/**
			 * 벌크 연산 주의
			 *
			 * 벌크 연산은 영속성 컨텍스트를 무시하고 데이터베이스에 직접 쿼리
			 * 방법 1. 벌크 연산을 먼저 실행
			 * 방법 2. 벌크 연산 수행 후 영속성 컨텍스트 초기화 !**
			 */

			Member findMember = entityManager.find(Member.class, member1.getId());
			System.out.println(findMember.getAge()); //  23

			/**
			 * 해결 방법
			 */
			entityManager.clear();
			Member findMember1 = entityManager.find(Member.class, member1.getId());
			System.out.println(findMember1.getAge()); //  20

			transaction.commit();
		} catch (Exception e) {
			entityManager.close();
			e.printStackTrace();
		} finally {
			entityManagerFactory.close();
		}
	}

}
