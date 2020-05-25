package app.jpa.jpql.domain;

import app.jpa.jpql.domain.member.Member;
import org.apache.tomcat.jni.Mmap;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
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
			Member member = new Member("name123", 23, null);
			entityManager.persist(member);

			/**
			 * 반환 타입이 명확할 떄: TypeQuery
			 * 반환 타입이 명확하지 않을 때 : Query
			 */
			TypedQuery<Member> typedQuery = entityManager.createQuery("select m From Member m", Member.class);
			Query query = entityManager.createQuery("select m.name, m.age from Member m");

			/**
			 * query,getResultList(): 결과가 하나 이상일때, 리스트 반환, 결과가 없으면 빈 리스트 반환
			 * query.getSingleResult(): 결과가 정확히 하나여야 한다.
			 *  - 결과가 없으면: NoResultException
			 *  - 결과가 둘 이상이면: NonUniqueResultException 발생
			 */
			List<Member> members = typedQuery.getResultList();
			Member member1 = typedQuery.getSingleResult();

			TypedQuery<Member> query1 = entityManager.createQuery("select m From Member m where m.name = :name", Member.class);
			query1.setParameter("name", "name123");
			Member findMember = query1.getSingleResult();
			System.out.println(findMember.getName());

			/**
			 * 보통 아래처럼 체이닝해서 사용함
			 */
			Member findMember2 = entityManager.createQuery("select m From Member m where m.name = :name", Member.class)
					.setParameter("name", "name123")
					.getSingleResult();
			System.out.println(findMember2.getName());

			transaction.commit();
		} catch (Exception e) {
			entityManager.close();
			e.printStackTrace();
		} finally {
			entityManagerFactory.close();
		}
	}

}
