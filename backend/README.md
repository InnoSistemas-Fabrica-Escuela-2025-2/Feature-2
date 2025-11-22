# Backend

This directory contains the server-side application logic for the project and task management system.

## 🏗️ Architecture

The backend is built using a microservices architecture with the following services:

- **Gateway Service** (Port 8080): API Gateway with Spring Cloud Gateway for routing and CORS
- **Authenticator Service** (Port 8081): Authentication and JWT token management
- **Innosistemas Service** (Port 8082): Core project and task management features
- **PostgreSQL Database**: Shared database for all services

## 🚀 Features

- Project management APIs
- Task creation and assignment
- User authentication and authorization with JWT
- Progress tracking
- Deadline management
- Team collaboration features
- Health checks for monitoring
- Dockerized for easy deployment

## 🛠️ Technology Stack

- **Framework**: Spring Boot 3.5+
- **Language**: Java 21
- **Database**: PostgreSQL
- **API Gateway**: Spring Cloud Gateway
- **Security**: Spring Security + JWT
- **ORM**: Spring Data JPA / Hibernate
- **Containerization**: Docker & Docker Compose
- **Monitoring**: Spring Boot Actuator

## 📁 Structure

```
backend/
├── authenticator/          # Authentication service
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── innosistemas/          # Core business logic service
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── gateway/               # API Gateway
│   ├── src/
│   ├── application.yml
│   ├── Dockerfile
│   └── pom.xml
├── docker-compose.yml     # Docker orchestration
├── .env                   # Environment variables
├── docker-helper.ps1      # Interactive Docker helper
└── start.ps1             # Quick start script
```

## 🐳 Quick Start with Docker (Recommended)

### Prerequisites
- Docker Desktop installed and running
- 4GB+ RAM available for Docker

### Option 1: Quick Start Script
```powershell
cd backend
.\start.ps1
```

### Option 2: Manual Docker Compose
```bash
cd backend

# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

### Option 3: Interactive Helper
```powershell
cd backend
.\docker-helper.ps1
```

The services will be available at:
- **Gateway**: http://localhost:8080
- **Authenticator**: http://localhost:8081
- **Innosistemas**: http://localhost:8082
- **PostgreSQL**: localhost:5432

## 🔧 Development Setup (Without Docker)

### Prerequisites
- Java 21 or higher
- Maven 3.9+
- PostgreSQL 16+

### Install Dependencies

```bash
# Authenticator
cd authenticator
mvnw clean install

# Innosistemas
cd ../innosistemas
mvnw clean install

# Gateway
cd ../gateway
mvnw clean install
```

### Configure Database

1. Create a PostgreSQL database:
```sql
CREATE DATABASE innosistemas_db;
CREATE USER innosistemas_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE innosistemas_db TO innosistemas_user;
```

2. Update `.env` file with your database credentials

### Run Services

```bash
# Terminal 1 - Authenticator
cd authenticator
mvnw spring-boot:run

# Terminal 2 - Innosistemas
cd innosistemas
mvnw spring-boot:run

# Terminal 3 - Gateway
cd gateway
mvnw spring-boot:run
```

## 🌐 API Endpoints

All requests go through the Gateway at `http://localhost:8080`

### Authentication
- `POST /authenticator/login` - User login
- `POST /authenticator/register` - User registration
- `GET /authenticator/validate` - Validate JWT token

### Projects
- `GET /project/all` - List all projects
- `POST /project/save` - Create new project
- `PUT /project/update/{id}` - Update project
- `DELETE /project/delete/{id}` - Delete project

### Tasks
- `GET /project/task/all` - List all tasks
- `POST /project/task/save` - Create new task
- `PUT /project/task/update/{id}` - Update task
- `DELETE /project/task/delete/{id}` - Delete task

### Health Checks
- `GET /actuator/health` - Service health status

## 🔐 Environment Variables

See `.env.example` for all available variables. Key variables:

| Variable | Description | Default |
|----------|-------------|---------|
| `PORT` | Service port | 8080/8081/8082 |
| `JWT_SECRET` | Secret for JWT signing | (required) |
| `SPRING_DATASOURCE_URL` | Database connection URL | (required) |
| `ALLOWED_ORIGINS` | CORS allowed origins | http://localhost:5173 |

## 🧪 Testing

```bash
# Run tests for all services
cd authenticator && mvnw test
cd ../innosistemas && mvnw test
cd ../gateway && mvnw test

# Run specific test
mvnw test -Dtest=TestClassName
```

## ☁️ Deployment

### Deploy to Render

See [DOCKER_DEPLOYMENT.md](./DOCKER_DEPLOYMENT.md) for detailed Docker deployment instructions.

See [RENDER_DEPLOYMENT.md](../RENDER_DEPLOYMENT.md) for step-by-step Render deployment guide.

Quick steps:
1. Push code to GitHub
2. Create PostgreSQL database on Render
3. Deploy each service (authenticator, innosistemas, gateway)
4. Configure environment variables
5. Verify health checks

## 📊 Monitoring

All services expose actuator endpoints:

- Health: `http://localhost:8080/actuator/health`
- Info: `http://localhost:8080/actuator/info`

## 🐛 Troubleshooting

### Services won't start
1. Check Docker is running: `docker info`
2. Check ports are available: `netstat -ano | findstr "8080 8081 8082"`
3. View logs: `docker-compose logs [service-name]`

### Database connection errors
1. Verify PostgreSQL is running
2. Check credentials in `.env`
3. Ensure database exists

### Build failures
1. Clean Maven cache: `mvnw clean`
2. Update dependencies: `mvnw dependency:resolve`
3. Check Java version: `java -version` (should be 21+)

## 📚 Additional Documentation

- [Docker Deployment Guide](./DOCKER_DEPLOYMENT.md)
- [Render Deployment Checklist](../RENDER_DEPLOYMENT.md)
- [API Documentation](#) (Coming soon)

## 🤝 Contributing

1. Create a feature branch
2. Make your changes
3. Run tests
4. Submit a pull request

## 📝 License

[Your License Here]