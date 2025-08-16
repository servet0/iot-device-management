package com.yourcompany.iotplatform.iot_device_management.service;

import com.yourcompany.iotplatform.iot_device_management.model.User;
import com.yourcompany.iotplatform.iot_device_management.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Kullanıcı Servisi
 * Kullanıcı işlemleri ve Spring Security entegrasyonu
 */
@Service
public class UserService implements UserDetailsService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Spring Security için kullanıcı yükleme
     * @param username Kullanıcı adı
     * @return UserDetails
     * @throws UsernameNotFoundException Kullanıcı bulunamadı
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsernameOrEmail(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Kullanıcı bulunamadı: " + username);
        }
        return user.get();
    }
    
    /**
     * Yeni kullanıcı oluşturma
     * @param user Kullanıcı bilgileri
     * @return Oluşturulan kullanıcı
     */
    public User createUser(User user) {
        // Şifreyi hash'leme
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Kullanıcı adı ve e-posta kontrolü
        if (userRepository.existsByUsernameOrEmail(user.getUsername(), user.getEmail())) {
            throw new RuntimeException("Kullanıcı adı veya e-posta zaten kullanımda");
        }
        
        User savedUser = userRepository.save(user);
        logger.info("Yeni kullanıcı oluşturuldu: {}", savedUser.getUsername());
        return savedUser;
    }
    
    /**
     * Kullanıcı güncelleme
     * @param id Kullanıcı ID'si
     * @param user Güncellenecek kullanıcı bilgileri
     * @return Güncellenmiş kullanıcı
     */
    public User updateUser(Long id, User user) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isEmpty()) {
            throw new RuntimeException("Kullanıcı bulunamadı: " + id);
        }
        
        User currentUser = existingUser.get();
        
        // Şifre değişikliği varsa hash'leme
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            currentUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        // Diğer alanları güncelleme
        if (user.getEmail() != null) {
            currentUser.setEmail(user.getEmail());
        }
        if (user.getRole() != null) {
            currentUser.setRole(user.getRole());
        }
        if (user.isActive() != currentUser.isActive()) {
            currentUser.setActive(user.isActive());
        }
        
        User updatedUser = userRepository.save(currentUser);
        logger.info("Kullanıcı güncellendi: {}", updatedUser.getUsername());
        return updatedUser;
    }
    
    /**
     * Kullanıcı silme (soft delete)
     * @param id Kullanıcı ID'si
     */
    public void deleteUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new RuntimeException("Kullanıcı bulunamadı: " + id);
        }
        
        User currentUser = user.get();
        currentUser.setActive(false);
        userRepository.save(currentUser);
        
        logger.info("Kullanıcı silindi: {}", currentUser.getUsername());
    }
    
    /**
     * Tüm aktif kullanıcıları listeleme
     * @return Aktif kullanıcı listesi
     */
    public List<User> getAllActiveUsers() {
        return userRepository.findByIsActiveTrue();
    }
    
    /**
     * ID'ye göre kullanıcı bulma
     * @param id Kullanıcı ID'si
     * @return Kullanıcı (varsa)
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * Kullanıcı adına göre kullanıcı bulma
     * @param username Kullanıcı adı
     * @return Kullanıcı (varsa)
     */
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * E-posta adresine göre kullanıcı bulma
     * @param email E-posta adresi
     * @return Kullanıcı (varsa)
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * Belirli role sahip kullanıcıları listeleme
     * @param role Kullanıcı rolü
     * @return Rol sahibi kullanıcı listesi
     */
    public List<User> getUsersByRole(User.UserRole role) {
        return userRepository.findByRole(role);
    }
    
    /**
     * Kullanıcı şifresini değiştirme
     * @param id Kullanıcı ID'si
     * @param newPassword Yeni şifre
     */
    public void changePassword(Long id, String newPassword) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new RuntimeException("Kullanıcı bulunamadı: " + id);
        }
        
        User currentUser = user.get();
        currentUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(currentUser);
        
        logger.info("Kullanıcı şifresi değiştirildi: {}", currentUser.getUsername());
    }
    
    /**
     * Kullanıcı hesabını aktif/pasif yapma
     * @param id Kullanıcı ID'si
     * @param active Aktif durumu
     */
    public void setUserActive(Long id, boolean active) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new RuntimeException("Kullanıcı bulunamadı: " + id);
        }
        
        User currentUser = user.get();
        currentUser.setActive(active);
        userRepository.save(currentUser);
        
        logger.info("Kullanıcı durumu değiştirildi: {} - {}", currentUser.getUsername(), active);
    }
}
