package com.shopme.admin.user;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;


@Service
@Transactional
public class UserService {

	public static final int USERS_PER_PAGE = 4;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private RoleRepository roleRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public User getByEmail(String email) {
		return userRepo.getUserByemail(email);
	}

	//Method hiển thị danh sách User
	public List<User> listAll(){
		return userRepo.findAll(Sort.by("firstName").ascending());
	}

	//Method cho phân trang-paging
	public void listByPage(int pageNum, PagingAndSortingHelper helper){
		helper.listEntities(pageNum, USERS_PER_PAGE, userRepo);
	}

	public List<Role> listRoles(){
		return (List<Role>) roleRepo.findAll();
	}
	//Method lưu user và mã hõa mật khẩu của user trước khi lưu vào db
	public User save(User user) {
		boolean isUpdatingUser = (user.getId() != null);

		if(isUpdatingUser) {
			User existingUser = userRepo.findById(user.getId()).get();
			if(user.getPassword().isEmpty()) {
				user.setPassword(existingUser.getPassword());
			}else {
				encodePassWord(user);
			}

		}else {
			encodePassWord(user);
		}

		return userRepo.save(user);
	}
	//Method mã hóa mật khẩu
	private void encodePassWord(User user) {
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
	}

	//Method check trùng email
	public boolean isEmailUnique(Integer id, String email) {
		User userByEmail = userRepo.getUserByemail(email);

		if(userByEmail == null) {
			return true;
		}

		boolean isCreatingNew = (id == null);

		if(isCreatingNew) {
			if(userByEmail != null) {
				return false;
			}
		}else {
			if(userByEmail.getId() != id) {
				return false;
			}
		}

		return true;
	}

	public User get(Integer id) throws UserNotFoundException {
		try {


			return userRepo.findById(id).get();
		}catch(NoSuchElementException ex) {
			throw new UserNotFoundException("Cound not find any user with ID " + id);
		}
	}

	//Method xóa user khỏi db
	public void delete(Integer id) throws UserNotFoundException{
		Long countById = userRepo.countById(id);
		if(countById == null || countById == 0) {
			throw new UserNotFoundException("Could not find any user with ID " + id);
		}
		//Method delete có sẵn
		userRepo.deleteById(id);
	}

	//Method update enable status
	public void updateUserEnabledStatus(Integer id, boolean enabled) {
		userRepo.updateEnabledStatus(id, enabled);
	}

	//method update cho view account_form
	public User updateAccount(User userInForm) {
		User userInDB = userRepo.findById(userInForm.getId()).get();

		if(!userInForm.getPassword().isEmpty()) {
			userInDB.setPassword(userInForm.getPassword());
			encodePassWord(userInDB);
		}
		if(userInForm.getPhotos() != null) {
			userInDB.setPhotos(userInForm.getPhotos());
		}
		userInDB.setFirstName(userInDB.getFirstName());
		userInDB.setLastName(userInForm.getLastName());

		return userRepo.save(userInDB);
	}
}
