package com.yourcompany.iotplatform.iot_device_management.controller;

import com.yourcompany.iotplatform.iot_device_management.dto.DeviceDto;
import com.yourcompany.iotplatform.iot_device_management.model.Device;
import com.yourcompany.iotplatform.iot_device_management.model.User;
import com.yourcompany.iotplatform.iot_device_management.service.DeviceService;
import com.yourcompany.iotplatform.iot_device_management.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Cihaz Yönetimi Controller'ı
 * Cihaz CRUD işlemleri ve yönetimi için REST API endpoint'leri
 */
@RestController
@RequestMapping("/api/devices")
@CrossOrigin(origins = "*")
public class DeviceController {
    
    private static final Logger logger = LoggerFactory.getLogger(DeviceController.class);
    
    @Autowired
    private DeviceService deviceService;
    
    @Autowired
    private UserService userService;
    
    /**
     * Tüm cihazları listeleme (sayfalı)
     * @param page Sayfa numarası
     * @param size Sayfa boyutu
     * @return Cihaz listesi
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'OPERATOR')")
    public ResponseEntity<List<DeviceDto>> getAllDevices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            List<Device> allDevices = deviceService.getAllActiveDevices();
            
            // Manuel sayfalama
            int start = page * size;
            int end = Math.min(start + size, allDevices.size());
            
            if (start >= allDevices.size()) {
                return ResponseEntity.ok(List.of());
            }
            
            List<Device> pagedDevices = allDevices.subList(start, end);
            List<DeviceDto> deviceDtos = pagedDevices.stream()
                    .map(DeviceDto::new)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(deviceDtos);
        } catch (Exception e) {
            logger.error("Cihaz listesi alma hatası: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * ID'ye göre cihaz getirme
     * @param id Cihaz ID'si
     * @return Cihaz bilgileri
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'OPERATOR')")
    public ResponseEntity<DeviceDto> getDeviceById(@PathVariable Long id) {
        try {
            Optional<Device> device = deviceService.getDeviceById(id);
            if (device.isPresent()) {
                return ResponseEntity.ok(new DeviceDto(device.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Cihaz getirme hatası: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Cihaz ID'sine göre cihaz getirme
     * @param deviceId Cihaz ID'si
     * @return Cihaz bilgileri
     */
    @GetMapping("/by-device-id/{deviceId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'OPERATOR')")
    public ResponseEntity<DeviceDto> getDeviceByDeviceId(@PathVariable String deviceId) {
        try {
            Optional<Device> device = deviceService.getDeviceByDeviceId(deviceId);
            if (device.isPresent()) {
                return ResponseEntity.ok(new DeviceDto(device.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Cihaz getirme hatası: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Yeni cihaz oluşturma
     * @param deviceDto Cihaz bilgileri
     * @return Oluşturulan cihaz
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<DeviceDto> createDevice(@Valid @RequestBody DeviceDto deviceDto) {
        try {
            // Mevcut kullanıcıyı alma
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User currentUser = userService.getUserByUsername(username).orElse(null);
            
            if (currentUser == null) {
                return ResponseEntity.badRequest().build();
            }
            
            Device createdDevice = deviceService.createDevice(deviceDto, currentUser.getId());
            return ResponseEntity.ok(new DeviceDto(createdDevice));
        } catch (Exception e) {
            logger.error("Cihaz oluşturma hatası: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Cihaz güncelleme
     * @param id Cihaz ID'si
     * @param deviceDto Güncellenecek cihaz bilgileri
     * @return Güncellenmiş cihaz
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<DeviceDto> updateDevice(@PathVariable Long id, @Valid @RequestBody DeviceDto deviceDto) {
        try {
            Device updatedDevice = deviceService.updateDevice(id, deviceDto);
            return ResponseEntity.ok(new DeviceDto(updatedDevice));
        } catch (Exception e) {
            logger.error("Cihaz güncelleme hatası: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Cihaz silme
     * @param id Cihaz ID'si
     * @return Silme işlemi sonucu
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Void> deleteDevice(@PathVariable Long id) {
        try {
            deviceService.deleteDevice(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Cihaz silme hatası: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Kullanıcının cihazlarını listeleme
     * @param page Sayfa numarası
     * @param size Sayfa boyutu
     * @return Kullanıcının cihazları
     */
    @GetMapping("/my-devices")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'OPERATOR')")
    public ResponseEntity<List<DeviceDto>> getMyDevices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User currentUser = userService.getUserByUsername(username).orElse(null);
            
            if (currentUser == null) {
                return ResponseEntity.badRequest().build();
            }
            
            List<Device> userDevices = deviceService.getDevicesByOwner(currentUser.getId());
            
            // Manuel sayfalama
            int start = page * size;
            int end = Math.min(start + size, userDevices.size());
            
            if (start >= userDevices.size()) {
                return ResponseEntity.ok(List.of());
            }
            
            List<Device> pagedDevices = userDevices.subList(start, end);
            List<DeviceDto> deviceDtos = pagedDevices.stream()
                    .map(DeviceDto::new)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(deviceDtos);
        } catch (Exception e) {
            logger.error("Kullanıcı cihazları alma hatası: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Belirli durumdaki cihazları listeleme
     * @param status Cihaz durumu
     * @return Durum sahibi cihazlar
     */
    @GetMapping("/by-status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'OPERATOR')")
    public ResponseEntity<List<DeviceDto>> getDevicesByStatus(@PathVariable Device.DeviceStatus status) {
        try {
            List<Device> devices = deviceService.getDevicesByStatus(status);
            List<DeviceDto> deviceDtos = devices.stream()
                    .map(DeviceDto::new)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(deviceDtos);
        } catch (Exception e) {
            logger.error("Durum bazlı cihaz listesi alma hatası: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Belirli tipteki cihazları listeleme
     * @param deviceType Cihaz tipi
     * @return Tip sahibi cihazlar
     */
    @GetMapping("/by-type/{deviceType}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'OPERATOR')")
    public ResponseEntity<List<DeviceDto>> getDevicesByType(@PathVariable Device.DeviceType deviceType) {
        try {
            List<Device> devices = deviceService.getDevicesByType(deviceType);
            List<DeviceDto> deviceDtos = devices.stream()
                    .map(DeviceDto::new)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(deviceDtos);
        } catch (Exception e) {
            logger.error("Tip bazlı cihaz listesi alma hatası: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Online cihazları listeleme
     * @return Online cihazlar
     */
    @GetMapping("/online")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'OPERATOR')")
    public ResponseEntity<List<DeviceDto>> getOnlineDevices() {
        try {
            List<Device> devices = deviceService.getOnlineDevices();
            List<DeviceDto> deviceDtos = devices.stream()
                    .map(DeviceDto::new)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(deviceDtos);
        } catch (Exception e) {
            logger.error("Online cihaz listesi alma hatası: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Cihaz adına göre arama
     * @param name Cihaz adı
     * @return Arama sonuçları
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'OPERATOR')")
    public ResponseEntity<List<DeviceDto>> searchDevicesByName(@RequestParam String name) {
        try {
            List<Device> devices = deviceService.searchDevicesByName(name);
            List<DeviceDto> deviceDtos = devices.stream()
                    .map(DeviceDto::new)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(deviceDtos);
        } catch (Exception e) {
            logger.error("Cihaz arama hatası: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Cihaz durumunu güncelleme
     * @param id Cihaz ID'si
     * @param status Yeni durum
     * @return Güncelleme sonucu
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Void> updateDeviceStatus(@PathVariable Long id, @RequestParam Device.DeviceStatus status) {
        try {
            deviceService.updateDeviceStatus(id, status);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Cihaz durumu güncelleme hatası: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Cihaz istatistiklerini getirme
     * @return Cihaz istatistikleri
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'OPERATOR')")
    public ResponseEntity<DeviceService.DeviceStatistics> getDeviceStatistics() {
        try {
            DeviceService.DeviceStatistics statistics = deviceService.getDeviceStatistics();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            logger.error("Cihaz istatistikleri alma hatası: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
