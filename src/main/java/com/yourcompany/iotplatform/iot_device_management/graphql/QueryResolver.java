package com.yourcompany.iotplatform.iot_device_management.graphql;

import com.yourcompany.iotplatform.iot_device_management.model.Device;
import com.yourcompany.iotplatform.iot_device_management.model.TelemetryData;
import com.yourcompany.iotplatform.iot_device_management.model.User;
import com.yourcompany.iotplatform.iot_device_management.service.DeviceService;
import com.yourcompany.iotplatform.iot_device_management.service.TelemetryDataService;
import com.yourcompany.iotplatform.iot_device_management.service.UserService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * GraphQL Query Resolver
 * GraphQL sorguları için resolver işlemleri
 */
@Component
public class QueryResolver implements GraphQLQueryResolver {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private DeviceService deviceService;
    
    @Autowired
    private TelemetryDataService telemetryDataService;
    
    // User Queries
    
    /**
     * Mevcut kullanıcıyı getirme
     * @return Mevcut kullanıcı
     */
    @PreAuthorize("isAuthenticated()")
    public User me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userService.getUserByUsername(username).orElse(null);
    }
    
    /**
     * ID'ye göre kullanıcı getirme
     * @param id Kullanıcı ID'si
     * @return Kullanıcı
     */
    @PreAuthorize("hasRole('ADMIN')")
    public User user(String id) {
        return userService.getUserById(Long.parseLong(id)).orElse(null);
    }
    
    /**
     * Tüm kullanıcıları getirme
     * @return Kullanıcı listesi
     */
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> users() {
        return userService.getAllActiveUsers();
    }
    
    /**
     * Belirli role sahip kullanıcıları getirme
     * @param role Kullanıcı rolü
     * @return Kullanıcı listesi
     */
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> usersByRole(User.UserRole role) {
        return userService.getUsersByRole(role);
    }
    
    // Device Queries
    
    /**
     * ID'ye göre cihaz getirme
     * @param id Cihaz ID'si
     * @return Cihaz
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'OPERATOR')")
    public Device device(String id) {
        return deviceService.getDeviceById(Long.parseLong(id)).orElse(null);
    }
    
    /**
     * Cihaz ID'sine göre cihaz getirme
     * @param deviceId Cihaz ID'si
     * @return Cihaz
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'OPERATOR')")
    public Device deviceByDeviceId(String deviceId) {
        return deviceService.getDeviceByDeviceId(deviceId).orElse(null);
    }
    
    /**
     * Tüm cihazları getirme
     * @return Cihaz listesi
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'OPERATOR')")
    public List<Device> devices() {
        return deviceService.getAllActiveDevices();
    }
    
    /**
     * Mevcut kullanıcının cihazlarını getirme
     * @return Cihaz listesi
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'OPERATOR')")
    public List<Device> myDevices() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userService.getUserByUsername(username).orElse(null);
        
        if (currentUser == null) {
            return List.of();
        }
        
        return deviceService.getDevicesByOwner(currentUser.getId());
    }
    
    /**
     * Belirli durumdaki cihazları getirme
     * @param status Cihaz durumu
     * @return Cihaz listesi
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'OPERATOR')")
    public List<Device> devicesByStatus(Device.DeviceStatus status) {
        return deviceService.getDevicesByStatus(status);
    }
    
    /**
     * Belirli tipteki cihazları getirme
     * @param deviceType Cihaz tipi
     * @return Cihaz listesi
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'OPERATOR')")
    public List<Device> devicesByType(Device.DeviceType deviceType) {
        return deviceService.getDevicesByType(deviceType);
    }
    
    /**
     * Online cihazları getirme
     * @return Cihaz listesi
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'OPERATOR')")
    public List<Device> onlineDevices() {
        return deviceService.getOnlineDevices();
    }
    
    /**
     * Cihaz adına göre arama
     * @param name Cihaz adı
     * @return Cihaz listesi
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'OPERATOR')")
    public List<Device> searchDevices(String name) {
        return deviceService.searchDevicesByName(name);
    }
    
    /**
     * Cihaz istatistiklerini getirme
     * @return Cihaz istatistikleri
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'OPERATOR')")
    public DeviceService.DeviceStatistics deviceStatistics() {
        return deviceService.getDeviceStatistics();
    }
    
    // Telemetry Queries
    
    /**
     * Belirli cihaza ait telemetri verilerini getirme
     * @param deviceId Cihaz ID'si
     * @param limit Limit sayısı
     * @return Telemetri verileri
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'OPERATOR')")
    public List<TelemetryData> telemetryData(String deviceId, Integer limit) {
        int dataLimit = limit != null ? limit : 10;
        return telemetryDataService.getTelemetryDataByDevice(Long.parseLong(deviceId), dataLimit);
    }
    
    /**
     * Belirli cihaza ait telemetri verilerini zaman aralığına göre getirme
     * @param deviceId Cihaz ID'si
     * @param startTime Başlangıç zamanı
     * @param endTime Bitiş zamanı
     * @return Telemetri verileri
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'OPERATOR')")
    public List<TelemetryData> telemetryDataByTimeRange(String deviceId, String startTime, String endTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDateTime start = LocalDateTime.parse(startTime, formatter);
        LocalDateTime end = LocalDateTime.parse(endTime, formatter);
        
        return telemetryDataService.getTelemetryDataByTimeRange(Long.parseLong(deviceId), start, end);
    }
    
    /**
     * Belirli cihazın en son telemetri verisini getirme
     * @param deviceId Cihaz ID'si
     * @return En son telemetri verisi
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'OPERATOR')")
    public TelemetryData latestTelemetryData(String deviceId) {
        return telemetryDataService.getLatestTelemetryData(Long.parseLong(deviceId)).orElse(null);
    }
}
