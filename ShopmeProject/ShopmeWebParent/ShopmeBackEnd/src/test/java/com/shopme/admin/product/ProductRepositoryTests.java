package com.shopme.admin.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.product.Product;


@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ProductRepositoryTests {
	
	@Autowired
	private ProductRepository repo;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateProduct() {
		Brand brand = entityManager.find(Brand.class, 1);
		Category category = entityManager.find(Category.class, 6);
		
		Product product = new Product();
		product.setName("Acer Asprire Desktop");
		product.setAlias("acer_aspire_desktop");
		product.setShortDescription("Short Description for Acer laptops");
		product.setFullDescription("Full Description for Acer laptops");
		
		product.setBrand(brand);
		product.setCategory(category);
		product.setEnabled(true);
		product.setInStock(true);
		product.setPrice(910);
		product.setCost(600);
		product.setCreatedTime(new Date());
		product.setUpdatedTime(new Date());
		
		Product savedProduct = repo.save(product);
		
		assertThat(savedProduct).isNotNull();
		assertThat(savedProduct.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testListAllProduct() {
		Iterable<Product> iterableProducts = repo.findAll();
		
		iterableProducts.forEach(System.out::println);
	}
	
	@Test
	public void testGetProduct() {
		Integer id = 2;
		Product product = repo.findById(id).get();
		System.out.println(product);
		
		assertThat(product).isNotNull();
	}
	
	@Test
	public void testUpdateProduct() {
		Integer id = 1;
		Product product = repo.findById(id).get();
		product.setPrice(500);
		
		repo.save(product);
		
		Product updatedProduct = entityManager.find(Product.class, id);
		
		assertThat(updatedProduct.getPrice()).isEqualTo(500);
	}
	
	@Test
	public void testDeleteProduct() {
		Integer id = 3;
		repo.deleteById(id);
		
		Optional<Product> result = repo.findById(id);
		
		assertThat(!result.isPresent());
	}
	
	@Test
	public void testSaveProductWithImages() {
		Integer productId = 1;
		Product product = repo.findById(productId).get();
		
		product.setMainImage("main2 image.jpg");
		product.addExtraImage("extra image 4.png");
		product.addExtraImage("extra image_2.png");
		product.addExtraImage("extra image6.png");
		
		Product savedProduct = repo.save(product);
		
		assertThat(savedProduct.getImages().size()).isEqualTo(12);
	}
	
	@Test
	public void testSaveProductWithDetails() {
		Integer productId = 2;
		Product product = repo.findById(productId).get();
		
		product.addDetail("Device memory", "256 GB");
		product.addDetail("CPU ", "Intel");
		product.addDetail("OS", "Hyper ÓS");
		
		Product savedProduct = repo.save(product);
		assertThat(savedProduct.getDetails()).isNotEmpty();
	}
}
