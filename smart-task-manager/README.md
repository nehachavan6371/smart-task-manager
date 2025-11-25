# Smart Task Manager

Lightweight task manager service (Spring Boot backend + Vite/React frontend). This branch (`feat/admin-seed`) includes JWT auth, RBAC (User/Role), admin seeding via environment variables, audit logging, Dockerfiles, and CI/deploy workflows.

<!-- Badges (add after publishing) -->
<!-- e.g. build / coverage / docker image -->


## Quick Start (backend)

Requirements: Java 17, Maven (or use the included `mvnw`), Docker (for containerized runs).

Build and run locally:

```bash
# from repo root
cd backend
mvn -B -DskipTests package
# run with Maven wrapper
./mvnw spring-boot:run
```

Default server: `http://localhost:8080`.

Health endpoint (use for probes)

- The application exposes Spring Boot Actuator at `/actuator/health` by default. Make sure this path is available for load-balancer/readiness checks in staging/production (unprotected or via a probe-specific route).

Auth: default seeded admin (dev only)

- Username: `admin`
- Password: `admin`

To override the seeded admin, set environment variables before starting the app:

```bash
export ADMIN_USERNAME=myadmin
export ADMIN_PASSWORD=strongpassword
export ADMIN_ROLES=ROLE_ADMIN,ROLE_USER
```

The `DataInitializer` reads `ADMIN_USERNAME`/`ADMIN_PASSWORD`/`ADMIN_ROLES` at startup.

## Docker (development)

Start Postgres + backend using the repo `docker-compose.yml` (dev):

```bash
cd backend
docker compose up --build
```

For staging/production the repository includes `backend/docker-compose.prod.yml` which expects an image tag and environment variables:

```bash
# set the image built/pushed by CI
export SMART_TASK_BACKEND_IMAGE=ghcr.io/<your-org>/smart-task-manager-backend:<tag>
./deploy.sh
```

## CI & Deploy

- Unit & integration tests run in `.github/workflows/ci.yml`.
- A deploy workflow `.github/workflows/deploy-staging.yml` builds and pushes the backend image to GHCR and SSH-deploys to a staging host using `docker compose`.

Required repository secrets for the deploy workflow (set in GitHub repository Settings → Secrets):
- `GHCR_PAT` (or configure `GITHUB_TOKEN` permissions for GHCR)
- `STAGING_HOST`, `STAGING_USER`, `STAGING_SSH_KEY`, `STAGING_SSH_PORT` (optional)
- `STAGING_APP_DIR` — directory on staging host containing `docker-compose.prod.yml` and `deploy.sh`

Security reminder: never commit secrets to the repository. Use GitHub Secrets, a dedicated secrets manager, or environment variables injected at deploy time.

## Testing the API (curl)

Login and get a JWT (default admin):

```bash
curl -s -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"admin"}'
```

Use the returned token for protected endpoints:

```bash
curl -H "Authorization: Bearer <token>" http://localhost:8080/api/tasks
```

## Security notes (short)

- Do not use the default `admin:admin` credentials in staging/production.
- Provide `JWT_SECRET` in environment for staging/production and store it in a secrets manager.
- Ensure TLS is enabled for production endpoints.
- Add image vulnerability scanning (e.g., `trivy`) in CI before deploying images.

## Contributing / PRs

- Branch from `main` for features and open PRs to `main` (we used `feat/admin-seed` for this work).
- Use the prepared PR body in this branch when creating the PR.

## Where to go next

- Set up repository secrets and run the `deploy-staging` workflow after merging to `main`.
- Harden production settings: disable `hibernate.ddl-auto`, add Flyway migrations for schema/seeding, add health/readiness probes, and enable image scanning.

PR note: after pushing changes to `feat/admin-seed`, create a Pull Request against `main` (you can open the PR in the browser at:

`https://github.com/nehachavan6371/smart-task-manager/pull/new/feat/admin-seed`)

---
Generated/updated by development automation on branch `feat/admin-seed`.
