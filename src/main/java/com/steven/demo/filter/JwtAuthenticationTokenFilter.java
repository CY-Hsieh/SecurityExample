package com.steven.demo.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;

import com.steven.demo.config.WebSecurityConfig;
import com.steven.demo.utils.JWTTokenUtils;

import io.jsonwebtoken.ExpiredJwtException;


public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

	private final Logger log = LoggerFactory.getLogger(JwtAuthenticationTokenFilter.class);

	@Autowired
	private JWTTokenUtils tokenProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest servletRequest, HttpServletResponse servletResponse, FilterChain filterChain)
			throws ServletException, IOException {
		log.info("JwtAuthenticationTokenFilter");
		try {
			String jwt = resolveToken(servletRequest);
			if (StringUtils.hasText(jwt) && this.tokenProvider.validateToken(jwt)) { // 驗證JWT是否正確
				
				Authentication authentication = this.tokenProvider.getAuthentication(jwt); // 獲取使用者認證資訊
				System.out.println("doFilterInternal() authentication: " + authentication);
				
				SecurityContextHolder.getContext().setAuthentication(authentication); // 將使用者儲存到SecurityContext
				System.out.println("doFilterInternal() Security Context Authentication: " + ToStringBuilder.reflectionToString(SecurityContextHolder.getContext().getAuthentication()));
			}
			filterChain.doFilter(servletRequest, servletResponse);
		} catch (ExpiredJwtException e) { // JWT失效
			log.info("Security exception for user {} - {}", e.getClaims().getSubject(), e.getMessage());
			log.trace("Security exception trace: {}", e);
			((HttpServletResponse) servletResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
		
	}
	// @formatter:on

	
	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(WebSecurityConfig.AUTHORIZATION_HEADER); // 從HTTP頭部獲取TOKEN
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7, bearerToken.length()); // 返回Token字串，去除Bearer
		}
		String jwt = request.getParameter(WebSecurityConfig.AUTHORIZATION_TOKEN); // 從請求引數中獲取TOKEN
		if (StringUtils.hasText(jwt)) {
			return jwt;
		}
		return null;
	}


}
