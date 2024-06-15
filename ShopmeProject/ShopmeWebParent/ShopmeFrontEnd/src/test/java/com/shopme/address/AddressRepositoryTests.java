package com.shopme.address;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Address;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class AddressRepositoryTests {
	@Autowired 
	private AddressRepository repo;

	@Test
	public void testAddNew() {
		Integer customerId = 10;
		Integer countryId = 4; // USA

		Address newAddress = new Address();
		newAddress.setCustomer(new Customer(customerId));
		newAddress.setCountry(new Country(countryId));
		newAddress.setFirstName("Hieu");
		newAddress.setLastName("Nguyen");
		newAddress.setPhoneNumber("646-232-3902");
		newAddress.setAddressLine1("Vinhome GrandPark");
		newAddress.setCity("Ho Chi Minh City");
		newAddress.setState("District 9");
		newAddress.setPostalCode("300475");

		Address savedAddress = repo.save(newAddress);

		assertThat(savedAddress).isNotNull();
		assertThat(savedAddress.getId()).isGreaterThan(0);
	}

	@Test
	public void testFindByCustomer() {
		Integer customerId = 10;
		List<Address> listAddresses = repo.findByCustomer(new Customer(customerId));
		assertThat(listAddresses.size()).isGreaterThan(0);

		listAddresses.forEach(System.out::println);
	}

	@Test
	public void testFindByIdAndCustomer() {
		Integer addressId = 1;
		Integer customerId = 10;

		Address address = repo.findByIdAndCustomer(addressId, customerId);

		assertThat(address).isNotNull();
		System.out.println(address);
	}

	@Test
	public void testUpdate() {
		Integer addressId = 2;
		

		Address address = repo.findById(addressId).get();
		address.setDefaultForShipping(true);

		Address updatedAddress = repo.save(address);
		//assertThat(updatedAddress.getPhoneNumber()).isEqualTo(phoneNumber);
	}

	@Test
	public void testDeleteByIdAndCustomer() {
		Integer addressId = 1;
		Integer customerId = 10;

		repo.deleteByIdAndCustomer(addressId, customerId);

		Address address = repo.findByIdAndCustomer(addressId, customerId);
		assertThat(address).isNull();
	}
	
	@Test
	public void testSetDefault() {
		Integer addressId = 4;
		repo.setDefaultAddress(addressId);
		
		Address address = repo.findById(addressId).get();
		assertThat(address.isDefaultForShipping()).isTrue();	
	}

	@Test
	public void testSetNonDefault() {
		Integer addressId = 4;
		Integer customerId = 10;
		
		repo.setNonDefaultForOthers(addressId, customerId);
	}
	
	@Test
	public void testGetDefault() {
		Integer customerId = 10;
		Address address = repo.findDefaultByCustomer(customerId);
		assertThat(address).isNotNull();
		System.out.println(address);
	}
}
