package app.jpa.jpql.domain.member;

import app.jpa.jpql.domain.team.Team;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;

@Getter
@NamedQuery(
		name = "Member.findByName",
		query = "select m from Member m where m.name = : name"
)
// 실행 시점에 쿼리를 검증함! (매우 좋음!)
// Spring Data JPA에서 @Query의 내부적으로 JPA의 NamedQuery가 사용된다.
// @Query("select m from Member m where m.name =: name")
// Member findByName(String name);
@NoArgsConstructor
@Entity
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private int age;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TEAM_ID")
	private Team team;

	@Enumerated(EnumType.STRING)
	private MemberType type;

	public Member(String name, int age, Team team, MemberType type) {
		this.name = name;
		this.age = age;
		this.team = team;
		this.type = type;
	}

	@Override
	public String toString() {
		return "Member{" +
				"id=" + id +
				", name='" + name + '\'' +
				", age=" + age +
				", type=" + type +
				'}';
	}

}
