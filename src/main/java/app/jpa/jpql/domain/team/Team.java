package app.jpa.jpql.domain.team;

import app.jpa.jpql.domain.member.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Team {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	@BatchSize(size = 100) // 방법 1
	@OneToMany(mappedBy = "team")
	private List<Member> members = new ArrayList<>();

	public Team(String name) {
		this.name = name;
	}

}
