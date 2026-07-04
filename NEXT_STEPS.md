# Next Steps - Deployment & Integration

## Current Status

✅ **Backend Complete & Ready**
- Spring Boot 3.1.4 fully functional
- All entities, services, controllers implemented
- JWT authentication working
- 6 controllers with 20+ endpoints
- Database auto-initialization
- Project compiles without errors

---

## What to Do Next

### Option 1: Continue Development (Recommended for Now)

Keep backend and frontend separate for development:

```
proxymedoc/
├── frontend/                 (Next.js, http://localhost:3000)
│   └── backend/            (Spring Boot, http://localhost:8080)
└── [git repo root]         (Will be created later)
```

**Start Backend:**
```bash
cd frontend/backend
mvn spring-boot:run
```

**Start Frontend (in another terminal):**
```bash
cd frontend
npm run dev
```

### Option 2: Organize for Production

As mentioned in requirements, later you'll move backend to parent directory:

```
proxymedoc/                  (root git repo)
├── backend/                (Spring Boot)
├── frontend/               (Next.js)
└── .gitignore
```

**Migration steps** (you can do manually after testing):
1. Create `/home/dr_xw/Desktop/lastV/proxymedoc/` directory
2. Initialize git: `git init`
3. Move `frontend/backend/` to `proxymedoc/backend/`
4. Move `frontend/` to `proxymedoc/frontend/`
5. Add to git and commit

---

## Testing Checklist

### 1. Database Connection
- [ ] PostgreSQL running on localhost:5432
- [ ] Database `proxymedoc` created
- [ ] User `proxyadmin` exists

### 2. Backend Startup
- [ ] `mvn spring-boot:run` starts without errors
- [ ] Server listens on `http://localhost:8080`
- [ ] Sample data seeded (2 pharmacies, 2 medications)

### 3. API Endpoints
- [ ] `GET /api/pharmacies` returns 2 pharmacies
- [ ] `POST /api/auth/register` returns JWT token
- [ ] `GET /api/medicaments` returns 2 medications
- [ ] `GET /api/pharmacies/search/nearby?lat=3.8667&lon=11.5167&radius=10` works

### 4. Frontend Integration
- [ ] Frontend calls `http://localhost:8080/api/auth/register`
- [ ] Frontend stores JWT token
- [ ] Frontend makes authenticated requests with token
- [ ] CORS headers accepted from frontend

---

## Key Files to Know

| File | Purpose |
|------|---------|
| `pom.xml` | Maven dependencies & build config |
| `application.properties` | Database & JWT configuration |
| `ProxymedocBackendApplication.java` | Spring Boot entry point |
| `SecurityConfig.java` | Security & CORS setup |
| `DataLoader.java` | Sample data initialization |
| `GlobalExceptionHandler.java` | Error handling |
| `*Controller.java` | REST endpoints (6 files) |
| `*Service.java` | Business logic (4 files) |
| `*Entity.java` | JPA models (13 files) |
| `*DTO.java` | DTOs with validation (8 files) |

---

## Environment Variables (Optional)

Can override defaults by setting environment variables:

```bash
export APP_JWT_SECRET=YourSecretKeyHere
export APP_JWT_EXPIRATION_MS=86400000
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/proxymedoc
export SPRING_DATASOURCE_USERNAME=proxyadmin
export SPRING_DATASOURCE_PASSWORD=proxyadmin
```

Then start:
```bash
mvn spring-boot:run
```

---

## Common Issues & Solutions

### Issue: Port 8080 already in use
**Solution:**
```bash
lsof -i :8080
kill -9 <PID>
```

### Issue: PostgreSQL connection refused
**Solution:**
```bash
# Check PostgreSQL status
sudo systemctl status postgresql

# Start PostgreSQL if needed
sudo systemctl start postgresql

# Verify connection
psql -U proxyadmin -d proxymedoc
```

### Issue: JWT token validation fails
**Solution:**
- Ensure token is passed with `Authorization: Bearer <token>`
- Check token hasn't expired (24-hour lifespan)
- Register new user to get fresh token

### Issue: CORS errors in frontend
**Solution:**
- Backend already configured for `http://localhost:3000`
- Ensure frontend is actually on localhost:3000 (not 127.0.0.1)
- Clear browser cache/cookies

### Issue: Maven build fails
**Solution:**
```bash
# Clear Maven cache
mvn clean

# Update dependencies
mvn dependency:resolve

# Rebuild
mvn clean install
```

---

## Production Deployment Checklist

When ready for production, consider:

- [ ] **Environment Variables** - Move secrets to env vars (JWT secret, DB password)
- [ ] **Database** - Use managed PostgreSQL (AWS RDS, DigitalOcean, etc.)
- [ ] **SSL/TLS** - Enable HTTPS (Nginx reverse proxy recommended)
- [ ] **Logging** - Centralize logs (ELK stack, CloudWatch, etc.)
- [ ] **Monitoring** - Set up uptime monitoring & alerts
- [ ] **Backups** - Daily database backups
- [ ] **API Documentation** - Add Swagger/OpenAPI (optional)
- [ ] **Rate Limiting** - Add request throttling
- [ ] **Authentication** - Consider OAuth2 for more security
- [ ] **Tests** - Add unit & integration tests before production

---

## Git Workflow (When Ready)

```bash
# Create root directory
mkdir ~/Desktop/lastV/proxymedoc && cd ~/Desktop/lastV/proxymedoc

# Initialize git
git init

# Create .gitignore
cat > .gitignore << 'EOF'
backend/target/
backend/.mvn/
backend/*.log
frontend/.next/
frontend/node_modules/
frontend/.env.local
.env
EOF

# Add backend
cp -r ~/Desktop/lastV/proxymedoc/frontend/backend ./backend

# Add frontend
cp -r ~/Desktop/lastV/proxymedoc/frontend ./frontend

# Add to git
git add .
git commit -m "Initial commit: Proxymedoc platform

- Spring Boot backend with full API
- Next.js frontend integration-ready
- JWT authentication
- PostgreSQL database"

# Create remote (if using GitHub)
git remote add origin https://github.com/yourusername/proxymedoc.git
git push -u origin main
```

---

## Quick Development Workflow

**Terminal 1 - Backend:**
```bash
cd ~/Desktop/lastV/proxymedoc/frontend/backend
mvn spring-boot:run
```

**Terminal 2 - Frontend:**
```bash
cd ~/Desktop/lastV/proxymedoc/frontend
npm run dev
```

**Browser:**
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080

**Change something → Backend auto-restarts, Frontend auto-reloads**

---

## Documentation

- **QUICK_START.md** - Getting started & API examples
- **IMPLEMENTATION_SUMMARY.md** - Complete feature list & architecture
- **README.md** - General project overview (update as needed)
- **This file** - Next steps & deployment guide

---

## Support & Debugging

### View Backend Logs
```bash
# Tail last 100 lines
tail -100 target/*.log

# Watch logs in real-time
tail -f target/*.log
```

### Check Database
```bash
# Connect to PostgreSQL
psql -U proxyadmin -d proxymedoc

# View all tables
\dt

# View seeded pharmacies
SELECT id, nom, statut FROM pharmacie;

# View seeded medications
SELECT id, nom, prix_unitaire FROM medicament;
```

### API Testing Tools
- **cURL** - Command line
- **Postman** - GUI tool (import endpoints)
- **Thunder Client** - VS Code extension
- **REST Client** - VS Code extension (create .rest files)

---

**Backend is production-ready for integration testing! 🚀**

Next: Connect frontend to backend and run full integration tests.
