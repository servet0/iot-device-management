package com.yourcompany.iotplatform.iot_device_management.repository;

import com.yourcompany.iotplatform.iot_device_management.model.Device;
import com.yourcompany.iotplatform.iot_device_management.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Cihaz veritabanı işlemleri için repository
 * Spring Data JPA ile otomatik CRUD işlemleri sağlar
 */
@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    
    /**
     * Cihaz ID'sine göre cihaz bulma
     * @param deviceId Cihaz ID'si
     * @return Cihaz (varsa)
     */
    Optional<Device> findByDeviceId(String deviceId);
    
    /**
     * Aktif cihazları listeleme
     * @return Aktif cihaz listesi
     */
    List<Device> findByIsActiveTrue();
    
    /**
     * Belirli durumdaki cihazları listeleme
     * @param status Cihaz durumu
     * @return Durum sahibi cihaz listesi
     */
    List<Device> findByStatus(Device.DeviceStatus status);
    
    /**
     * Belirli tipteki cihazları listeleme
     * @param deviceType Cihaz tipi
     * @return Tip sahibi cihaz listesi
     */
    List<Device> findByDeviceType(Device.DeviceType deviceType);
    
    /**
     * Belirli kullanıcıya ait cihazları listeleme
     * @param owner Cihaz sahibi
     * @return Kullanıcıya ait cihaz listesi
     */
    List<Device> findByOwner(User owner);
    
    /**
     * Cihaz ID'sine göre cihaz var mı kontrol etme
     * @param deviceId Cihaz ID'si
     * @return Cihaz var mı
     */
    boolean existsByDeviceId(String deviceId);
    
    /**
     * Belirli durumdaki cihaz sayısını bulma
     * @param status Cihaz durumu
     * @return Cihaz sayısı
     */
    long countByStatus(Device.DeviceStatus status);
    
    /**
     * Belirli tipteki cihaz sayısını bulma
     * @param deviceType Cihaz tipi
     * @return Cihaz sayısı
     */
    long countByDeviceType(Device.DeviceType deviceType);
    
    /**
     * Son görülme zamanına göre cihazları bulma
     * @param lastSeen Son görülme zamanı
     * @return Cihaz listesi
     */
    List<Device> findByLastSeenBefore(LocalDateTime lastSeen);
    
    /**
     * Online cihazları listeleme
     * @return Online cihaz listesi
     */
    @Query("SELECT d FROM Device d WHERE d.status = 'ONLINE' AND d.isActive = true")
    List<Device> findOnlineDevices();
    
    /**
     * Belirli kullanıcıya ait aktif cihazları sayfalı listeleme
     * @param owner Cihaz sahibi
     * @param pageable Sayfalama bilgisi
     * @return Sayfalı cihaz listesi
     */
    Page<Device> findByOwnerAndIsActiveTrue(User owner, Pageable pageable);
    
    /**
     * Cihaz adına göre arama yapma
     * @param name Cihaz adı (kısmi eşleşme)
     * @return Cihaz listesi
     */
    @Query("SELECT d FROM Device d WHERE d.name LIKE %:name% AND d.isActive = true")
    List<Device> findByNameContaining(@Param("name") String name);
    
    /**
     * Belirli IP adresine sahip cihazları bulma
     * @param ipAddress IP adresi
     * @return Cihaz listesi
     */
    List<Device> findByIpAddress(String ipAddress);
}
