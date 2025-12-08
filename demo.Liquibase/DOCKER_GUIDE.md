# Docker Setup Guide for Liquibase Project

## Overview

This guide explains how to run your Spring Boot Liquibase project using Docker and Docker Compose.

### What's Included

1. **Dockerfile** - Multi-stage build for optimized image
2. **docker-compose.yml** - MySQL + Application + phpMyAdmin
3. **docker-compose.dev.yml** - Development with H2 (simpler, no DB needed)
4. **docker-compose.postgresql.yml** - PostgreSQL + Application + pgAdmin

---

## Prerequisites

- Docker installed ([Download Docker Desktop](https://www.docker.com/products/docker-desktop))
- Docker Compose (included with Docker Desktop)
- 4GB+ available RAM
- Port 8080 available (or change in compose file)

### Verify Installation

```bash
docker --version
docker-compose --version
```

---

## Quick Start Options

### Option 1: Development (H2 Database - RECOMMENDED FOR BEGINNERS)

**Simplest setup - no external database needed**

```bash
# Navigate to project
cd demo.Liquibase

# Start with H2 (in-memory database)
docker-compose -f docker-compose.dev.yml up --build

# In another terminal, test the API
curl http://localhost:8080/api/users
```

**Advantages:**
- ✅ No database setup needed
- ✅ Fastest to get running
- ✅ Perfect for learning
- ✅ Data resets on restart

**Access:**
- Application: http://localhost:8080
- H2 Console: http://localhost:8080/h2-console

---

### Option 2: Production (MySQL + phpMyAdmin)

**Full production-like setup with MySQL database**

```bash
# Navigate to project
cd demo.Liquibase

# Start everything
docker-compose up --build

# Monitor logs
docker-compose logs -f app

# Test the API (in another terminal)
curl http://localhost:8080/api/users
```

**Access:**
- Application: http://localhost:8080/api/users
- phpMyAdmin: http://localhost:8081 (user: root, pass: root_password)
- MySQL: localhost:3306 (user: root, pass: root_password)

---

### Option 3: Production (PostgreSQL + pgAdmin)

**Production-ready setup with PostgreSQL**

```bash
# Navigate to project
cd demo.Liquibase

# Start with PostgreSQL
docker-compose -f docker-compose.postgresql.yml up --build

# Test the API
curl http://localhost:8080/api/users
```

**Access:**
- Application: http://localhost:8080/api/users
- pgAdmin: http://localhost:5050 (user: admin@example.com, pass: admin_password)
- PostgreSQL: localhost:5432 (user: liquibase_user, pass: liquibase_password)

---

## Common Docker Commands

### Start Services
```bash
# Start all services (default: docker-compose.yml)
docker-compose up

# Start in background
docker-compose up -d

# Start with specific compose file
docker-compose -f docker-compose.postgresql.yml up

# Build images (if changes made)
docker-compose up --build

# Rebuild without cache
docker-compose up --build --force-recreate
```

### Stop Services
```bash
# Stop all services (containers still exist)
docker-compose stop

# Stop and remove containers, networks
docker-compose down

# Remove everything including volumes (DATA LOSS!)
docker-compose down -v
```

### View Logs
```bash
# View all logs
docker-compose logs

# Follow logs in real-time
docker-compose logs -f

# View logs for specific service
docker-compose logs -f app

# View last 100 lines
docker-compose logs --tail=100 app
```

### Manage Services
```bash
# List running containers
docker-compose ps

# Execute command in container
docker-compose exec app bash

# Restart service
docker-compose restart app

# View resource usage
docker stats
```

### Database Access
```bash
# Access MySQL CLI
docker-compose exec mysql mysql -u root -p

# Access PostgreSQL CLI
docker-compose -f docker-compose.postgresql.yml exec postgres psql -U liquibase_user -d liquibase_db

# View database files (MySQL)
docker volume ls
docker volume inspect demo.Liquibase_mysql_data
```

---

## Testing the Application

### Test with curl

```bash
# Health check
curl http://localhost:8080/actuator/health

# Create user
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "secure123",
    "firstName": "John",
    "lastName": "Doe"
  }'

# Get all users
curl http://localhost:8080/api/users

# Get user by ID
curl http://localhost:8080/api/users/1

# Update user
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Smith"
  }'

# Delete user
curl -X DELETE http://localhost:8080/api/users/1
```

### Test with Postman

1. Open Postman
2. Create new request
3. Use curl examples above (convert to Postman format)
4. Send requests

---

## Understanding Docker Compose Files

### docker-compose.yml (MySQL)

```yaml
services:
  app:               # Spring Boot application
    build: .
    ports:
      - "8080:8080"  # Map container port 8080 to localhost 8080
    depends_on:
      - mysql        # Wait for MySQL to be healthy before starting
    
  mysql:             # MySQL database
    image: mysql:8.0
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql  # Persistent storage
    
  phpmyadmin:        # Database management UI
    image: phpmyadmin
    ports:
      - "8081:80"
```

### Key Concepts

**services:** Containers to run
**build:** Build from Dockerfile
**image:** Use pre-built image
**ports:** Map host:container ports
**environment:** Set environment variables
**depends_on:** Service dependencies
**volumes:** Data persistence
**networks:** Container networking
**healthcheck:** Verify service is healthy

---

## Troubleshooting

### Issue: Port Already in Use
```bash
# Find what's using port 8080
netstat -ano | findstr :8080

# Change port in docker-compose.yml
# Change "8080:8080" to "8081:8080" for example
```

### Issue: Application Won't Start
```bash
# View logs
docker-compose logs -f app

# Check if database is ready
docker-compose ps

# If database isn't healthy, wait and try again
docker-compose logs -f mysql
```

### Issue: Database Connection Error
```bash
# Check if MySQL container is running
docker-compose ps

# Verify environment variables match
docker-compose config | grep DATASOURCE

# Check MySQL logs
docker-compose logs -f mysql
```

### Issue: Out of Memory
```bash
# Docker Desktop settings → Resources → Memory → increase
# Or stop other containers: docker-compose down
```

### Issue: Can't Access Database UI
- phpMyAdmin: http://localhost:8081
- pgAdmin: http://localhost:5050
- Check if containers are running: docker-compose ps

---

## Data Persistence

### MySQL (docker-compose.yml)
```yaml
volumes:
  mysql_data:  # Named volume persists data between restarts
```

Data is stored in Docker volume. To see it:
```bash
docker volume ls
docker volume inspect demo.liquibase_mysql_data
```

### PostgreSQL (docker-compose.postgresql.yml)
```yaml
volumes:
  postgres_data:  # Named volume persists data
```

### H2 (docker-compose.dev.yml)
```
# In-memory database - data is LOST when container stops
# Use only for development
```

---

## Environment Variables

### Customizing Database Credentials

Edit the compose file and change:
```yaml
environment:
  SPRING_DATASOURCE_USERNAME: root
  SPRING_DATASOURCE_PASSWORD: root_password
  MYSQL_ROOT_PASSWORD: root_password
```

### Using .env File

Create `.env` file in project root:
```env
DB_ROOT_PASSWORD=secure_password
DB_USER=liquibase_user
DB_PASSWORD=secure_password
APP_PORT=8080
```

Use in compose file:
```yaml
environment:
  SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD}
```

---

## Performance Optimization

### Resource Limits
```yaml
services:
  app:
    deploy:
      resources:
        limits:
          cpus: '1'
          memory: 1G
        reservations:
          cpus: '0.5'
          memory: 512M
```

### Multi-stage Build
The Dockerfile uses multi-stage build to reduce image size:
- Stage 1: Build with Maven (uses maven image - large)
- Stage 2: Runtime with JRE only (small)

Result: ~500MB image (vs 1GB+ with single stage)

---

## Cleanup

### Remove Stopped Containers
```bash
docker-compose down
```

### Remove Unused Resources
```bash
docker system prune
```

### Complete Cleanup (including data!)
```bash
docker-compose down -v  # Remove volumes too!
```

⚠️ **Warning:** `docker-compose down -v` deletes database data!

---

## Deployment Tips

### Development
- Use: `docker-compose.dev.yml` with H2
- Fast startup, no database setup

### Testing
- Use: `docker-compose.yml` with MySQL
- Matches production closely
- Database persists between tests

### Production
- Use: `docker-compose.postgresql.yml` with PostgreSQL
- More robust and scalable
- Consider using Docker Swarm or Kubernetes
- Add volume backups
- Use strong passwords
- Don't expose ports directly

---

## Example: Complete Development Workflow

```bash
# 1. Clone/navigate to project
cd demo.Liquibase

# 2. Start development environment
docker-compose -f docker-compose.dev.yml up --build

# 3. Wait for startup (watch logs for "Started Application")

# 4. In another terminal, test the API
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"pass123"}'

# 5. View H2 console
# Open browser: http://localhost:8080/h2-console

# 6. Check logs
docker-compose logs -f app

# 7. Stop when done
docker-compose down

# 8. Start again (data preserved if not using -v)
docker-compose -f docker-compose.dev.yml up
```

---

## Docker Best Practices

✅ **Do:**
- Use specific image versions (not `latest`)
- Implement health checks
- Use named volumes for persistence
- Keep Dockerfile simple
- Use multi-stage builds
- Document environment variables

❌ **Don't:**
- Run as root in containers
- Hardcode secrets in images
- Use very large base images
- Ignore security vulnerabilities
- Skip healthchecks
- Use `docker-compose down -v` in production

---

## Next Steps

1. **Start with dev environment:** `docker-compose -f docker-compose.dev.yml up`
2. **Test the API:** Use curl examples above
3. **Explore database:** View database through UI (phpMyAdmin/pgAdmin)
4. **Add more tables:** Create Liquibase changesets
5. **Push to production:** Use PostgreSQL compose file

---

## Useful Resources

- [Docker Documentation](https://docs.docker.com)
- [Docker Compose Reference](https://docs.docker.com/compose/compose-file/)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)
- [Multi-stage Builds](https://docs.docker.com/build/building/multi-stage/)

---

## Quick Reference

| Command | Purpose |
|---------|---------|
| `docker-compose up` | Start all services |
| `docker-compose down` | Stop all services |
| `docker-compose logs -f` | View logs |
| `docker-compose ps` | List services |
| `docker-compose exec app bash` | Enter container shell |
| `docker-compose build` | Rebuild images |
| `docker volume ls` | List data volumes |

---

**Happy Dockerizing! 🐳**
