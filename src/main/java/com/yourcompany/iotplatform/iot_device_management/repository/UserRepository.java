package com.yourcompany.iotplatform.iot_device_management.repository;

import com.yourcompany.iotplatform.iot_device_management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Kullanıcı veritabanı işlemleri için repository
 * Spring Data JPA ile otomatik CRUD işlemleri sağlar
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Kullanıcı adına göre kullanıcı bulma
     * @param username Kullanıcı adı
     * @return Kullanıcı (varsa)
     */
    Optional<User> findByUsername(String username);
    
    /**
     * E-posta adresine göre kullanıcı bulma
     * @param email E-posta adresi
     * @return Kullanıcı (varsa)
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Aktif kullanıcıları listeleme
     * @return Aktif kullanıcı listesi
     */
    List<User> findByIsActiveTrue();
    
    /**
     * Belirli role sahip kullanıcıları listeleme
     * @param role Kullanıcı rolü
     * @return Rol sahibi kullanıcı listesi
     */
    List<User> findByRole(User.UserRole role);
    
    /**
     * Kullanıcı adı veya e-posta ile kullanıcı var mı kontrol etme
     * @param username Kullanıcı adı
     * @param email E-posta adresi
     * @return Kullanıcı var mı
     */
    boolean existsByUsernameOrEmail(String username, String email);
    
    /**
     * Kullanıcı adına göre kullanıcı var mı kontrol etme
     * @param username Kullanıcı adı
     * @return Kullanıcı var mı
     */
    boolean existsByUsername(String username);
    
    /**
     * E-posta adresine göre kullanıcı var mı kontrol etme
     * @param email E-posta adresi
     * @return Kullanıcı var mı
     */
    boolean existsByEmail(String email);
    
    /**
     * Kullanıcı adı veya e-posta ile kullanıcı bulma (giriş için)
     * @param usernameOrEmail Kullanıcı adı veya e-posta
     * @return Kullanıcı (varsa)
     */
    @Query("SELECT u FROM User u WHERE u.username = :usernameOrEmail OR u.email = :usernameOrEmail")
    Optional<User> findByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);
}
