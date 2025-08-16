package com.yourcompany.iotplatform.iot_device_management.service;

import com.yourcompany.iotplatform.iot_device_management.dto.DeviceDto;
import com.yourcompany.iotplatform.iot_device_management.model.Device;
import com.yourcompany.iotplatform.iot_device_management.model.User;
import com.yourcompany.iotplatform.iot_device_management.repository.DeviceRepository;
import com.yourcompany.iotplatform.iot_device_management.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Cihaz Servisi
 * Cihaz işlemleri ve yönetimi
 */
@Service
public class DeviceService {
    
    private static final Logger logger = LoggerFactory.getLogger(DeviceService.class);
    
    @Autowired
    private DeviceRepository deviceRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Yeni cihaz oluşturma
     * @param deviceDto Cihaz bilgileri
     * @param ownerId Cihaz sahibi ID'si
     * @return Oluşturulan cihaz
     */
    public Device createDevice(DeviceDto deviceDto, Long ownerId) {
        // Cihaz ID'si benzersizlik kontrolü
        if (deviceRepository.existsByDeviceId(deviceDto.getDeviceId())) {
            throw new RuntimeException("Cihaz ID zaten kullanımda: " + deviceDto.getDeviceId());
        }
        
        // Cihaz sahibini bulma
        Optional<User> owner = userRepository.findById(ownerId);
        if (owner.isEmpty()) {
            throw new RuntimeException("Cihaz sahibi bulunamadı: " + ownerId);
        }
        
        // Cihaz oluşturma
        Device device = new Device();
        device.setDeviceId(deviceDto.getDeviceId());
        device.setName(deviceDto.getName());
        device.setDescription(deviceDto.getDescription());
        device.setDeviceType(deviceDto.getDeviceType());
        device.setStatus(Device.DeviceStatus.OFFLINE);
        device.setIpAddress(deviceDto.getIpAddress());
        device.setFirmwareVersion(deviceDto.getFirmwareVersion());
        device.setHardwareVersion(deviceDto.getHardwareVersion());
        device.setLocationLat(deviceDto.getLocationLat());
        device.setLocationLng(deviceDto.getLocationLng());
        device.setLocationDescription(deviceDto.getLocationDescription());
        device.setOwner(owner.get());
        
        Device savedDevice = deviceRepository.save(device);
        logger.info("Yeni cihaz oluşturuldu: {} - {}", savedDevice.getDeviceId(), savedDevice.getName());
        return savedDevice;
    }
    
    /**
     * Cihaz güncelleme
     * @param id Cihaz ID'si
     * @param deviceDto Güncellenecek cihaz bilgileri
     * @return Güncellenmiş cihaz
     */
    public Device updateDevice(Long id, DeviceDto deviceDto) {
        Optional<Device> existingDevice = deviceRepository.findById(id);
        if (existingDevice.isEmpty()) {
            throw new RuntimeException("Cihaz bulunamadı: " + id);
        }
        
        Device device = existingDevice.get();
        
        // Cihaz ID'si değişikliği varsa benzersizlik kontrolü
        if (!device.getDeviceId().equals(deviceDto.getDeviceId()) && 
            deviceRepository.existsByDeviceId(deviceDto.getDeviceId())) {
            throw new RuntimeException("Cihaz ID zaten kullanımda: " + deviceDto.getDeviceId());
        }
        
        // Alanları güncelleme
        device.setDeviceId(deviceDto.getDeviceId());
        device.setName(deviceDto.getName());
        device.setDescription(deviceDto.getDescription());
        device.setDeviceType(deviceDto.getDeviceType());
        device.setIpAddress(deviceDto.getIpAddress());
        device.setFirmwareVersion(deviceDto.getFirmwareVersion());
        device.setHardwareVersion(deviceDto.getHardwareVersion());
        device.setLocationLat(deviceDto.getLocationLat());
        device.setLocationLng(deviceDto.getLocationLng());
        device.setLocationDescription(deviceDto.getLocationDescription());
        
        Device updatedDevice = deviceRepository.save(device);
        logger.info("Cihaz güncellendi: {} - {}", updatedDevice.getDeviceId(), updatedDevice.getName());
        return updatedDevice;
    }
    
    /**
     * Cihaz silme (soft delete)
     * @param id Cihaz ID'si
     */
    public void deleteDevice(Long id) {
        Optional<Device> device = deviceRepository.findById(id);
        if (device.isEmpty()) {
            throw new RuntimeException("Cihaz bulunamadı: " + id);
        }
        
        Device currentDevice = device.get();
        currentDevice.setActive(false);
        deviceRepository.save(currentDevice);
        
        logger.info("Cihaz silindi: {} - {}", currentDevice.getDeviceId(), currentDevice.getName());
    }
    
    /**
     * ID'ye göre cihaz bulma
     * @param id Cihaz ID'si
     * @return Cihaz (varsa)
     */
    public Optional<Device> getDeviceById(Long id) {
        return deviceRepository.findById(id);
    }
    
    /**
     * Cihaz ID'sine göre cihaz bulma
     * @param deviceId Cihaz ID'si
     * @return Cihaz (varsa)
     */
    public Optional<Device> getDeviceByDeviceId(String deviceId) {
        return deviceRepository.findByDeviceId(deviceId);
    }
    
