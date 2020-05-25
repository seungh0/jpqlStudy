package app.jpa.jpql.domain.order;

import app.jpa.jpql.domain.common.Address;
import app.jpa.jpql.domain.product.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "ORDERS")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private int orderAmount;

	@Embedded
	private Address address;

	@ManyToOne
	@JoinColumn(name = "PRODUCT_ID")
	private Product product;

}
