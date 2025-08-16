package com.yourcompany.iotplatform.iot_device_management.graphql;

import com.yourcompany.iotplatform.iot_device_management.dto.AuthResponse;
import com.yourcompany.iotplatform.iot_device_management.dto.DeviceDto;
import com.yourcompany.iotplatform.iot_device_management.model.Device;
import com.yourcompany.iotplatform.iot_device_management.model.User;
import com.yourcompany.iotplatform.iot_device_management.security.JwtTokenProvider;
import com.yourcompany.iotplatform.iot_device_management.service.DeviceService;
import com.yourcompany.iotplatform.iot_device_management.service.UserService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * GraphQL Mutation Resolver
 * GraphQL mutasyonları için resolver işlemleri
 */
@Component
public class MutationResolver implements GraphQLMutationResolver {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private DeviceService deviceService;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    // Auth Mutations
    
    /**
     * Kullanıcı girişi
     * @param input Giriş bilgileri
     * @return Kimlik doğrulama yanıtı
     */
    public AuthResponse login(LoginInput input) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    input.getUsernameOrEmail(),
                    input.getPassword()
                )
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            String jwt = jwtTokenProvider.generateToken(authentication);
            
            User user = userService.getUserByUsername(input.getUsernameOrEmail())
                    .orElse(userService.getUserByEmail(input.getUsernameOrEmail()).orElse(null));
            
            if (user == null) {
                return new AuthResponse("Kullanıcı bulunamadı", null, null);
            }
            
            return new AuthResponse("Giriş başarılı", jwt, user);
            
        } catch (Exception e) {
            return new AuthResponse("Geçersiz kullanıcı adı veya şifre", null, null);
        }
    }
    
    /**
     * Kullanıcı kaydı
     * @param input Kayıt bilgileri
     * @return Kimlik doğrulama yanıtı
     */
    public AuthResponse register(RegisterInput input) {
        try {
            if (userService.getUserByUsername(input.getUsername()).isPresent()) {
                return new AuthResponse("Kullanıcı adı zaten kullanımda", null, null);
            }
            
            if (userService.getUserByEmail(input.getEmail()).isPresent()) {
                return new AuthResponse("E-posta adresi zaten kullanımda", null, null);
            }
            
            User user = new User();
            user.setUsername(input.getUsername());
            user.setEmail(input.getEmail());
            user.setPassword(input.getPassword());
            user.setRole(User.UserRole.USER);
            
            User savedUser = userService.createUser(user);
            String jwt = jwtTokenProvider.generateTokenFromUsername(savedUser.getUsername());
            
            return new AuthResponse("Kayıt başarılı", jwt, savedUser);
            
        } catch (Exception e) {
            return new AuthResponse("Kayıt işlemi başarısız: " + e.getMessage(), null, null);
        }
    }
    
    // User Mutations
    
    /**
     * Kullanıcı güncelleme
     * @param id Kullanıcı ID'si
     * @param username Kullanıcı adı
     * @param email E-posta
     * @param role Rol
     * @param isActive Aktif durumu
     * @return Güncellenmiş kullanıcı
     */
    @PreAuthorize("hasRole('ADMIN')")
    public User updateUser(String id, String username, String email, User.UserRole role, Boolean isActive) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setRole(role);
        if (isActive != null) {
            user.setActive(isActive);
        }
        
        return userService.updateUser(Long.parseLong(id), user);
    }
    
    /**
     * Kullanıcı silme
     * @param id Kullanıcı ID'si
     * @return Silme işlemi sonucu
     */
    @PreAuthorize("hasRole('ADMIN')")
    public Boolean deleteUser(String id) {
        try {
            userService.deleteUser(Long.parseLong(id));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Şifre değiştirme
     * @param id Kullanıcı ID'si
     * @param newPassword Yeni şifre
     * @return İşlem sonucu
     */
    @PreAuthorize("hasRole('ADMIN')")
    public Boolean changePassword(String id, String newPassword) {
        try {
            userService.changePassword(Long.parseLong(id), newPassword);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    // Device Mutations
    
    /**
     * Cihaz oluşturma
     * @param input Cihaz bilgileri
     * @return Oluşturulan cihaz
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Device createDevice(DeviceInput input) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userService.getUserByUsername(username).orElse(null);
        
        if (currentUser == null) {
            throw new RuntimeException("Kullanıcı bulunamadı");
        }
        
        DeviceDto deviceDto = new DeviceDto();
        deviceDto.setDeviceId(input.getDeviceId());
        deviceDto.setName(input.getName());
        deviceDto.setDescription(input.getDescription());
        deviceDto.setDeviceType(input.getDeviceType());
        deviceDto.setIpAddress(input.getIpAddress());
        deviceDto.setFirmwareVersion(input.getFirmwareVersion());
        deviceDto.setHardwareVersion(input.getHardwareVersion());
        deviceDto.setLocationLat(input.getLocationLat());
        deviceDto.setLocationLng(input.getLocationLng());
        deviceDto.setLocationDescription(input.getLocationDescription());
        
        return deviceService.createDevice(deviceDto, currentUser.getId());
    }
    
    /**
     * Cihaz güncelleme
     * @param id Cihaz ID'si
     * @param input Güncellenecek cihaz bilgileri
     * @return Güncellenmiş cihaz
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Device updateDevice(String id, DeviceUpdateInput input) {
        DeviceDto deviceDto = new DeviceDto();
        deviceDto.setDeviceId(input.getDeviceId());
        deviceDto.setName(input.getName());
        deviceDto.setDescription(input.getDescription());
        deviceDto.setDeviceType(input.getDeviceType());
        deviceDto.setIpAddress(input.getIpAddress());
        deviceDto.setFirmwareVersion(input.getFirmwareVersion());
        deviceDto.setHardwareVersion(input.getHardwareVersion());
        deviceDto.setLocationLat(input.getLocationLat());
        deviceDto.setLocationLng(input.getLocationLng());
        deviceDto.setLocationDescription(input.getLocationDescription());
        
        return deviceService.updateDevice(Long.parseLong(id), deviceDto);
    }
    
    /**
     * Cihaz silme
     * @param id Cihaz ID'si
     * @return Silme işlemi sonucu
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Boolean deleteDevice(String id) {
        try {
            deviceService.deleteDevice(Long.parseLong(id));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Cihaz durumu güncelleme
     * @param id Cihaz ID'si
     * @param status Yeni durum
     * @return Güncellenmiş cihaz
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Device updateDeviceStatus(String id, Device.DeviceStatus status) {
        deviceService.updateDeviceStatus(Long.parseLong(id), status);
        return deviceService.getDeviceById(Long.parseLong(id)).orElse(null);
    }
    
    // Input Classes
    
    public static class LoginInput {
        private String usernameOrEmail;
        private String password;
        
        public String getUsernameOrEmail() { return usernameOrEmail; }
        public void setUsernameOrEmail(String usernameOrEmail) { this.usernameOrEmail = usernameOrEmail; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    
    public static class RegisterInput {
        private String username;
        private String email;
        private String password;
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    
    public static class DeviceInput {
        private String deviceId;
        private String name;
        private String description;
        private Device.DeviceType deviceType;
        private String ipAddress;
        private String firmwareVersion;
        private String hardwareVersion;
        private Double locationLat;
        private Double locationLng;
        private String locationDescription;
        
        // Getters and Setters
        public String getDeviceId() { return deviceId; }
        public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Device.DeviceType getDeviceType() { return deviceType; }
        public void setDeviceType(Device.DeviceType deviceType) { this.deviceType = deviceType; }
        
        public String getIpAddress() { return ipAddress; }
        public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
        
        public String getFirmwareVersion() { return firmwareVersion; }
        public void setFirmwareVersion(String firmwareVersion) { this.firmwareVersion = firmwareVersion; }
        
        public String getHardwareVersion() { return hardwareVersion; }
        public void setHardwareVersion(String hardwareVersion) { this.hardwareVersion = hardwareVersion; }
        
        public Double getLocationLat() { return locationLat; }
        public void setLocationLat(Double locationLat) { this.locationLat = locationLat; }
        
        public Double getLocationLng() { return locationLng; }
        public void setLocationLng(Double locationLng) { this.locationLng = locationLng; }
        
        public String getLocationDescription() { return locationDescription; }
        public void setLocationDescription(String locationDescription) { this.locationDescription = locationDescription; }
    }
    
    public static class DeviceUpdateInput {
        private String deviceId;
        private String name;
        private String description;
        private Device.DeviceType deviceType;
        private String ipAddress;
        private String firmwareVersion;
        private String hardwareVersion;
        private Double locationLat;
        private Double locationLng;
        private String locationDescription;
        
        // Getters and Setters
        public String getDeviceId() { return deviceId; }
        public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Device.DeviceType getDeviceType() { return deviceType; }
        public void setDeviceType(Device.DeviceType deviceType) { this.deviceType = deviceType; }
        
        public String getIpAddress() { return ipAddress; }
        public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
        
        public String getFirmwareVersion() { return firmwareVersion; }
        public void setFirmwareVersion(String firmwareVersion) { this.firmwareVersion = firmwareVersion; }
        
        public String getHardwareVersion() { return hardwareVersion; }
        public void setHardwareVersion(String hardwareVersion) { this.hardwareVersion = hardwareVersion; }
        
        public Double getLocationLat() { return locationLat; }
        public void setLocationLat(Double locationLat) { this.locationLat = locationLat; }
        
        public Double getLocationLng() { return locationLng; }
        public void setLocationLng(Double locationLng) { this.locationLng = locationLng; }
        
        public String getLocationDescription() { return locationDescription; }
        public void setLocationDescription(String locationDescription) { this.locationDescription = locationDescription; }
    }
}
