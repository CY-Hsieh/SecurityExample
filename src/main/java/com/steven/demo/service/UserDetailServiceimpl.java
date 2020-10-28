package com.steven.demo.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.steven.demo.model.User;
import com.steven.demo.repository.UserRepository;

@Service
public class UserDetailServiceimpl implements UserDetailsService {
	
	@Autowired
	UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
		//從資料庫中載入使用者物件
		Optional<User> user = userRepository.findByUserName(s);
		//除錯用，如果值存在則輸出下使用者名稱與密碼
		user.ifPresent((value)->System.out.println("使用者名稱:" + value.getUserName() +" 使用者密碼：" + value.getPassword()));
		//若值不再則返回null
		return new UserDeatilsimpl(user.orElse(null));
	}

}
