package com.steven.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import com.steven.demo.filter.JwtAuthenticationTokenFilter;





@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String AUTHORIZATION_TOKEN = "access_token";

	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService)// 自定義獲取使用者資訊
//			.passwordEncoder(passwordEncoder());// 設定密碼加密
			.passwordEncoder(NoOpPasswordEncoder.getInstance());// 設定密碼加密
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// 配置請求訪問策略
		http.cors().disable().csrf().disable()// 關閉CSRF、CORS			
			.authorizeRequests()// 驗證Http請求
			.antMatchers("/auth/login", "/register").permitAll()// 允許所有使用者訪問首頁 與 登入				
			.antMatchers("/userpage").hasAnyRole("user")// 使用者頁面需要使用者許可權
			.anyRequest().authenticated().and()// 其它任何請求都要經過認證通過
			// 由於使用Token，所以不需要Session
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
			.logout().permitAll();// 設定登出
		// 新增JWT filter
		http.addFilterBefore(oncePerRequestFilter(), UsernamePasswordAuthenticationFilter.class);
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
	return new BCryptPasswordEncoder();
	}
	
	@Bean
	public OncePerRequestFilter oncePerRequestFilter() {
	return new JwtAuthenticationTokenFilter();
	}

	@Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
	
	@Bean
	GrantedAuthorityDefaults grantedAuthorityDefaults() {
	    return new GrantedAuthorityDefaults(""); // Remove the ROLE_ prefix
	}
}
