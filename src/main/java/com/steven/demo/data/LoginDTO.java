package com.steven.demo.data;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class LoginDTO implements Serializable {

	private static final long serialVersionUID = -71636547610092919L;
	
	@NotBlank(message = "使用者名稱不能為空")
	private String userName;
	
	@NotBlank(message = "密碼不能為空")
	private String password;

}
