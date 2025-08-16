package com.yourcompany.iotplatform.iot_device_management.graphql;

import com.yourcompany.iotplatform.iot_device_management.model.Device;
import com.yourcompany.iotplatform.iot_device_management.model.User;
import com.yourcompany.iotplatform.iot_device_management.service.DeviceService;
import com.yourcompany.iotplatform.iot_device_management.service.UserService;
import graphql.kickstart.tools.GraphQLResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * GraphQL User Resolver
 * User tipi için GraphQL resolver işlemleri
 */
@Component
public class UserResolver implements GraphQLResolver<User> {
    
    @Autowired
    private DeviceService deviceService;
    
    /**
     * Kullanıcının cihazlarını getirme
     * @param user Kullanıcı
     * @return Kullanıcının cihazları
     */
    public List<Device> devices(User user) {
        return deviceService.getDevicesByOwner(user.getId());
    }
}
