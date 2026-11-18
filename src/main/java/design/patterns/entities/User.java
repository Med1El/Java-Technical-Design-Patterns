package design.patterns.entities;

import java.util.Map;

import design.patterns.dal.Persistable;

public class User implements Persistable {

	@Override
	public String tableName() {
		return "users";
	}

	@Override
	public String idColumn() {
		return "user_id";
	}
	
	@Override
	public Map<String, String> columns() {
		return Map.of(
				"userId", "user_id",
				"username", "username",
				"email", "email",
				"firstName", "first_name",
				"lastName", "last_name",
				"password", "password",
				"phoneNumber", "phone_number",
				"address", "address"
		);
	}
	
	public Long userId;
	public String username;
	public String email;
	public String firstName;
	public String lastName;
	public String password;
	public String phoneNumber;
	public String address;
	
	public User() {}

	public User(String username, String email, String firstName, String lastName, String password, String phoneNumber,
			String address) {
		super();
		this.username = username;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.password = password;
		this.phoneNumber = phoneNumber;
		this.address = address;
	}

	public Long getUserId() {
		return userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	
		
	

}
