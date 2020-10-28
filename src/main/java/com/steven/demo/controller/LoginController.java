package com.steven.demo.controller;

import java.util.Objects;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.steven.demo.config.WebSecurityConfig;
import com.steven.demo.data.LoginDTO;
import com.steven.demo.model.User;
import com.steven.demo.repository.UserRepository;
import com.steven.demo.utils.JWTTokenUtils;

@RestController
public class LoginController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JWTTokenUtils jwtTokenUtils;

	@RequestMapping(value = "/auth/login", method = RequestMethod.POST)
	public String login(@Valid LoginDTO loginDTO, HttpServletResponse httpResponse) throws Exception {
		// 通過使用者名稱和密碼建立一個 Authentication 認證物件，實現類為 UsernamePasswordAuthenticationToken
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
				loginDTO.getUserName(), loginDTO.getPassword());
		// 如果認證物件不為空
		if (Objects.nonNull(authenticationToken)) {
			User user = userRepository.findByUserName(authenticationToken.getPrincipal().toString())
						.orElseThrow(() -> new Exception("使用者不存在"));
			System.out.println("login user is: " + user);
		}
		try {
			// 通過 AuthenticationManager（預設實現為ProviderManager）的authenticate方法驗證
			// Authentication 物件
			Authentication authentication = authenticationManager.authenticate(authenticationToken);
			// 將 Authentication 繫結到 SecurityContext
			SecurityContextHolder.getContext().setAuthentication(authentication);
			// 生成Token
			String token = jwtTokenUtils.createToken(authentication, false);
			// 將Token寫入到Http頭部
			httpResponse.addHeader(WebSecurityConfig.AUTHORIZATION_HEADER, "Bearer " + token);
			return "Bearer " + token;
		} catch (BadCredentialsException authentication) {
			throw new Exception("密碼錯誤");
		}
	}

}
