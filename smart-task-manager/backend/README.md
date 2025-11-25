# Smart Task Manager — backend (Spring Boot scaffold)

This folder contains a minimal Spring Boot scaffold for the Smart Task Manager backend.

Goals
- Provide a small, runnable API for tasks (CRUD).
- Demonstrate audit logging for create/update/delete.
- Provide Dockerfile and docker-compose for local runs.

Quick start (local, using in-memory H2):

```bash
# build
mvn -B -DskipTests package

# run
java -jar target/backend-0.0.1-SNAPSHOT.jar

# or with docker-compose
docker compose up --build
```

API endpoints (examples):
- `GET /api/tasks` — list tasks
- `GET /api/tasks/{id}` — get task by id
- `POST /api/tasks` — create task (optionally add header `X-User` to indicate who performed the action)
- `PUT /api/tasks/{id}` — update task
- `DELETE /api/tasks/{id}` — delete task

Notes
- The scaffold uses H2 in-memory DB for quick runs. Swap to Postgres by updating `application.yml` and enabling the `db` service in `docker-compose.yml`.
- Security (RBAC) and full authentication are intentionally left as next steps. The code stores the `performedBy` value from the `X-User` header into audit logs; replace with real auth integration when present.

Authentication
- This scaffold now includes JWT-based auth + RBAC.
- Endpoints:
	- `POST /api/auth/register` — create a demo user (no validation; for local testing only).
	- `POST /api/auth/login` — body `{ "username": "admin", "password": "admin" }` returns `{ "token": "..." }`.

Use the returned token in the `Authorization` header for task endpoints:

```http
Authorization: Bearer <token>
```

Default demo user: `admin` / `admin` (created at startup). Change credentials in `DataInitializer`.

Next steps you may ask me to do:
- Add JWT-based auth and RBAC middleware.
- Implement user/role entities and permission checks on endpoints.
- Add database migrations (Flyway or Liquibase).

Frontend
- A minimal React + Vite frontend is available in the `frontend/` folder. It connects to the backend at `http://localhost:8080` and expects CORS to be enabled for `http://localhost:5173`.
- Quick start (frontend):
	- Install dependencies: `cd frontend && npm ci`
	- Run dev server: `npm run dev` (Vite, default `http://localhost:5173`).
	- Build for production: `npm run build` and serve with `npm run preview` or the provided `Dockerfile`.

Notes:
- The backend has CORS enabled for common dev origins. If you run the frontend on a different host/port, update `SecurityConfig.corsConfigurationSource()`.
- The demo `admin/admin` user is created at backend startup.
