package com.steven.demo.model;



import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "user")
public class User {
	
	@Id
	@GeneratedValue
	@Column(name = "uid")
	private Integer id;
	
	@Column(name = "username")
	private String userName;
	
	@Column(name = "password")
	private String password;
	
	@ManyToMany(cascade = {CascadeType.REFRESH}, fetch = FetchType.EAGER)
	@JoinTable(name = "user_role", 
	joinColumns = {@JoinColumn(name = "uid", referencedColumnName = "uid")}, 
	inverseJoinColumns = {@JoinColumn(name = "rid", referencedColumnName = "rid")})
	private List<Role> roles;

	public User() {
		super();
	}

	public User(Integer id, String userName, String password, List<Role> roles) {
		super();
		this.id = id;
		this.userName = userName;
		this.password = password;
		this.roles = roles;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
	
}
