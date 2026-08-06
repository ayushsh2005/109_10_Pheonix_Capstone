# Portfolio Manager Frontend

Frontend application for the Investment Portfolio Management System.

This app is a React + Vite single-page application used by an investment manager to:

- View portfolio dashboard metrics
- Manage customers
- Manage investments
- Review rule-based and AI suggestions

It supports both:

- Mock mode for frontend-only development
- Live API mode against the Spring Boot backend

## Tech Stack

- React 19
- Vite 8
- React Router 7
- Axios
- Recharts
- Jest + Testing Library
- ESLint

## Features

- Dashboard summary view
- Customer list and customer detail pages
- Investment list and management workflows
- Suggestions page
- Theme support via context provider
- Toast notifications via context provider
- API service layer with mock/live switching

## Routes

- `/` -> Dashboard
- `/customers` -> Customers
- `/customers/:id` -> Customer detail
- `/investments` -> Investments
- `/suggestions` -> Suggestions

## Project Structure

```text
frontend/
	src/
		api/
			client.js
			config.js
			services/
			mock/
		components/
			charts/
			forms/
			layout/
			ui/
		context/
		pages/
		utils/
		__tests__/
		App.jsx
		main.jsx
```

## Prerequisites

- Node.js 20+
- npm 10+

## Environment Variables

Create or update `frontend/.env`:

```env
VITE_USE_MOCK=false
VITE_API_URL=http://localhost:8080/api
```

Variables:

- `VITE_USE_MOCK`
	- `true`: use in-memory mock data (no backend required)
	- `false`: use backend APIs
- `VITE_API_URL`
	- Base URL used by Axios client
	- Default fallback is `http://localhost:8080/api`

## Getting Started

### 1. Install dependencies

```bash
cd frontend
npm ci
```

### 2. Run in mock mode (frontend-only)

```bash
# in frontend/.env
VITE_USE_MOCK=true

npm run dev
```

Open:

- http://localhost:5173

### 3. Run with backend API

Make sure backend is running at `http://localhost:8080` with context path `/api`.

```bash
# in frontend/.env
VITE_USE_MOCK=false
VITE_API_URL=http://localhost:8080/api

npm run dev
```

## Available Scripts

- `npm run dev` - Start Vite dev server
- `npm run build` - Create production build in `dist/`
- `npm run preview` - Preview production build locally
- `npm run lint` - Run ESLint
- `npm run test` - Run Jest tests
- `npm run test:watch` - Run tests in watch mode
- `npm run test:coverage` - Generate coverage report

## Testing

Run all tests:

```bash
npm test
```

Run coverage:

```bash
npm run test:coverage
```

Tests are colocated in `src/**` and in `src/__tests__/`.

## Docker

The frontend uses a multi-stage Docker build:

- Build stage: Node 20, Vite build
- Runtime stage: Nginx serving static files

In containerized mode, Nginx forwards `/api/*` requests to the backend service.

### Build frontend image only

From repository root:

```bash
docker build -t portfolio-frontend:local ./frontend
```

### Run full stack with Docker Compose

From repository root:

```bash
docker compose up -d --build
```

Then open:

- http://localhost

## API Integration

The frontend service layer expects these backend endpoints:

- `GET /api/dashboard/summary`
- `GET /api/customers`
- `GET /api/customers/:id`
- `POST /api/customers`
- `PUT /api/customers/:id`
- `DELETE /api/customers/:id`
- `GET /api/customers/:id/portfolio`
- `POST /api/customers/:id/archive`
- `POST /api/customers/:id/restore`
- `GET /api/investments`
- `GET /api/customers/:id/investments`
- `POST /api/customers/:id/investments`
- `PUT /api/investments/:id`
- `DELETE /api/investments/:id`
- `GET /api/suggestions`
- `GET /api/customers/:id/suggestions`
- `GET /api/customers/:id/ai-suggestions`

## Troubleshooting

- Blank page or failed API calls:
	- Verify `VITE_API_URL`
	- Verify backend context path is `/api`
- CORS errors in local dev:
	- Confirm backend CORS settings allow `http://localhost:5173`
- Stale dependency issues:

```bash
# PowerShell (Windows)
Remove-Item -Recurse -Force node_modules
Remove-Item -Force package-lock.json
npm install

# Bash (macOS/Linux)
rm -rf node_modules package-lock.json
npm install
```

## Related Docs

- Root project overview: `../readme.md`
- Product requirements: `../Portfolio_Manager_PRD.md`
- Backend notes: `../backend/Backend.md`
