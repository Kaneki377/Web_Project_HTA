package com.shopme.admin.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shopme.admin.paging.SearchRepository;
import com.shopme.common.entity.User;

public interface UserRepository extends SearchRepository<User, Integer>{
	@Query("SELECT u FROm User u WHERE u.email = :email")
	public User getUserByemail(@Param("email") String email);

	//Method đếm xem record có trong DB hay kh
	public Long countById(Integer id);

	//Method biểu thị trạng thái enable
	@Query("UPDATE User u SET u.enabled = ?2 WHERE u.id = ?1")
	@Modifying
	public void updateEnabledStatus(Integer id, boolean enabled);

	@Query("SELECT u FROM User u WHERE CONCAT(u.id, ' ', u.email, ' ', u.firstName, ' '," +
			" u.lastName) LIKE %?1%")
	public Page<User> findAll(String keyword, Pageable pageable);
}
