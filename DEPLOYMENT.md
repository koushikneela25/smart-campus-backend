# Smart Campus Backend - Render Deployment Guide 🚀

This guide explains step-by-step how to deploy the **Smart Campus Backend** on [Render](https://render.com) and connect it to your frontend at `https://smart-campus-frontend-chi.vercel.app/` using PostgreSQL.

---

## 🌟 Method 1: 1-Click Deployment using Render Blueprint (Recommended)

Render Blueprints automatically create both the **PostgreSQL Database** and the **Spring Boot Web Service** and link them together.

1. Push this backend repository to your GitHub account.
2. Go to your [Render Dashboard](https://dashboard.render.com/).
3. Click **New +** (top right) $\rightarrow$ **Blueprint**.
4. Connect your GitHub repository (`smart-campus-backend`).
5. Render will detect `render.yaml` and show:
   - **smart-campus-postgres** (PostgreSQL Database)
   - **smart-campus-backend** (Docker Web Service)
6. Click **Apply**.
7. Wait 2–3 minutes while Render sets up the PostgreSQL database and builds your Docker container.
8. Once finished, Render will assign your backend a public URL (e.g., `https://smart-campus-backend-xxxx.onrender.com`).

---

## 🛠️ Method 2: Manual Deployment on Render

If you prefer to configure services manually in the Render dashboard:

### Step 1: Create PostgreSQL Database on Render
1. In Render Dashboard, click **New +** $\rightarrow$ **PostgreSQL**.
2. Fill in the details:
   - **Name**: `smart-campus-postgres`
   - **Database**: `campus`
   - **User**: `campus_user`
   - **Plan**: Free (or Starter)
3. Click **Create Database**.
4. Once created, scroll down to the **Connections** section and copy the **Internal Database URL** (or External Database URL if deploying across accounts).

### Step 2: Create Web Service on Render
1. Click **New +** $\rightarrow$ **Web Service**.
2. Connect your GitHub repository (`smart-campus-backend`).
3. Configure the settings:
   - **Name**: `smart-campus-backend`
   - **Language / Runtime**: `Docker`
   - **Dockerfile Path**: `./Dockerfile`
   - **Plan**: Free (or Starter)
   - **Health Check Path**: `/api/health`
4. Under **Environment Variables**, add the following:

| Key | Value | Description |
|---|---|---|
| `DATABASE_URL` | *(Paste the Internal PostgreSQL URL from Step 1)* | Database connection string |
| `CORS_ORIGIN` | `https://smart-campus-frontend-chi.vercel.app,http://localhost:5173,http://localhost:3000` | Allowed origins for CORS |
| `JWT_SECRET` | *(Generate a 32+ character random string)* | Secret for JWT authentication |
| `JWT_EXPIRATION_MS` | `86400000` | 24-hour token validity |
| `PORT` | `8080` | Render port binding |

5. Click **Create Web Service**.

---

## 🔗 Step 3: Connect Frontend (Vercel) to Render Backend

Now that your backend is running on Render, update your Vercel frontend:

1. Go to your [Vercel Dashboard](https://vercel.com/dashboard).
2. Select your project for `https://smart-campus-frontend-chi.vercel.app`.
3. Go to **Settings** $\rightarrow$ **Environment Variables**.
4. Add or update the API base URL variable (e.g. `VITE_API_BASE_URL` or `NEXT_PUBLIC_API_URL`):
   ```
   VITE_API_BASE_URL = https://smart-campus-backend-xxxx.onrender.com
   ```
   *(Replace with your actual Render service URL)*
5. Go to **Deployments** $\rightarrow$ Click **Redeploy** on the latest deployment so it picks up the new environment variable.

---

## 🧪 Verification & Default Credentials

### 1. Check Backend Health
Open your browser and navigate to:
```
https://<your-render-app-url>/api/health
```
You should see:
```json
{
  "status": "UP",
  "service": "smart-campus-backend",
  "version": "1.0.0",
  "timestamp": "2026-10-10T..."
}
```

### 2. Default Seeded Accounts
The application automatically seeds the database on first startup:
- **Admin Email**: `admin@campus.edu`
- **Admin Password**: `admin123`
- **Default Geofence Zone**: Main Block (17.3850, 78.4867, 150m)
- **Default Event**: Hackathon Kickoff

You can register students and faculty directly from the frontend at `https://smart-campus-frontend-chi.vercel.app/`.
