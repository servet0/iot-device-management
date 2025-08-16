# Industrial IoT Device Management Platform

A comprehensive, enterprise-grade Industrial IoT Device Management Platform built with Spring Boot, providing real-time device monitoring, telemetry data collection, and management capabilities through REST and GraphQL APIs.

## 🚀 Features

### Core Functionality
- **Device Management**: Complete CRUD operations for IoT devices
- **Real-time Telemetry**: MQTT-based telemetry data collection and storage
- **Dual API Support**: Both REST and GraphQL APIs for flexible integration
- **Real-time Streaming**: WebSocket support for live telemetry data
- **User Management**: Role-based security with JWT authentication
- **Database Support**: PostgreSQL (production) and H2 (development/testing)

### Technical Stack
- **Backend**: Spring Boot 3.5.4, Java 17
- **Database**: PostgreSQL, H2 (in-memory)
- **Messaging**: MQTT (Eclipse Paho)
- **APIs**: REST API, GraphQL (Spring GraphQL)
- **Real-time**: WebSocket (STOMP, SockJS)
- **Security**: Spring Security, JWT
- **Validation**: Bean Validation
- **Monitoring**: Spring Boot Actuator

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL (for production)
- MQTT Broker (optional, for telemetry)

## 🛠️ Installation & Setup

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/iot-device-management.git
cd iot-device-management
```

### 2. Configuration

#### Development Environment (H2 Database)
The application is configured to run with H2 in-memory database by default for development:

```properties
# Default configuration in application.properties
spring.datasource.url=jdbc:h2:mem:iotdb
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop
```

#### Production Environment (PostgreSQL)
For production, use PostgreSQL configuration:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/iot_platform
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

### 3. Running the Application

#### Using Maven Wrapper (Recommended)
```bash
# Development mode (H2)
.\mvnw.cmd spring-boot:run

# With specific profile
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev

# Production mode (PostgreSQL)
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=prod
```

#### Using Docker
```bash
# Build the application
docker build -t iot-device-management .

# Run with Docker Compose (includes PostgreSQL, MQTT, Redis, Nginx)
docker-compose up -d
```

## 🏗️ Project Structure

```
src/
├── main/
│   ├── java/com/yourcompany/iotplatform/iot_device_management/
│   │   ├── config/           # Configuration classes
│   │   ├── controller/       # REST API controllers
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── entity/          # JPA entities
│   │   ├── exception/       # Custom exceptions
│   │   ├── graphql/         # GraphQL resolvers
│   │   ├── repository/      # Data repositories
│   │   ├── service/         # Business logic services
│   │   └── websocket/       # WebSocket configuration
│   └── resources/
│       ├── application.properties      # Default configuration
│       ├── application-dev.properties  # Development profile
│       ├── application-test.properties # Test profile
│       └── graphql/
│           └── schema.graphqls         # GraphQL schema
```

## 🔌 API Documentation

### REST API Endpoints

#### Device Management
- `GET /api/devices` - List all devices
- `GET /api/devices/{id}` - Get device by ID
- `POST /api/devices` - Create new device
- `PUT /api/devices/{id}` - Update device
- `DELETE /api/devices/{id}` - Delete device

#### User Management
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `GET /api/users` - List users (admin only)

#### Telemetry Data
- `GET /api/telemetry/device/{deviceId}` - Get device telemetry
- `GET /api/telemetry/aggregate` - Get aggregated telemetry data

### GraphQL API

#### Queries
```graphql
# Get all devices
query {
  devices {
    id
    name
    type
    status
    telemetryData {
      temperature
      humidity
      timestamp
    }
  }
}

# Get device by ID
query {
  device(id: "1") {
    id
    name
    type
    status
  }
}
```

#### Mutations
```graphql
# Create device
mutation {
  createDevice(device: {
    name: "Sensor-001"
    type: "TEMPERATURE"
    location: "Building A"
  }) {
    id
    name
    status
  }
}

