# Industrial IoT Device Management Platform

Bu proje, endüstriyel IoT cihazlarının yönetimi için geliştirilmiş kapsamlı bir platformdur. Spring Boot ve Java kullanılarak oluşturulmuş olup, MQTT üzerinden telemetri verisi alımı, REST ve GraphQL API'leri, gerçek zamanlı WebSocket iletişimi ve kullanıcı yönetimi özelliklerini içerir.

## 🚀 Özellikler

### 🔧 Temel Özellikler
- **MQTT Telemetri Alımı**: IoT cihazlarından MQTT protokolü ile telemetri verisi alımı
- **PostgreSQL Veritabanı**: Telemetri verilerinin ve cihaz bilgilerinin saklanması
- **REST API**: Cihaz yönetimi için kapsamlı REST endpoint'leri
- **GraphQL API**: Esnek veri sorgulama ve yönetim
- **WebSocket**: Gerçek zamanlı telemetri verisi akışı
- **Spring Security**: JWT tabanlı kimlik doğrulama ve yetkilendirme
- **Rol Tabanlı Güvenlik**: ADMIN, USER, OPERATOR rolleri

### 📊 Cihaz Yönetimi
- Cihaz kayıt ve güncelleme
- Cihaz durumu takibi (ONLINE, OFFLINE, MAINTENANCE, ERROR, DISABLED)
- Cihaz tipi yönetimi (SENSOR, ACTUATOR, GATEWAY, CONTROLLER, CAMERA)
- Cihaz konum bilgileri
- Cihaz sahipliği ve yetkilendirme

### 📈 Telemetri Verisi
- Gerçek zamanlı veri alımı
- Zaman aralığına göre veri sorgulama
- Veri analizi (ortalama, minimum, maksimum değerler)
- Veri kalitesi takibi
- Otomatik veri temizleme

### 🔐 Güvenlik
- JWT token tabanlı kimlik doğrulama
- BCrypt şifre hash'leme
- Rol tabanlı erişim kontrolü
- CORS konfigürasyonu
- Güvenli API endpoint'leri

## 🛠️ Teknolojiler

- **Java 17**
- **Spring Boot 3.5.4**
- **Spring Security**
- **Spring Data JPA**
- **Spring WebSocket (STOMP)**
- **Spring GraphQL**
- **PostgreSQL**
- **MQTT (Eclipse Paho)**
- **JWT (jjwt)**
- **Maven**

## 📋 Gereksinimler

- Java 17 veya üzeri
- Maven 3.6+
- PostgreSQL 12+
- MQTT Broker (Mosquitto, HiveMQ, vb.)

## 🚀 Kurulum

### 1. Veritabanı Kurulumu

PostgreSQL veritabanında `iot_platform` adında bir veritabanı oluşturun:

```sql
CREATE DATABASE iot_platform;
```

### 2. Uygulama Konfigürasyonu

`src/main/resources/application.properties` dosyasını kendi ortamınıza göre düzenleyin:

```properties
# PostgreSQL Veritabanı
spring.datasource.url=jdbc:postgresql://localhost:5432/iot_platform
spring.datasource.username=your_username
spring.datasource.password=your_password

# MQTT Broker
mqtt.broker.url=tcp://localhost:1883
mqtt.client.id=iot-platform-server

# JWT Secret
jwt.secret=your-secret-key-here-make-it-long-and-secure-in-production
```

### 3. Uygulamayı Çalıştırma

```bash
# Projeyi derleme
mvn clean install

# Uygulamayı çalıştırma
mvn spring-boot:run
```

Uygulama varsayılan olarak `http://localhost:8080` adresinde çalışacaktır.

## 📚 API Dokümantasyonu

### REST API Endpoint'leri

#### Kimlik Doğrulama
- `POST /api/auth/register` - Kullanıcı kaydı
- `POST /api/auth/login` - Kullanıcı girişi
- `POST /api/auth/validate` - Token doğrulama
- `GET /api/auth/me` - Mevcut kullanıcı bilgileri

#### Cihaz Yönetimi
- `GET /api/devices` - Tüm cihazları listeleme
- `GET /api/devices/{id}` - Cihaz detayları
- `POST /api/devices` - Yeni cihaz oluşturma
- `PUT /api/devices/{id}` - Cihaz güncelleme
- `DELETE /api/devices/{id}` - Cihaz silme
- `GET /api/devices/my-devices` - Kullanıcının cihazları
- `GET /api/devices/online` - Online cihazlar
- `GET /api/devices/statistics` - Cihaz istatistikleri

