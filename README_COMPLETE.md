# Proxymedoc Backend - Spring Boot (Complete Option 1)

Full backend implementation with DTOs, validations, services, and seed data for Proxymedoc pharmaceutical management platform.

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+
- PostgreSQL 12+ running on `localhost:5432`
- Database: `proxymedoc`, user: `proxyadmin`, password: `proxyadmin`

### Build & Run

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

Server starts on `http://localhost:8080`

## Database Setup

```sql
CREATE DATABASE proxymedoc;
CREATE USER proxyadmin WITH PASSWORD 'proxyadmin';
GRANT ALL PRIVILEGES ON DATABASE proxymedoc TO proxyadmin;
```

## Architecture

**Layers:**
- **Controllers**: REST endpoints with CORS enabled for `http://localhost:3000`
- **Services**: Business logic (CommandeService, NotificationService, OrdonnanceService, PharmacieService)
- **DTOs**: Request/Response with Bean Validation (@Valid)
- **Entities**: JPA models (10+ entity classes with inheritance + relationships)
- **Repositories**: Spring Data JPA
- **Mapper**: EntityDTOMapper for DTO ↔ Entity conversion
- **Config**: DataLoader (seed data), SecurityConfig (CORS), GlobalExceptionHandler

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register patient or pharmacy

**Patient Request:**
```json
{
  "role": "patient",
  "name": "Jean Martin",
  "email": "jean@example.com"
}
```

**Pharmacy Request:**
```json
{
  "role": "pharmacie",
  "name": "Pharmacie du Palais",
  "email": "contact@pharmacie.com",
  "adresse": "Bastos, Yaoundé",
  "phone": "+237 699 123 456",
  "licence": "LP-2023-00041"
}
```

### Pharmacies
- `GET /api/pharmacies` - List all pharmacies with stocks
- `GET /api/pharmacies/{id}` - Get pharmacy details
- `POST /api/pharmacies` - Create pharmacy (requires valid DTO)

### Orders (Commandes)
- `POST /api/commandes` - Create order
  - Auto-calculates total, decrements stock
  - Request: patientId, pharmacieId, lignes[]

### Reservations
- `POST /api/reservations` - Reserve medication (7-day default expiration)

### Prescriptions (Ordonnances)
- `POST /api/ordonnances` - Submit prescription
- `PUT /api/ordonnances/{id}/validate` - Pharmacist approves

### Notifications
- `GET /api/notifications/user/{userId}` - Get user notifications
- `POST /api/notifications` - Create notification

## Validations

All DTOs include Bean Validation constraints:
- `@NotBlank` for required strings
- `@NotNull` for required fields
- `@Min(0)` for prices/quantities
- Error responses include field-level details

## Sample Data

DataLoader seeds on startup:
- 2 validated pharmacies
- 2 medications
- Stock entries per pharmacy

## Entity Model

**Inheritance:**
```
Utilisateur (abstract)
├── Patient
├── Pharmacien
└── Administrateur
```

**Key Relations:**
- Pharmacie ← 1:N → Stock, Commande, Ordonnance, Reservation
- Medicament ← 1:N → Stock
- Utilisateur ← 1:N → Notification, Reservation, Commande, Ordonnance, CommandePersonnalisee

## Integration with Frontend

Frontend (localhost:3000) calls backend (localhost:8080):
- CORS enabled
- All endpoints return JSON
- Validation errors: HTTP 400 with error map

```javascript
const res = await fetch('http://localhost:8080/api/pharmacies');
const pharmacies = await res.json();
```

## Project Structure

```
backend/
├── pom.xml
├── src/main/java/com/proxymedoc/backend/
│   ├── ProxymedocBackendApplication.java
│   ├── model/           (Utilisateur, Pharmacie, Medicament, etc.)
│   ├── controller/      (REST endpoints)
│   ├── service/         (Business logic)
│   ├── repository/      (Data access)
│   ├── dto/            (Request/Response DTOs)
│   ├── mapper/         (DTO ↔ Entity conversion)
│   └── config/         (Security, DataLoader, Exception Handler)
└── src/main/resources/
    └── application.properties (PostgreSQL config)
```

## Next Steps

- Implement JWT authentication
- Add role-based access control
- Search endpoints (pharmacy by location, medication by name)
- File upload for documents
- Audit logging
- API documentation (Swagger)