# User login
mutation {
  login(credentials: {
    username: "admin"
    password: "password"
  }) {
    token
    user {
      id
      username
      role
    }
  }
}
```

#### Subscriptions
```graphql
# Real-time telemetry updates
subscription {
  telemetryUpdate(deviceId: "1") {
    temperature
    humidity
    timestamp
  }
}
```

## 🔐 Security

### Authentication
- JWT-based authentication
- Role-based access control (USER, ADMIN)
- Password encryption with BCrypt

### Authorization
- `@PreAuthorize` annotations for method-level security
- GraphQL field-level security
- REST API endpoint protection

## 📊 Monitoring & Health Checks

### Actuator Endpoints
- `GET /actuator/health` - Application health status
- `GET /actuator/info` - Application information
- `GET /actuator/metrics` - Application metrics

### Database Console
- H2 Console: `http://localhost:8080/h2-console` (development only)

## 🧪 Testing

### Running Tests
```bash
# Run all tests
.\mvnw.cmd test

# Run with specific profile
.\mvnw.cmd test -Dspring-boot.run.profiles=test

# Run integration tests
.\mvnw.cmd verify
```

### Test Configuration
- H2 in-memory database for tests
- Mock MQTT broker
- Test data initialization

## 🐳 Docker Deployment

### Docker Compose Setup
The project includes a complete Docker Compose configuration:

```yaml
services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: iot_platform
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: password
    ports:
      - "5432:5432"

  mqtt:
    image: eclipse-mosquitto:latest
    ports:
      - "1883:1883"
      - "9001:9001"

  redis:
    image: redis:alpine
    ports:
      - "6379:6379"

  app:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - postgres
      - mqtt
      - redis

  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
    depends_on:
      - app
```

### Environment Variables
Create a `.env` file based on `env.example`:

```bash
# Database
POSTGRES_PASSWORD=your_secure_password
POSTGRES_DB=iot_platform

# MQTT
MQTT_BROKER_URL=tcp://mqtt:1883

# Application
JWT_SECRET=your_jwt_secret_key
APP_PORT=8080
```

## 🔧 Configuration Profiles

### Development Profile (`dev`)
- H2 in-memory database
- MQTT disabled
- Debug logging enabled
- H2 console enabled

### Test Profile (`test`)
- H2 in-memory database
- Mock services
- Test data initialization

### Production Profile (`prod`)
- PostgreSQL database
- MQTT enabled
- Production logging
- Security hardening

## 📈 Performance & Scalability

### Database Optimization
- Connection pooling with HikariCP
- JPA query optimization
- Indexed database fields

### Caching
- Redis integration for session management
- Application-level caching
- Query result caching

### Monitoring
- Spring Boot Actuator metrics
- Custom health indicators
- Performance monitoring

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Style
- Follow Java coding conventions
- Use meaningful variable and method names
- Add comprehensive comments for complex logic
- Write unit tests for new features

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

### Common Issues

#### ANTLR Version Conflict
If you encounter ANTLR version conflicts:
```bash
.\mvnw.cmd dependency:purge-local-repository -DmanualInclude="org.antlr"
.\mvnw.cmd clean
.\mvnw.cmd compile
```

#### Database Connection Issues
- Ensure PostgreSQL is running (production)
- Check database credentials
- Verify network connectivity

#### MQTT Connection Issues
- Ensure MQTT broker is running
- Check broker URL and credentials
- Verify firewall settings

### Getting Help
- Create an issue on GitHub
- Check existing issues for solutions
- Review the documentation

## 🗺️ Roadmap

- [ ] Kubernetes deployment support
- [ ] Advanced analytics dashboard
- [ ] Device firmware management
- [ ] Multi-tenant architecture
- [ ] Advanced alerting system
- [ ] Data export capabilities
- [ ] Mobile application
- [ ] Machine learning integration

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- Eclipse Paho for MQTT client library
- GraphQL Java team for GraphQL support
- PostgreSQL community for the robust database

---

**Built with ❤️ using Spring Boot and Java**