    /**
     * Tüm aktif cihazları listeleme
     * @return Aktif cihaz listesi
     */
    public List<Device> getAllActiveDevices() {
        return deviceRepository.findByIsActiveTrue();
    }
    
    /**
     * Belirli kullanıcıya ait cihazları listeleme
     * @param ownerId Cihaz sahibi ID'si
     * @return Kullanıcıya ait cihaz listesi
     */
    public List<Device> getDevicesByOwner(Long ownerId) {
        Optional<User> owner = userRepository.findById(ownerId);
        if (owner.isEmpty()) {
            throw new RuntimeException("Kullanıcı bulunamadı: " + ownerId);
        }
        return deviceRepository.findByOwner(owner.get());
    }
    
    /**
     * Belirli kullanıcıya ait aktif cihazları sayfalı listeleme
     * @param ownerId Cihaz sahibi ID'si
     * @param pageable Sayfalama bilgisi
     * @return Sayfalı cihaz listesi
     */
    public Page<Device> getActiveDevicesByOwner(Long ownerId, Pageable pageable) {
        Optional<User> owner = userRepository.findById(ownerId);
        if (owner.isEmpty()) {
            throw new RuntimeException("Kullanıcı bulunamadı: " + ownerId);
        }
        return deviceRepository.findByOwnerAndIsActiveTrue(owner.get(), pageable);
    }
    
    /**
     * Belirli durumdaki cihazları listeleme
     * @param status Cihaz durumu
     * @return Durum sahibi cihaz listesi
     */
    public List<Device> getDevicesByStatus(Device.DeviceStatus status) {
        return deviceRepository.findByStatus(status);
    }
    
    /**
     * Belirli tipteki cihazları listeleme
     * @param deviceType Cihaz tipi
     * @return Tip sahibi cihaz listesi
     */
    public List<Device> getDevicesByType(Device.DeviceType deviceType) {
        return deviceRepository.findByDeviceType(deviceType);
    }
    
    /**
     * Online cihazları listeleme
     * @return Online cihaz listesi
     */
    public List<Device> getOnlineDevices() {
        return deviceRepository.findOnlineDevices();
    }
    
    /**
     * Cihaz adına göre arama yapma
     * @param name Cihaz adı
     * @return Cihaz listesi
     */
    public List<Device> searchDevicesByName(String name) {
        return deviceRepository.findByNameContaining(name);
    }
    
    /**
     * Cihaz durumunu güncelleme
     * @param id Cihaz ID'si
     * @param status Yeni durum
     */
    public void updateDeviceStatus(Long id, Device.DeviceStatus status) {
        Optional<Device> device = deviceRepository.findById(id);
        if (device.isEmpty()) {
            throw new RuntimeException("Cihaz bulunamadı: " + id);
        }
        
        Device currentDevice = device.get();
        currentDevice.setStatus(status);
        
        if (status == Device.DeviceStatus.ONLINE) {
            currentDevice.setLastSeen(LocalDateTime.now());
        }
        
        deviceRepository.save(currentDevice);
        logger.info("Cihaz durumu güncellendi: {} - {}", currentDevice.getDeviceId(), status);
    }
    
    /**
     * Cihazın son görülme zamanını güncelleme
     * @param deviceId Cihaz ID'si
     */
    public void updateDeviceLastSeen(String deviceId) {
        Optional<Device> device = deviceRepository.findByDeviceId(deviceId);
        if (device.isPresent()) {
            Device currentDevice = device.get();
            currentDevice.setLastSeen(LocalDateTime.now());
            currentDevice.setStatus(Device.DeviceStatus.ONLINE);
            deviceRepository.save(currentDevice);
        }
    }
    
    /**
     * Cihaz istatistiklerini getirme
     * @return Cihaz istatistikleri
     */
    public DeviceStatistics getDeviceStatistics() {
        long totalDevices = deviceRepository.count();
        long onlineDevices = deviceRepository.countByStatus(Device.DeviceStatus.ONLINE);
        long offlineDevices = deviceRepository.countByStatus(Device.DeviceStatus.OFFLINE);
        long sensorDevices = deviceRepository.countByDeviceType(Device.DeviceType.SENSOR);
        long actuatorDevices = deviceRepository.countByDeviceType(Device.DeviceType.ACTUATOR);
        
        return new DeviceStatistics(totalDevices, onlineDevices, offlineDevices, sensorDevices, actuatorDevices);
    }
    
    /**
     * Cihaz istatistikleri sınıfı
     */
    public static class DeviceStatistics {
        private final long totalDevices;
        private final long onlineDevices;
        private final long offlineDevices;
        private final long sensorDevices;
        private final long actuatorDevices;
        
        public DeviceStatistics(long totalDevices, long onlineDevices, long offlineDevices, 
                               long sensorDevices, long actuatorDevices) {
            this.totalDevices = totalDevices;
            this.onlineDevices = onlineDevices;
            this.offlineDevices = offlineDevices;
            this.sensorDevices = sensorDevices;
            this.actuatorDevices = actuatorDevices;
        }
        
        // Getters
        public long getTotalDevices() { return totalDevices; }
        public long getOnlineDevices() { return onlineDevices; }
        public long getOfflineDevices() { return offlineDevices; }
        public long getSensorDevices() { return sensorDevices; }
        public long getActuatorDevices() { return actuatorDevices; }
    }
}