#### Telemetri Verisi
- `GET /api/telemetry/device/{deviceId}` - Cihaz telemetri verileri
- `GET /api/telemetry/device/{deviceId}/latest` - En son telemetri verisi
- `GET /api/telemetry/device/{deviceId}/timerange` - Zaman aralığı telemetri verileri
- `GET /api/telemetry/device/{deviceId}/average` - Ortalama değer hesaplama

### GraphQL Endpoint'i

GraphQL endpoint'i: `http://localhost:8080/graphql`
GraphiQL arayüzü: `http://localhost:8080/graphiql`

#### Örnek GraphQL Sorguları

```graphql
# Kullanıcı bilgilerini alma
query {
  me {
    id
    username
    email
    role
    devices {
      id
      deviceId
      name
      status
    }
  }
}

# Cihazları listeleme
query {
  devices {
    id
    deviceId
    name
    deviceType
    status
    owner {
      username
    }
  }
}

# Telemetri verilerini alma
query {
  telemetryData(deviceId: "1", limit: 10) {
    id
    timestamp
    dataType
    valueNumeric
    unit
  }
}
```

### WebSocket Endpoint'i

WebSocket bağlantı noktası: `ws://localhost:8080/ws`

#### Telemetri Verisi Aboneliği
```javascript
// SockJS ve STOMP kullanarak bağlantı
const socket = new SockJS('/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function (frame) {
    // Belirli cihazın telemetri verilerini dinleme
    stompClient.subscribe('/topic/telemetry/device-001', function (message) {
        const telemetryData = JSON.parse(message.body);
        console.log('Yeni telemetri verisi:', telemetryData);
    });
});
```

## 🔧 MQTT Konfigürasyonu

### Topic Yapısı
- `iot/{deviceId}/telemetry` - Telemetri verisi gönderimi
- `iot/{deviceId}/status` - Cihaz durumu güncellemesi
- `iot/{deviceId}/command` - Cihaza komut gönderimi

### Örnek MQTT Mesajı
```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "dataType": "temperature",
  "value": 25.5,
  "unit": "°C",
  "quality": 100
}
```

## 🧪 Test

### Unit Testler
```bash
mvn test
```

### Integration Testler
```bash
mvn verify
```

## 📊 Monitoring

### Actuator Endpoint'leri
- `GET /actuator/health` - Uygulama sağlık durumu
- `GET /actuator/info` - Uygulama bilgileri
- `GET /actuator/metrics` - Metrikler

## 🔒 Güvenlik

### Roller ve Yetkiler
- **ADMIN**: Tüm işlemleri yapabilir
- **USER**: Kendi cihazlarını yönetebilir
- **OPERATOR**: Cihazları görüntüleyebilir, telemetri verilerini okuyabilir

### JWT Token Kullanımı
```bash
# Login isteği
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"user@example.com","password":"password"}'

# Token ile API çağrısı
curl -X GET http://localhost:8080/api/devices \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 🚀 Deployment

### Docker ile Deployment
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/iot-device-management-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

### Docker Compose
```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/iot_platform
    depends_on:
      - db
      - mqtt
  
  db:
    image: postgres:13
    environment:
      POSTGRES_DB: iot_platform
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: password
    volumes:
      - postgres_data:/var/lib/postgresql/data
  
  mqtt:
    image: eclipse-mosquitto:latest
    ports:
      - "1883:1883"
    volumes:
      - ./mosquitto.conf:/mosquitto/config/mosquitto.conf

volumes:
  postgres_data:
```

## 🤝 Katkıda Bulunma

1. Fork yapın
2. Feature branch oluşturun (`git checkout -b feature/amazing-feature`)
3. Değişikliklerinizi commit edin (`git commit -m 'Add some amazing feature'`)
4. Branch'inizi push edin (`git push origin feature/amazing-feature`)
5. Pull Request oluşturun

## 📄 Lisans

Bu proje MIT lisansı altında lisanslanmıştır. Detaylar için `LICENSE` dosyasına bakın.

## 📞 İletişim

Proje hakkında sorularınız için:
- Email: your-email@example.com
- GitHub Issues: [Proje Issues Sayfası](https://github.com/yourusername/iot-device-management/issues)

## 🙏 Teşekkürler

Bu proje aşağıdaki açık kaynak projelerin kullanımı ile mümkün olmuştur:
- Spring Boot
- Spring Security
- PostgreSQL
- Eclipse Paho MQTT Client
- GraphQL Java Tools
