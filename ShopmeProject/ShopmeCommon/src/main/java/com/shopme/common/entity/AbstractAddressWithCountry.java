package com.shopme.common.entity;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractAddressWithCountry extends AbstractAddress {

	@ManyToOne
	@JoinColumn(name = "country_id")
	private Country country;
	
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
	
	@Override
	public String toString() {
		String address = firstName;
		
		if(lastName != null && !lastName.isEmpty()) {
			address += " " + lastName;
		}
		
		if(!addressLine1.isEmpty()) address += ": " + addressLine1;
		
		if(addressLine2 != null && !addressLine2.isEmpty()) address += ", " + addressLine2;
		 
		if(!city.isEmpty()) address += ", " + city;
		
		if(state != null && !state.isEmpty()) address += ", " + state;
		
		address += ", " + country.getName();
		
		if(!postalCode.isEmpty()) address += ", Postal Code: " + postalCode;
		if(!phoneNumber.isEmpty()) address += ", Phone Number: " + phoneNumber;
		
		return address;
	}
	
}
