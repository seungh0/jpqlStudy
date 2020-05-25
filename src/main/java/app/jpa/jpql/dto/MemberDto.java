package app.jpa.jpql.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberDto {

	private String name;

	private int age;

	public MemberDto(String name, int age) {
		this.name = name;
		this.age = age;
	}
	
}
