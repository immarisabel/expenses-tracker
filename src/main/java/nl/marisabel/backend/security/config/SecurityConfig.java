package nl.marisabel.backend.security.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

 @Bean
 public UserDetailsService userDetailsService(PasswordEncoder bCryptPasswordEncoder) {
  InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
  manager.createUser(User.withUsername("POL")
          .password(bCryptPasswordEncoder.encode("Asran206"))
          .roles("ADMIN")
          .build());
  return manager;
 }

 @Bean
 public PasswordEncoder passwordEncoder() {
  return new BCryptPasswordEncoder();
 }




 @Bean
 public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

  http
          .cors(withDefaults())
          .csrf(withDefaults())

          .authorizeHttpRequests((requests) -> requests
                  .requestMatchers("/css/**", "/", "/403", "/errorpage", "/simulateError").permitAll()
                  .requestMatchers("/chart/**", "/expenses/**", "/categories/**").hasRole("ADMIN")
                  .requestMatchers("/upload").hasRole("ADMIN")
                  .requestMatchers("/expenses/updateCategory/**").hasRole("ADMIN")
                  .requestMatchers("/expenses/filter").hasRole("ADMIN")

          )
          .formLogin((form) -> form
                          .loginPage("/login")
                          .defaultSuccessUrl("/expenses", true)
                          .permitAll()
          )
          .logout((logout) -> logout.permitAll())
          .exceptionHandling(
                  (exceptionHandling) -> exceptionHandling
                          .accessDeniedPage("/403"));
//
  return http.build();

 }

 @Bean
 public WebSecurityCustomizer webSecurityCustomizer() {
  return (web) -> web.ignoring().requestMatchers("/favicon.ico");
 }
}