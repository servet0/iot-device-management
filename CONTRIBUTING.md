# Contributing to Industrial IoT Device Management Platform

Thank you for your interest in contributing to our Industrial IoT Device Management Platform! This document provides guidelines and information for contributors.

## 🤝 How to Contribute

### Reporting Bugs
- Use the GitHub issue tracker
- Provide detailed information about the bug
- Include steps to reproduce the issue
- Mention your environment (OS, Java version, etc.)

### Suggesting Enhancements
- Create a feature request issue
- Describe the enhancement clearly
- Explain the benefits and use cases
- Consider implementation complexity

### Code Contributions
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## 🛠️ Development Setup

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Git

### Local Development
```bash
# Clone your fork
git clone https://github.com/yourusername/iot-device-management.git
cd iot-device-management

# Add upstream remote
git remote add upstream https://github.com/original-owner/iot-device-management.git

# Create a feature branch
git checkout -b feature/your-feature-name

# Run the application
.\mvnw.cmd spring-boot:run
```

## 📝 Code Style Guidelines

### Java Code Style
- Follow Java coding conventions
- Use meaningful variable and method names
- Add comprehensive comments for complex logic
- Keep methods focused and concise
- Use appropriate access modifiers

### Spring Boot Best Practices
- Use dependency injection
- Follow Spring Boot conventions
- Use appropriate annotations
- Implement proper exception handling
- Follow REST API design principles

### Database and JPA
- Use meaningful entity names
- Implement proper relationships
- Add appropriate indexes
- Use DTOs for data transfer
- Implement proper validation

## 🧪 Testing Guidelines

### Unit Tests
- Write tests for all new functionality
- Use descriptive test method names
- Follow AAA pattern (Arrange, Act, Assert)
- Mock external dependencies
- Aim for high test coverage

### Integration Tests
- Test API endpoints
- Test database operations
- Test security configurations
- Use test profiles

### Running Tests
```bash
# Run all tests
.\mvnw.cmd test

# Run specific test class
.\mvnw.cmd test -Dtest=DeviceServiceTest

# Run with coverage
.\mvnw.cmd jacoco:report
```

## 🔒 Security Guidelines

### Authentication & Authorization
- Never commit sensitive data
- Use environment variables for secrets
- Implement proper input validation
- Follow OWASP guidelines
- Test security configurations

### Data Protection
- Validate all inputs
- Sanitize data before storage
- Implement proper error handling
- Use HTTPS in production
- Follow GDPR compliance

## 📚 Documentation

### Code Documentation
- Add JavaDoc for public methods
- Document complex algorithms
- Explain business logic
- Update README for new features

### API Documentation
- Document all REST endpoints
- Provide GraphQL schema documentation
- Include request/response examples
- Update API documentation for changes

## 🚀 Pull Request Process

### Before Submitting
1. Ensure all tests pass
2. Update documentation
3. Follow code style guidelines
4. Add appropriate labels
5. Write a clear description

### Pull Request Template
```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
- [ ] Unit tests pass
- [ ] Integration tests pass
- [ ] Manual testing completed

## Checklist
- [ ] Code follows style guidelines
- [ ] Self-review completed
- [ ] Documentation updated
- [ ] No breaking changes
```

## 🏷️ Issue Labels

- `bug` - Something isn't working
- `enhancement` - New feature or request
- `documentation` - Improvements to documentation
- `good first issue` - Good for newcomers
- `help wanted` - Extra attention is needed
- `priority: high` - High priority issue
- `priority: low` - Low priority issue

## 📞 Getting Help

### Communication Channels
- GitHub Issues for bug reports and feature requests
- GitHub Discussions for questions and ideas
- Pull Request reviews for code feedback

### Resources
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [GraphQL Documentation](https://graphql.org/)
- [MQTT Documentation](https://mqtt.org/)
- [JPA Documentation](https://docs.oracle.com/javaee/6/tutorial/doc/bnbpz.html)

## 🙏 Recognition

Contributors will be recognized in:
- Project README
- Release notes
- Contributor hall of fame

## 📄 License

By contributing to this project, you agree that your contributions will be licensed under the MIT License.

---

Thank you for contributing to the Industrial IoT Device Management Platform! 🚀
