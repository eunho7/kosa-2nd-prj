package com.example._team.dto;

import com.example._team.domain.enums.Authority;
import lombok.Getter;
import lombok.Setter;

public class AddUserRequest {
	
	private String password;
    private String email;
    private String phone;
    private String nickname;
    private Integer status;
    private Authority authority;

    @Override
	public String toString() {
		return "AddUserRequest [password=" + password + ", email=" + email + ", phone=" + phone + ", nickname="
				+ nickname + ", status=" + status + ", authority=" + authority + "]";
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Authority getAuthority() {
		return authority;
	}
	public void setAuthority(Authority authority) {
		this.authority = authority;
	}
}
