package app.jpa.jpql.domain.member;

import app.jpa.jpql.domain.team.Team;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Getter
@NoArgsConstructor
@Entity
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private int age;

	@ManyToOne
	@JoinColumn(name = "TEAM_ID")
	private Team team;

	public Member(String name, int age, Team team) {
		this.name = name;
		this.age = age;
		this.team = team;
	}


	public void changeAge(int age) {
		this.age = age;
	}

}
