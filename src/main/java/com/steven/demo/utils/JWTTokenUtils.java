package com.steven.demo.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;

@Component
public class JWTTokenUtils {

	private final Logger log = LoggerFactory.getLogger(JWTTokenUtils.class);

	private static final String AUTHORITIES_KEY = "auth";
	private String secretKey; // 簽名金鑰
	private long tokenValidityInMilliseconds; // 失效日期
	private long tokenValidityInMillisecondsForRememberMe; // （記住我）失效日期

	@PostConstruct
	public void init() {
		this.secretKey = "Hakunamatata";
		int secondIn1day = 1000 * 60 * 60 * 24;
		this.tokenValidityInMilliseconds = secondIn1day * 2L;
		this.tokenValidityInMillisecondsForRememberMe = secondIn1day * 7L;
	}

//	private final static long EXPIRATIONTIME = 432_000_000;

	// 建立Token
	public String createToken(Authentication authentication, Boolean rememberMe) {
		String authorities = authentication.getAuthorities().stream() // 獲取使用者的許可權字串，如 USER,ADMIN
				.map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
		long now = (new Date()).getTime(); // 獲取當前時間戳
		Date validity; // 存放過期時間
		if (rememberMe) {
			validity = new Date(now + this.tokenValidityInMilliseconds);
		} else {
			validity = new Date(now + this.tokenValidityInMillisecondsForRememberMe);
		}
		return Jwts.builder() // 建立Token令牌
				.setSubject(authentication.getName()) // 設定面向使用者
				.claim(AUTHORITIES_KEY, authorities) // 新增許可權屬性
				.setExpiration(validity) // 設定失效時間
				.signWith(SignatureAlgorithm.HS512, secretKey) // 生成簽名
				.compact();
	}

	// 獲取使用者許可權
	public Authentication getAuthentication(String token) {
		System.out.println("token:" + token);
		
		Claims claims = Jwts.parser() // 解析Token的payload
				.setSigningKey(secretKey).parseClaimsJws(token).getBody();
		System.out.println("claims: " + claims);
		
		Collection<? extends GrantedAuthority> authorities = Arrays
				.stream(claims.get(AUTHORITIES_KEY).toString().split(","))// 獲取使用者許可權字串
				.map(SimpleGrantedAuthority::new).collect(Collectors.toList()); // 將元素轉換為GrantedAuthority介面集合
		System.out.println("authorities: " + authorities);
		
		User principal = new User(claims.getSubject(), "", authorities);
		System.out.println("principal: " + principal);
		
		return new UsernamePasswordAuthenticationToken(principal, "", authorities);
	}

	// 驗證Token是否正確
	public boolean validateToken(String token) {
		try {
			Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token); // 通過金鑰驗證Token
			return true;
		} catch (SignatureException e) { // 簽名異常
			log.info("Invalid JWT signature.");
			log.trace("Invalid JWT signature trace: {}", e);
		} catch (MalformedJwtException e) { // JWT格式錯誤
			log.info("Invalid JWT token.");
			log.trace("Invalid JWT token trace: {}", e);
		} catch (ExpiredJwtException e) { // JWT過期
			log.info("Expired JWT token.");
			log.trace("Expired JWT token trace: {}", e);
		} catch (UnsupportedJwtException e) { // 不支援該JWT
			log.info("Unsupported JWT token.");
			log.trace("Unsupported JWT token trace: {}", e);
		} catch (IllegalArgumentException e) { // 引數錯誤異常
			log.info("JWT token compact of handler are invalid.");
			log.trace("JWT token compact of handler are invalid trace: {}", e);
		}
		return false;
	}

}
