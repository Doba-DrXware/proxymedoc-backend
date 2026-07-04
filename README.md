# Proxymedoc Backend (Skeleton)

This is a minimal Spring Boot backend skeleton to integrate with the existing Next.js frontend.

Quick start

```bash
# from the frontend folder where this backend was created
cd backend
mvn spring-boot:run
```

The app uses an in-memory H2 database. Default port: `8080`.

Important endpoints

- POST `/api/auth/register` — simple registration (mirrors frontend `/api/register`).
- GET `/api/pharmacies` — list pharmacies (returns sample data persisted in DB once created).
- GET `/api/pharmacies/{id}` — get one pharmacy by id.

Notes & next steps

- The project is intentionally minimal: it contains entities for `Utilisateur`, `Pharmacie`, and `Medicament`, plus repositories and a couple controllers.
- Next steps: implement `Commande`, `Panier`, `Reservation`, `Notification`, DTOs, services, validations and adapt fields precisely to the class diagram in `spec/classes métiers new.pdf`.
- The frontend expects CORS from `http://localhost:3000` (configured on controllers).

When you're ready I can:

- Expand entities to fully match the class diagram.
- Add DTOs and mapping, sample data import to seed the DB with pharmacies/medicaments like the frontend.
- Add authentication (JWT) and role-based endpoints.
