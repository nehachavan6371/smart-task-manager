Admin seeding (DEV)

The application can automatically seed an administrator user on startup. By default it will create a user `admin` with password `admin` and roles `ROLE_USER,ROLE_ADMIN` if the username does not already exist.

You can override these values via environment variables (useful for Docker / CI):

- `ADMIN_USERNAME` (default `admin`)
- `ADMIN_PASSWORD` (default `admin`)
- `ADMIN_ROLES` (comma-separated, default `ROLE_USER,ROLE_ADMIN`)

Examples:

Run with custom admin credentials:

```bash
ADMIN_USERNAME=neha ADMIN_PASSWORD=SuperSecret123 mvn spring-boot:run
```

With the packaged jar:

```bash
ADMIN_USERNAME=neha ADMIN_PASSWORD=SuperSecret123 java -jar target/backend-0.0.1-SNAPSHOT.jar
```

Docker Compose snippet (add under the `backend` service):

```yaml
services:
  backend:
    environment:
      - ADMIN_USERNAME=neha
      - ADMIN_PASSWORD=SuperSecret123
      - ADMIN_ROLES=ROLE_USER,ROLE_ADMIN
```

Security note: Do not use weak passwords in production. Use a secrets manager (Vault, Kubernetes Secrets, GitHub secrets) for production deployments.
