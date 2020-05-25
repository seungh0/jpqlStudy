package app.jpa.jpql.domain.product;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@NoArgsConstructor
@Entity
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private int price;

	private int stockAmount;

	public Product(String name, int price, int stockAmount) {
		this.name = name;
		this.price = price;
		this.stockAmount = stockAmount;
	}
	
}
