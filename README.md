# 🚗 SmartPark – Smart Parking Management System · Backend

> A production-ready **Spring Boot REST API** for smart parking management with JWT authentication, OTP email verification, role-based access control (Admin, User, Parking Owner, Security), QR-code based check-in/check-out, payments, analytics, and reporting.

---

## 📋 Table of Contents

* [Tech Stack](#-tech-stack)
* [Features](#-features)
* [System Design](#-system-design)
* [Project Structure](#-project-structure)
* [Roles & Permissions](#-roles--permissions)
* [API Endpoints](#-api-endpoints)
* [Setup & Installation](#-setup--installation)
* [Environment Configuration](#-environment-configuration)
* [Running the Application](#-running-the-application)
* [Swagger / API Docs](#-swagger--api-docs)
* [Security Architecture](#-security-architecture)
* [Error Handling](#-error-handling)
* [Security Note](#-security-note)

---

## 🛠 Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.x |
| Security | Spring Security + JWT (jjwt) |
| Database | MySQL 8.x |
| ORM | Spring Data JPA / Hibernate |
| Email | Spring Mail (Gmail SMTP) |
| QR Codes | Google ZXing (core + javase) |
| API Docs | SpringDoc OpenAPI / Swagger UI |
| Real-time | Spring WebSocket |
| Validation | Jakarta Bean Validation |
| Build Tool | Maven |
| Dev Tools | Lombok, Spring DevTools |

---

## ✨ Features

* **Authentication** – Register, OTP email verification, Login (JWT), Forgot/Reset password
* **Role-Based Access Control** – `ADMIN`, `USER`, `PARKING_OWNER`, `SECURITY`
* **Parking Areas** – Create/manage parking areas with per-vehicle-type slot counts, search by city/address, soft delete & restore
* **Parking Slots** – Auto-generate slots, filter by status/type/availability, manual status update
* **Parking Rates** – Define and manage rates per parking area
* **Vehicles** – Add/manage user vehicles, search by number/type
* **Bookings** – Create booking, start/complete/cancel, filter by status/type/date-range
* **QR Code** – Generate booking QR, verify QR, check-in/check-out
* **Payments** – Make payment, mark failed, extra payment, filter by status/date-range
* **Notifications** – User notifications with unread count and read status
* **Dashboards** – Role-specific dashboards (Admin / User / Parking Owner)
* **Analytics & Reports** – Revenue, booking, and payment reports (Admin)
* **Soft Delete & Restore** – Across users, vehicles, parking areas, slots, rates

---

## 🏗 System Design

```
┌─────────────────────────────────────────────────────────────────┐
│                        CLIENT (Browser)                         │
│                    HTML + CSS + Vanilla JS                      │
└───────────────────────────┬─────────────────────────────────────┘
                            │  HTTP/REST (JSON)
                            │  CORS: localhost:5500
                            ▼
┌─────────────────────────────────────────────────────────────────────┐
│                   SPRING BOOT APPLICATION                           │
│                   localhost:8080                                    │
│                                                                     │
│  ┌─────────────┐      ┌───────────────┐     ┌────────────────────┐  │
│  │  JWT Filter │ ───▶ │ Controllers  │───▶ │      Services      │  │
│  │  (Auth Gate)│      │ /api/auth     │     │   Business Logic   │  │
│  └─────────────┘      │ /api/users    │     └────────┬───────────┘  │
│                       │ /api/bookings │              │              │
│  ┌──────────────┐     │ /api/parking-*│     ┌────────▼───────────┐  │
│  │  CORS Config │     │ /api/payments │     │    Repositories    │  │
│  │  (Security)  │     │ /api/dashboard│     │   (Spring Data)    │  │
│  └──────────────┘     └───────────────┘     └────────┬───────────┘  │
│                                                      │              │
└──────────────────────────────────────────────────────┼──────────────┘
                                                       │
                          ┌────────────────────────────▼───────────┐
                          │           MySQL Database               │
                          │                                        │
                          │ ┌──────┐ ┌──────────┐ ┌──────────────┐ │
                          │ │users │ │ vehicles │ │parking_areas │ │
                          │ └──────┘ └──────────┘ └──────────────┘ │
                          │ ┌──────────────┐ ┌──────────┐ ┌──────┐ │
                          │ │parking_slots │ │ bookings │ │rates │ │
                          │ └──────────────┘ └──────────┘ └──────┘ │
                          │     ┌──────────┐  ┌───────────────┐    │
                          │     │ payments │  │ notifications │    │
                          │     └──────────┘  └───────────────┘    │
                          └────────────────────────────────────────┘
                                                         │
                            ┌────────────────────────────▼──────────┐
                            │         Gmail SMTP Server             │
                            │    (OTP & Password Reset Emails)      │
                            └───────────────────────────────────────┘
```

### Authentication Flow

```
  REGISTER                   VERIFY OTP                 LOGIN
─────────────              ─────────────              ──────────
User fills form    →    Email arrives with     →   Enter email +
name/email/phone/        6-digit OTP code            password
password/role
      │                        │                        │
      ▼                        ▼                        ▼
POST /api/auth/register   POST /api/auth/verify    POST /api/auth/login
      │                        │                        │
      ▼                        ▼                        ▼
Save user (unverified)    Mark user verified      Validate credentials
Generate 6-digit OTP      Clear OTP from DB        Generate JWT token
Send OTP via Gmail               │                        │
                                 ▼                        ▼
                            Redirect to login        Return JWT (accessToken)
                                                  (stored in localStorage)

Every subsequent request:
  Request → JwtFilter → Validate Token → Set Authentication → Controller
```

### Booking & QR Flow

```
CREATE BOOKING            PAYMENT             QR CHECK-IN/OUT
──────────────          ──────────          ─────────────────
USER selects        →   USER pays for   →   SECURITY scans QR
vehicle + area +         booking              at entry/exit
slot + hours              │                       │
      │                   ▼                       ▼
      ▼              POST /api/payments   PUT /api/qr/check-in/{id}
POST /api/bookings        │              PUT /api/qr/check-out/{id}
      │                   ▼                       │
      ▼              Booking status         Booking status updated
QR code generated      updated              (IN_PROGRESS / COMPLETED)
GET /api/qr/booking/{bookingNumber}
```

---

## 📁 Project Structure

```
smart-parking-management/
│
├── src/main/java/com/smartparking/management/
│   │
│   ├── config/                          # Security & JWT Config
│   │   ├── SecurityConfig.java          # Spring Security rules + CORS
│   │   ├── JwtFilter.java               # JWT request filter (per request)
│   │   ├── JwtUtil.java                 # Token generate / validate / extract
│   │   ├── CustomUserDetailsService.java# Load user by email for Spring Security
│   │   └── SwaggerConfig.java           # OpenAPI / Swagger configuration
│   │
│   ├── controller/                      # REST API Controllers
│   │   ├── AuthController.java          # /api/auth/**
│   │   ├── UserController.java          # /api/users/**
│   │   ├── VehicleController.java       # /api/vehicles/**
│   │   ├── ParkingAreaController.java   # /api/parking-areas/**
│   │   ├── ParkingSlotController.java   # /api/parking-slots/**
│   │   ├── ParkingRateController.java   # /api/parking-rates/**
│   │   ├── BookingController.java       # /api/bookings/**
│   │   ├── PaymentController.java       # /api/payments/**
│   │   ├── QrCodeController.java        # /api/qr/**
│   │   ├── NotificationController.java  # /api/notifications/**
│   │   ├── DashboardController.java     # /api/dashboard/**
│   │   ├── AnalyticsController.java     # /api/analytics
│   │   └── ReportController.java        # /api/reports/**
│   │
│   ├── service/ & service/impl/         # Business logic interfaces & implementations
│   │   ├── AuthService(Impl)
│   │   ├── UserService(Impl)
│   │   ├── VehicleService(Impl)
│   │   ├── ParkingAreaService(Impl)
│   │   ├── ParkingSlotService(Impl)
│   │   ├── ParkingRateService(Impl)
│   │   ├── BookingService(Impl)
│   │   ├── PaymentService(Impl)
│   │   ├── QrCodeService(Impl)
│   │   ├── NotificationService(Impl)
│   │   ├── DashboardService(Impl)
│   │   ├── AnalyticsService(Impl)
│   │   ├── ReportService(Impl)
│   │   └── EmailService(Impl)
│   │
│   ├── entity/                          # JPA Entities (Database Tables)
│   │   ├── User.java
│   │   ├── Vehicle.java
│   │   ├── ParkingArea.java
│   │   ├── ParkingSlot.java
│   │   ├── ParkingRate.java
│   │   ├── Booking.java
│   │   ├── Payment.java
│   │   └── Notification.java
│   │
│   ├── repository/                      # Spring Data JPA Repositories
│   │   ├── UserRepository.java
│   │   ├── VehicleRepository.java
│   │   ├── ParkingAreaRepository.java
│   │   ├── ParkingSlotRepository.java
│   │   ├── ParkingRateRepository.java
│   │   ├── BookingRepository.java
│   │   ├── PaymentRepository.java
│   │   └── NotificationRepository.java
│   │
│   ├── dto/
│   │   ├── request/                     # Incoming request bodies
│   │   │   ├── RegisterRequest.java
│   │   │   ├── LoginRequest.java
│   │   │   ├── VerifyOtpRequest.java
│   │   │   ├── ForgotPasswordRequest.java
│   │   │   ├── ResetPasswordRequest.java
│   │   │   ├── UpdateProfileRequest.java
│   │   │   ├── VehicleRequest.java
│   │   │   ├── ParkingAreaRequest.java
│   │   │   ├── ParkingSlotGenerateRequest.java
│   │   │   ├── ParkingRateRequest.java
│   │   │   ├── CreateBookingRequest.java
│   │   │   └── PaymentRequest.java
│   │   │
│   │   └── response/                    # Outgoing response bodies
│   │       ├── AuthResponse.java
│   │       ├── UserResponse.java
│   │       ├── VehicleResponse.java
│   │       ├── ParkingAreaResponse.java
│   │       ├── ParkingSlotResponse.java
│   │       ├── ParkingRateResponse.java
│   │       ├── BookingResponse.java
│   │       ├── PaymentResponse.java
│   │       ├── QrCodeResponse.java
│   │       ├── NotificationResponse.java
│   │       ├── AnalyticsResponse.java
│   │       ├── AdminDashboardResponse.java
│   │       ├── UserDashboardResponse.java
│   │       ├── ParkingOwnerDashboardResponse.java
│   │       ├── RevenueReportResponse.java
│   │       ├── BookingReportResponse.java
│   │       └── PaymentReportResponse.java
│   │
│   ├── mapper/                          # Entity ↔ DTO Converters
│   │   ├── UserMapper.java
│   │   ├── VehicleMapper.java
│   │   ├── ParkingAreaMapper.java
│   │   ├── ParkingSlotMapper.java
│   │   ├── ParkingRateMapper.java
│   │   ├── BookingMapper.java
│   │   ├── PaymentMapper.java
│   │   └── NotificationMapper.java
│   │
│   ├── enums/                           # Enumerations
│   │   ├── Role.java                    # USER, ADMIN, SECURITY, PARKING_OWNER
│   │   ├── VehicleType.java
│   │   ├── SlotStatus.java
│   │   ├── BookingStatus.java
│   │   ├── PaymentStatus.java
│   │   ├── PaymentMethod.java
│   │   └── NotificationType.java
│   │
│   ├── exceptions/                      # Custom Exceptions
│   │   ├── GlobalExceptionHandler.java  # @RestControllerAdvice
│   │   ├── ErrorResponse.java
│   │   ├── ResourceNotFoundException.java
│   │   ├── BadRequestException.java
│   │   ├── AccessDeniedException.java
│   │   ├── UnauthorizedException.java
│   │   └── EmailSendingException.java
│   │
│   ├── scheduler/
│   │   └── BookingScheduler.java        # Scheduled tasks (e.g., expiry checks)
│   │
│   ├── util/
│   │   ├── FloorUtil.java
│   │   └── QrCodeUtil.java
│   │
│   └── SmartParkingManagementApplication.java  # Main entry point
│
├── src/main/resources/
│   └── application.properties           # DB, JWT, Mail config
│
├── src/test/java/
│   └── SmartParkingManagementApplicationTests.java
│
└── pom.xml                              # Maven dependencies
```

---

## 👥 Roles & Permissions

| Role | Description | Key Permissions |
|------|-------------|-----------------|
| **ADMIN** | System administrator | Manage users, view all bookings/payments, analytics & reports, restore deleted records |
| **USER** | Regular customer | Manage own vehicles, create/view/cancel own bookings, make payments, view own dashboard, notifications |
| **PARKING_OWNER** | Owns/manages parking areas | Create & manage own parking areas, slots, and rates; view owner dashboard and bookings for their areas |
| **SECURITY** | Gate/security staff | Verify QR codes, check-in/check-out vehicles, start/complete bookings, search bookings |

---

## 🔌 API Endpoints

> Base URL: `http://localhost:8080`
> Protected endpoints require header: `Authorization: Bearer <token>`

### 🔑 Auth (`/api/auth`) — Public

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/api/auth/register` | Register new user | Public |
| POST | `/api/auth/verify` | Verify email OTP | Public |
| POST | `/api/auth/login` | Login, get JWT | Public |
| POST | `/api/auth/forgot` | Send password reset OTP | Public |
| POST | `/api/auth/reset` | Reset password | Public |

**Example – Register**
```http
POST /api/auth/register
Content-Type: application/json

{
  "name": "Madhan Kumar",
  "email": "madhan@example.com",
  "phoneNumber": "9876543210",
  "password": "Pass@123",
  "role": "USER"
}
```

**Example – Login**
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "madhan@example.com",
  "password": "Pass@123"
}
```
Response:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "userId": 1,
  "name": "Madhan Kumar",
  "role": "USER"
}
```

---

### 👤 Users (`/api/users`)

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/api/users/profile` | Get current user's profile | Authenticated |
| PUT | `/api/users/profile` | Update profile | USER |
| GET | `/api/users/{id}` | Get user by ID | ADMIN |
| GET | `/api/users` | Get all users | ADMIN |
| GET | `/api/users/all` | Get all users including deleted | ADMIN |
| GET | `/api/users/search/email?email=` | Search users by email | ADMIN |
| GET | `/api/users/search/phone?phoneNumber=` | Search users by phone | ADMIN |
| DELETE | `/api/users/{id}` | Soft delete user | ADMIN |
| PUT | `/api/users/{id}/restore` | Restore deleted user | ADMIN |

**Example – Update Profile**
```http
PUT /api/users/profile
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Madhan Kumar",
  "phoneNumber": "9876543210"
}
```

---

### 🚙 Vehicles (`/api/vehicles`)

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/api/vehicles` | Add a vehicle | USER |
| GET | `/api/vehicles/my-vehicles` | Get my vehicles | USER |
| GET | `/api/vehicles/{id}` | Get vehicle by ID | ADMIN |
| GET | `/api/vehicles` | Get all vehicles | ADMIN |
| GET | `/api/vehicles/search?vehicleNumber=` | Search by vehicle number | ADMIN |
| GET | `/api/vehicles/type?vehicleType=` | Filter by vehicle type | ADMIN |
| DELETE | `/api/vehicles/{id}` | Soft delete vehicle | USER |
| PUT | `/api/vehicles/{id}/restore` | Restore vehicle | USER |

**Example – Add Vehicle**
```http
POST /api/vehicles
Authorization: Bearer <token>
Content-Type: application/json

{
  "vehicleNumber": "TN09AB1234",
  "vehicleType": "CAR"
}
```

---

### 🅿️ Parking Areas (`/api/parking-areas`)

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/api/parking-areas` | Add parking area | ADMIN, PARKING_OWNER |
| GET | `/api/parking-areas` | Get all parking areas | ADMIN, PARKING_OWNER, USER |
| GET | `/api/parking-areas/{id}` | Get parking area by ID | ADMIN, USER, SECURITY, PARKING_OWNER |
| GET | `/api/parking-areas/my` | Get my parking areas | PARKING_OWNER |
| GET | `/api/parking-areas/all` | Get all (incl. deleted, for current owner) | ADMIN, PARKING_OWNER, USER |
| GET | `/api/parking-areas/search/city?city=` | Search by city | USER, ADMIN, SECURITY, PARKING_OWNER |
| GET | `/api/parking-areas/search/address?address=` | Search by address | USER, ADMIN, SECURITY, PARKING_OWNER |
| PUT | `/api/parking-areas/{id}` | Update parking area | ADMIN, PARKING_OWNER |
| DELETE | `/api/parking-areas/{id}` | Soft delete parking area | ADMIN, PARKING_OWNER |
| PUT | `/api/parking-areas/{id}/restore` | Restore parking area | ADMIN, PARKING_OWNER |
| DELETE | `/api/parking-areas/{parkingAreaId}/vehicle-type?vehicleType=` | Remove vehicle type from area | ADMIN, PARKING_OWNER |
| PUT | `/api/parking-areas/{parkingAreaId}/vehicle-type/restore?vehicleType=` | Restore vehicle type for area | ADMIN, PARKING_OWNER |

**Example – Add Parking Area**
```http
POST /api/parking-areas
Authorization: Bearer <token>
Content-Type: application/json

{
  "areaName": "City Center Parking",
  "address": "123 MG Road",
  "city": "Bengaluru",
  "bikeSlots": 20,
  "evBikeSlots": 5,
  "carSlots": 30,
  "evCarSlots": 10,
  "truckSlots": 5
}
```

---

### 🅿️ Parking Slots (`/api/parking-slots`)

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/api/parking-slots/generate` | Generate slots for a parking area | ADMIN, PARKING_OWNER |
| GET | `/api/parking-slots` | Get all slots | ADMIN, PARKING_OWNER |
| GET | `/api/parking-slots/{id}` | Get slot by ID | ADMIN, PARKING_OWNER, USER, SECURITY |
| GET | `/api/parking-slots/area/{parkingAreaId}` | Get slots by parking area | ADMIN, PARKING_OWNER, USER, SECURITY |
| GET | `/api/parking-slots/status?slotStatus=` | Filter by slot status | ADMIN, PARKING_OWNER, USER, SECURITY |
| GET | `/api/parking-slots/type?vehicleType=` | Filter by vehicle type | ADMIN, PARKING_OWNER, USER, SECURITY |
| GET | `/api/parking-slots/available/area/{parkingAreaId}` | Available slots in an area | ADMIN, PARKING_OWNER, USER, SECURITY |
| GET | `/api/parking-slots/available?parkingAreaId=&vehicleType=` | Available slots by area & type | ADMIN, PARKING_OWNER, USER, SECURITY |
| GET | `/api/parking-slots/search/slot?parkingAreaId=&vehicleType=&slotNumber=` | Search a specific slot | ADMIN, PARKING_OWNER, USER, SECURITY |
| PUT | `/api/parking-slots/{id}/status?status=` | Update slot status | ADMIN, PARKING_OWNER |
| DELETE | `/api/parking-slots/{id}` | Soft delete slot | ADMIN, PARKING_OWNER |
| PUT | `/api/parking-slots/{id}/restore` | Restore slot | ADMIN, PARKING_OWNER |

**Example – Generate Slots**
```http
POST /api/parking-slots/generate
Authorization: Bearer <token>
Content-Type: application/json

{
  "parkingAreaId": 1
}
```

**Example – Update Slot Status**
```http
PUT /api/parking-slots/15/status?status=MAINTENANCE
Authorization: Bearer <token>
```

---

### 💰 Parking Rates (`/api/parking-rates`)

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/api/parking-rates` | Add a parking rate | ADMIN, PARKING_OWNER |
| GET | `/api/parking-rates` | Get all rates | ADMIN, PARKING_OWNER |
| GET | `/api/parking-rates/{id}` | Get rate by ID | ADMIN, PARKING_OWNER |
| GET | `/api/parking-rates/area/{parkingAreaId}` | Get rates by parking area | ADMIN, PARKING_OWNER |
| PUT | `/api/parking-rates/{id}` | Update rate | ADMIN, PARKING_OWNER |
| DELETE | `/api/parking-rates/{id}` | Soft delete rate | ADMIN, PARKING_OWNER |
| PUT | `/api/parking-rates/{id}/restore` | Restore rate | ADMIN, PARKING_OWNER |

**Example – Add Parking Rate**
```http
POST /api/parking-rates
Authorization: Bearer <token>
Content-Type: application/json

{
  "parkingAreaId": 1,
  "vehicleType": "CAR",
  "ratePerHour": 30
}
```

---

### 📅 Bookings (`/api/bookings`)

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/api/bookings` | Create a booking | USER |
| GET | `/api/bookings/my` | Get my bookings | USER |
| GET | `/api/bookings/my/status?bookingStatus=` | My bookings by status | USER |
| GET | `/api/bookings/my/type?vehicleType=` | My bookings by vehicle type | USER |
| GET | `/api/bookings/my/date-range?fromDate=&toDate=` | My bookings by date range | USER |
| PUT | `/api/bookings/{bookingId}/cancel` | Cancel my booking | USER |
| GET | `/api/bookings/{bookingId}` | Get booking by ID | ADMIN, SECURITY, PARKING_OWNER |
| GET | `/api/bookings/search?bookingNumber=` | Search booking by number | ADMIN, SECURITY |
| GET | `/api/bookings` | Get all bookings | ADMIN, SECURITY, PARKING_OWNER |
| GET | `/api/bookings/status?bookingStatus=` | Filter by status | ADMIN, SECURITY, PARKING_OWNER |
| GET | `/api/bookings/type?vehicleType=` | Filter by vehicle type | ADMIN, SECURITY, PARKING_OWNER |
| GET | `/api/bookings/date-range?fromDate=&toDate=` | Filter by date range | ADMIN, SECURITY, PARKING_OWNER |
| PUT | `/api/bookings/{bookingId}/start` | Start booking (check-in) | SECURITY |
| PUT | `/api/bookings/{bookingId}/complete` | Complete booking (check-out) | SECURITY |

**Example – Create Booking**
```http
POST /api/bookings
Authorization: Bearer <token>
Content-Type: application/json

{
  "vehicleId": 3,
  "parkingAreaId": 1,
  "parkingSlotId": 15,
  "bookedHours": 4
}
```
Response:
```json
{
  "bookingId": 101,
  "bookingNumber": "BKG-20260613-0001",
  "status": "BOOKED",
  "vehicleNumber": "TN09AB1234",
  "slotNumber": "C-15",
  "bookedHours": 4,
  "totalAmount": 120.0,
  "qrCodeBase64": "iVBORw0KGgoAAAANSUhEUgAA..."
}
```

---

### 💳 Payments (`/api/payments`)

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/api/payments` | Make payment for a booking | USER |
| POST | `/api/payments/failed/{bookingId}` | Mark payment as failed | USER |
| POST | `/api/payments/extra/{bookingId}?paymentMethod=` | Make extra payment (overstay) | USER |
| GET | `/api/payments/my` | Get my payments | USER |
| GET | `/api/payments/my/date-range?fromDate=&toDate=` | My payments by date range | USER |
| GET | `/api/payments/{paymentId}` | Get payment by ID | ADMIN |
| GET | `/api/payments/search?paymentNumber=` | Search payment by number | ADMIN |
| GET | `/api/payments/booking/{bookingId}` | Get payment by booking ID | ADMIN |
| GET | `/api/payments` | Get all payments | ADMIN |
| GET | `/api/payments/status?paymentStatus=` | Filter by status | ADMIN |
| GET | `/api/payments/date-range?fromDate=&toDate=` | Filter by date range | ADMIN |

**Example – Make Payment**
```http
POST /api/payments
Authorization: Bearer <token>
Content-Type: application/json

{
  "bookingId": 101,
  "paymentMethod": "UPI"
}
```

**Example – Extra Payment**
```http
POST /api/payments/extra/101?paymentMethod=CARD
Authorization: Bearer <token>
```

---

### 📷 QR Code (`/api/qr`)

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/api/qr/booking/{bookingNumber}` | Generate QR code for booking | USER |
| GET | `/api/qr/verify?bookingNumber=` | Verify QR code | SECURITY |
| PUT | `/api/qr/check-in/{bookingId}` | Check-in vehicle (calls start booking) | SECURITY, USER |
| PUT | `/api/qr/check-out/{bookingId}` | Check-out vehicle (calls complete booking) | SECURITY, USER |

**Example – Verify QR**
```http
GET /api/qr/verify?bookingNumber=BKG-20260613-0001
Authorization: Bearer <token>
```

---

### 🔔 Notifications (`/api/notifications`)

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/api/notifications/my` | Get my notifications | USER |
| GET | `/api/notifications/my/unread-count` | Get unread notification count | USER |
| PUT | `/api/notifications/{notificationId}/read` | Mark notification as read | USER |

---

### 📊 Dashboard (`/api/dashboard`)

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/api/dashboard/admin` | Admin dashboard stats | ADMIN |
| GET | `/api/dashboard/user` | User dashboard stats | USER |
| GET | `/api/dashboard/owner` | Parking owner dashboard stats | PARKING_OWNER |

---

### 📈 Analytics (`/api/analytics`)

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/api/analytics` | Get system-wide analytics (vehicle type & area-wise revenue, occupancy, etc.) | ADMIN |

---

### 📑 Reports (`/api/reports`)

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/api/reports/revenue?fromDate=&toDate=` | Revenue report | ADMIN |
| GET | `/api/reports/bookings?fromDate=&toDate=` | Booking report | ADMIN |
| GET | `/api/reports/payments?fromDate=&toDate=` | Payment report | ADMIN |

**Example – Revenue Report**
```http
GET /api/reports/revenue?fromDate=2026-06-01&toDate=2026-06-13
Authorization: Bearer <token>
```

---

## ⚙️ Setup & Installation

### Prerequisites

```
✅ Java 17+
✅ Maven 3.8+
✅ MySQL 8.0+
✅ Gmail account (for email OTP)
```

### Step 1: Clone the Repository

```bash
git clone https://github.com/your-username/Smart-Parking-Management-System.git
cd Smart-Parking-Management-System/smart-parking-management
```

### Step 2: Create MySQL Database

```sql
CREATE DATABASE smart_parking_db;
```

### Step 3: Configure `application.properties`

```properties
spring.application.name=smart-parking-management

# ── Database ──────────────────────────────────────────
spring.datasource.url=jdbc:mysql://localhost:3306/smart_parking_db
spring.datasource.username=root
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect

# ── JWT ───────────────────────────────────────────────
jwt.secret=your_jwt_secret_key
jwt.expiration=10800000

# ── Gmail SMTP ────────────────────────────────────────
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-16-char-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

> **Gmail App Password**: Go to Google Account → Security → 2-Step Verification → App Passwords → Generate a 16-character password.

---

## 🌐 Environment Configuration

| Property | Description | Must Change Before Pushing |
|----------|-------------|------------------------------|
| `spring.datasource.username` / `password` | MySQL credentials | ✅ |
| `jwt.secret` | Secret key used to sign JWT tokens | ✅ |
| `jwt.expiration` | Token validity (ms) | Optional |
| `spring.mail.username` / `password` | Gmail SMTP credentials (App Password) | ✅ |

---

## ▶ Running the Application

```bash
# Build
mvn clean install

# Run
mvn spring-boot:run
```

Server starts at: `http://localhost:8080`

---

## 📘 Swagger / API Docs

Once the application is running, Swagger UI is available at:

```
http://localhost:8080/swagger-ui/index.html
```

Raw OpenAPI spec:

```
http://localhost:8080/v3/api-docs
```

These endpoints are publicly accessible (no JWT required) for browsing API documentation.

---

## 🔐 Security Architecture

```
Every request to a protected endpoint:

1. Request arrives → JwtFilter.doFilterInternal()
2. Extract "Authorization: Bearer <token>" header
3. Parse token → extract email & role
4. Load user from DB by email (CustomUserDetailsService)
5. Validate token: not expired, signature valid
6. Set Authentication in SecurityContextHolder
7. Request proceeds to Controller (role-based access enforced)

Authorization (selected highlights):
├── /api/auth/**             → No token required
├── /api/users/**             → ADMIN (USER for own profile)
├── /api/vehicles/**          → USER (own) / ADMIN (all)
├── /api/parking-areas/**     → ADMIN, PARKING_OWNER (manage); USER/SECURITY (read)
├── /api/parking-slots/**     → ADMIN, PARKING_OWNER (manage); USER/SECURITY (read)
├── /api/parking-rates/**     → ADMIN, PARKING_OWNER
├── /api/bookings/**          → USER (own); ADMIN/SECURITY/PARKING_OWNER (all)
├── /api/payments/**          → USER (own); ADMIN (all)
├── /api/qr/**                → USER (generate); SECURITY (verify, check-in/out)
├── /api/notifications/**     → USER
├── /api/dashboard/admin      → ADMIN
├── /api/dashboard/user       → USER
├── /api/dashboard/owner      → PARKING_OWNER
├── /api/analytics            → ADMIN
└── /api/reports/**           → ADMIN
```

CORS is configured for:
* `http://127.0.0.1:5500` (VS Code Live Server)
* `http://localhost:5500`

To add more origins, update `SecurityConfig.java`:
```java
configuration.setAllowedOrigins(List.of(
    "http://127.0.0.1:5500",
    "http://localhost:5500"
));
```

---

## ❌ Error Handling

All errors follow this standard format (via `GlobalExceptionHandler`):

```json
{
  "timestamp": "2026-06-13T12:00:00",
  "status": 404,
  "error": "NOT_FOUND",
  "message": "Booking not found with id: 101",
  "path": "/api/bookings/101"
}
```

| Exception | HTTP Status | Error Code |
|-----------|-------------|------------|
| ResourceNotFoundException | 404 | NOT_FOUND |
| BadRequestException | 400 | BAD_REQUEST |
| AccessDeniedException | 403 | FORBIDDEN |
| UnauthorizedException | 401 | UNAUTHORIZED |
| EmailSendingException | 500 | EMAIL_ERROR |
| MethodArgumentNotValidException | 400 | VALIDATION_ERROR |
| Exception (generic) | 500 | INTERNAL_SERVER_ERROR |

---

## ⚠️ Security Note

This repository's `application.properties` currently contains **real database, JWT secret, and Gmail SMTP credentials**. Before pushing to a public repository:

1. Move sensitive values (`spring.datasource.password`, `jwt.secret`, `spring.mail.username`, `spring.mail.password`) to **environment variables**.
2. Reference them in `application.properties` using `${ENV_VAR_NAME}` syntax.
3. Add `application.properties` (or a local override file) to `.gitignore`, and commit an `application.properties.example` template instead.
4. Rotate the Gmail App Password and JWT secret if they have already been committed/pushed.

---
