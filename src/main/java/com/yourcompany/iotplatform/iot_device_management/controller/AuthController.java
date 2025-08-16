package com.yourcompany.iotplatform.iot_device_management.controller;

import com.yourcompany.iotplatform.iot_device_management.dto.AuthResponse;
import com.yourcompany.iotplatform.iot_device_management.model.User;
import com.yourcompany.iotplatform.iot_device_management.security.JwtTokenProvider;
import com.yourcompany.iotplatform.iot_device_management.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Kimlik Doğrulama Controller'ı
 * Kullanıcı kayıt, giriş ve JWT token işlemleri
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    private UserService userService;
    
    /**
     * Kullanıcı girişi
     * @param loginRequest Giriş bilgileri
     * @return JWT token ve kullanıcı bilgileri
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Kullanıcıyı authenticate etme
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsernameOrEmail(),
                    loginRequest.getPassword()
                )
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // JWT token oluşturma
            String jwt = jwtTokenProvider.generateToken(authentication);
            
            // Kullanıcı bilgilerini alma
            User user = userService.getUserByUsername(loginRequest.getUsernameOrEmail())
                    .orElse(userService.getUserByEmail(loginRequest.getUsernameOrEmail()).orElse(null));
            
            if (user == null) {
                return ResponseEntity.badRequest().body(new AuthResponse("Kullanıcı bulunamadı", null, null));
            }
            
            AuthResponse response = new AuthResponse("Giriş başarılı", jwt, user);
            logger.info("Kullanıcı giriş yaptı: {}", user.getUsername());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Giriş hatası: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new AuthResponse("Geçersiz kullanıcı adı veya şifre", null, null));
        }
    }
    
    /**
     * Kullanıcı kaydı
     * @param registerRequest Kayıt bilgileri
     * @return JWT token ve kullanıcı bilgileri
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            // Kullanıcı adı ve e-posta kontrolü
            if (userService.getUserByUsername(registerRequest.getUsername()).isPresent()) {
                return ResponseEntity.badRequest().body(new AuthResponse("Kullanıcı adı zaten kullanımda", null, null));
            }
            
            if (userService.getUserByEmail(registerRequest.getEmail()).isPresent()) {
                return ResponseEntity.badRequest().body(new AuthResponse("E-posta adresi zaten kullanımda", null, null));
            }
            
            // Yeni kullanıcı oluşturma
            User user = new User();
            user.setUsername(registerRequest.getUsername());
            user.setEmail(registerRequest.getEmail());
            user.setPassword(registerRequest.getPassword());
            user.setRole(User.UserRole.USER); // Varsayılan rol
            
            User savedUser = userService.createUser(user);
            
            // JWT token oluşturma
            String jwt = jwtTokenProvider.generateTokenFromUsername(savedUser.getUsername());
            
            AuthResponse response = new AuthResponse("Kayıt başarılı", jwt, savedUser);
            logger.info("Yeni kullanıcı kaydı: {}", savedUser.getUsername());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Kayıt hatası: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new AuthResponse("Kayıt işlemi başarısız: " + e.getMessage(), null, null));
        }
    }
    
    /**
     * Token doğrulama
     * @param token JWT token
     * @return Token geçerliliği
     */
    @PostMapping("/validate")
    public ResponseEntity<AuthResponse> validateToken(@RequestParam String token) {
        try {
            if (jwtTokenProvider.validateToken(token)) {
                String username = jwtTokenProvider.getUsernameFromToken(token);
                User user = userService.getUserByUsername(username).orElse(null);
                
                if (user != null) {
                    return ResponseEntity.ok(new AuthResponse("Token geçerli", token, user));
                }
            }
            
            return ResponseEntity.badRequest().body(new AuthResponse("Geçersiz token", null, null));
            
        } catch (Exception e) {
            logger.error("Token doğrulama hatası: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new AuthResponse("Token doğrulama hatası", null, null));
        }
    }
    
    /**
     * Kullanıcı bilgilerini getirme
     * @return Mevcut kullanıcı bilgileri
     */
    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            User user = userService.getUserByUsername(username).orElse(null);
            if (user != null) {
                return ResponseEntity.ok(new AuthResponse("Kullanıcı bilgileri alındı", null, user));
            }
            
            return ResponseEntity.badRequest().body(new AuthResponse("Kullanıcı bulunamadı", null, null));
            
        } catch (Exception e) {
            logger.error("Kullanıcı bilgileri alma hatası: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new AuthResponse("Kullanıcı bilgileri alma hatası", null, null));
        }
    }
    
    /**
     * Giriş isteği DTO'su
     */
    public static class LoginRequest {
        private String usernameOrEmail;
        private String password;
        
        // Constructors
        public LoginRequest() {}
        
        public LoginRequest(String usernameOrEmail, String password) {
            this.usernameOrEmail = usernameOrEmail;
            this.password = password;
        }
        
        // Getters and Setters
        public String getUsernameOrEmail() { return usernameOrEmail; }
        public void setUsernameOrEmail(String usernameOrEmail) { this.usernameOrEmail = usernameOrEmail; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    
    /**
     * Kayıt isteği DTO'su
     */
    public static class RegisterRequest {
        private String username;
        private String email;
        private String password;
        
        // Constructors
        public RegisterRequest() {}
        
        public RegisterRequest(String username, String email, String password) {
            this.username = username;
            this.email = email;
            this.password = password;
        }
        
        // Getters and Setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
