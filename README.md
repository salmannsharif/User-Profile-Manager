# User Profile Manager

A robust and secure Spring Boot application for managing user profiles with authentication, image uploads, and PDF report generation capabilities.

## Features

- **User Authentication**
  - JWT-based authentication
  - Secure password hashing with BCrypt
  - Role-based access control (USER/ADMIN)

- **Profile Management**
  - Create, read, update, and delete user profiles
  - Upload and manage profile pictures
  - Two API versions (v1 and v2) with different response formats

- **Reporting**
  - Generate PDF reports of user profiles
  - Export user data in paginated format
  - Download comprehensive user reports

- **Security**
  - Input validation
  - Secure file uploads
  - Protection against common web vulnerabilities

## Tech Stack

- **Backend**: Spring Boot 3.x
- **Database**: PostgreSQL
- **Authentication**: JWT (JSON Web Tokens)
- **Document Generation**: iText PDF
- **Build Tool**: Maven
- **Java Version**: 17+

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+
- Git

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/UserProfileManager.git
cd UserProfileManager
```

### 2. Database Setup

1. Create a PostgreSQL database named `user_profile_db`
2. Update the database credentials in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/user_profile_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. Build and Run

```bash
mvn clean install
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Documentation

### Authentication

- **Login**
  - `POST /api/auth/login` - Authenticate and get JWT token

### User Profile API (v1 - Full Profile with Image)

- `POST /v1/api/profiles` - Create a new profile with image
- `GET /v1/api/profiles` - Get all profiles (paginated)
- `GET /v1/api/profiles/{id}` - Get profile by ID
- `PUT /v1/api/profiles/{id}` - Update profile
- `DELETE /v1/api/profiles/{id}` - Delete profile

### User Profile API (v2 - Simplified Response)

- `POST /v2/api/profiles` - Create a new profile (name and email only)
- `GET /v2/api/profiles` - Get all profiles (simplified response)
- `GET /v2/api/profiles/{id}` - Get profile by ID (simplified response)
- `DELETE /v2/api/profiles/{id}` - Delete profile

### Report Generation

- `GET /api/profiles/pdf` - Generate PDF report (paginated)
- `GET /api/profiles/pdf/all` - Generate PDF report of all users

## Request/Response Examples

### Authentication

**Request:**
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "user@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### Create Profile (v1)

**Request:**
```http
POST /v1/api/profiles
Content-Type: multipart/form-data
Authorization: Bearer 

profile: {
  "name": "John Doe",
  "email": "john@example.com",
  "password": "secure123",
  "address": "123 Main St"
}
image: [profile-image.jpg]
```

## Security

- All endpoints except `/api/auth/login` require authentication
- Passwords are hashed using BCrypt
- JWT tokens expire after a set period
- Input validation is implemented for all user inputs
- File uploads are restricted to 5MB

## Error Handling

The API returns appropriate HTTP status codes and error messages in the following format:

```json
{
  "timestamp": "2025-01-01T12:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Profile with ID 999 not found"
}
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Spring Boot Team
- PostgreSQL Team
- iText PDF Library
