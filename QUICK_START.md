# Quick Start Guide - Proxymedoc Backend

## 1. Database Setup

Ensure PostgreSQL is running on `localhost:5432`:

```sql
CREATE DATABASE proxymedoc;
CREATE USER proxyadmin WITH PASSWORD 'proxyadmin';
GRANT ALL PRIVILEGES ON DATABASE proxymedoc TO proxyadmin;
```

## 2. Build Backend

```bash
cd backend
mvn clean install
```

✅ Compilation successful → `target/backend-0.0.1-SNAPSHOT.jar` created

## 3. Start Backend Server

```bash
mvn spring-boot:run
```

or

```bash
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

**Backend runs on:** `http://localhost:8080`

## 4. Verify Backend is Running

```bash
curl http://localhost:8080/api/pharmacies
```

Should return: List of 2 seeded pharmacies (JSON array)

## 5. Test Authentication

### Register a Patient

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "role": "patient",
    "name": "Test Patient",
    "email": "patient@example.com"
  }'
```

**Response:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "user": {
    "id": 3,
    "role": "patient",
    "name": "Test Patient",
    "email": "patient@example.com"
  }
}
```

### Use JWT Token

Copy the `token` value and use in subsequent requests:

```bash
TOKEN="eyJhbGciOiJIUzUxMiJ9..."

curl -X POST http://localhost:8080/api/reservations \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "patientId": 3,
    "medicamentId": 1
  }'
```

## 6. Explore API Endpoints

### Get All Pharmacies
```bash
curl http://localhost:8080/api/pharmacies
```

### Search Pharmacies by Name
```bash
curl "http://localhost:8080/api/pharmacies/search/name?q=Palais"
```

### Search Pharmacies by Location (10 km radius)
```bash
curl "http://localhost:8080/api/pharmacies/search/nearby?lat=3.8667&lon=11.5167&radius=10"
```

### Get All Medications
```bash
curl http://localhost:8080/api/medicaments
```

### Search Medications
```bash
curl "http://localhost:8080/api/medicaments/search?q=Amoxicilline"
```

### Search by Category
```bash
curl http://localhost:8080/api/medicaments/category/antibiotique
```

### Search by Price Range
```bash
curl "http://localhost:8080/api/medicaments/price-range?min=200&max=1000"
```

## 7. Make an Order (Requires JWT)

```bash
TOKEN="your-jwt-token"

curl -X POST http://localhost:8080/api/commandes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "patientId": 3,
    "pharmacieId": 1,
    "typeCommande": "STANDARD",
    "lignes": [
      {
        "medicamentId": 1,
        "quantite": 2,
        "prixUnitaire": 900.0
      }
    ]
  }'
```

**Response:**
```json
{
  "id": 1,
  "montantTotal": 1800.0,
  "statut": "EN_ATTENTE"
}
```

## 8. Seeded Sample Data

**Pharmacies:**
- Pharmacie du Palais (Bastos, Yaoundé) - Latitude: 3.8667, Longitude: 11.5167
- Pharmacie Centrale (Centre-ville, Yaoundé) - Latitude: 3.8480, Longitude: 11.5021

**Medications:**
- Amoxicilline 250mg - 900.0 FCFA
- Paracétamol 500mg - 250.0 FCFA

**Stock:**
- Amoxicilline: 30 units in Pharmacie du Palais, 70 in Pharmacie Centrale (threshold: 5-10)
- Paracétamol: 100 units in Pharmacie du Palais (threshold: 10)

## 9. Integration with Frontend

Frontend (http://localhost:3000) can now:

1. Register patients/pharmacies
2. Get JWT token
3. Browse pharmacies and medications
4. Create orders
5. Make reservations
6. Submit prescriptions

**CORS enabled** for `http://localhost:3000`

## 10. Troubleshooting

### Port 8080 Already in Use
```bash
lsof -i :8080
kill -9 <PID>
```

### PostgreSQL Connection Error
- Check PostgreSQL is running: `sudo systemctl status postgresql`
- Verify credentials in `application.properties`
- Ensure database exists

### JWT Token Expired
- Token expires after 24 hours
- Register again to get new token

### Validation Errors
Example error response:
```json
{
  "success": false,
  "errors": {
    "nom": "Le nom du médicament est requis",
    "prixUnitaire": "Le prix doit être supérieur à 0"
  }
}
```

## 11. View Logs

Add this to `application.properties` for more verbose logging:

```properties
logging.level.root=INFO
logging.level.com.proxymedoc=DEBUG
logging.level.org.springframework.security=DEBUG
```

---

**Backend is now ready! 🚀**

For detailed API documentation, see [IMPLEMENTATION_SUMMARY.md](./IMPLEMENTATION_SUMMARY.md)
