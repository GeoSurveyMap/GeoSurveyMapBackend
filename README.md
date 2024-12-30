# GeoSurveyMap Documentation

Welcome to the **GeoSurveyMap** project! This open-source application is designed for managing geological data using **Kotlin**, **Spring Boot**, **PostgreSQL**, and **MinIO**. This documentation provides contributors with a guide to navigate and extend the codebase.

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Architecture](#architecture)
3. [Key Features](#key-features)
4. [Folder Structure](#folder-structure)
5. [Endpoints](#endpoints)
    - [Admin Endpoints](#admin-endpoints)
    - [Survey Endpoints](#survey-endpoints)
    - [User Endpoints](#user-endpoints)
6. [Security](#security)
7. [How to Contribute](#how-to-contribute)
8. [Setup Instructions](#setup-instructions)
9. [Getting Started](#getting-started)

---

## Project Overview
GeoSurveyMap is an application for managing geological survey data. It supports:

- Data storage using **PostgreSQL** for relational data and **MinIO** for file storage.
- Filtering and managing surveys, locations, and user data.
- Spatial filtering capabilities for surveys and locations.

---

## Architecture

The application follows a layered architecture with the following components:

- **Controller Layer:** Handles HTTP requests and responses.
- **Service Layer:** Contains business logic.
- **Repository Layer:** Interfaces with the database using Spring Data JPA.
- **Utility Layer:** Provides reusable utilities for API responses, error handling, etc.

---

## Key Features

1. **Role-Based Access Control:** Features for administrators and regular users.
2. **Survey Management:** Create, upload files, filter, and fetch surveys.
3. **Location Management:** Spatial filtering using bounding boxes and radius.
4. **User Management:** User registration, banning, and permission updates.
5. **Report Generation:** Download Excel reports with advanced filtering options.

---

## Folder Structure

Below is the folder structure of the project:

- `admin/`
    - `AdminController.kt`: Handles admin-specific endpoints like reports, user management, and survey approvals.
- `apiutils/`
    - Contains utilities for handling API responses and exceptions.
- `config/`
    - `SecurityConfig.kt`: Configures security (OAuth2, JWT).
    - `MinioConfig.kt`: Configures MinIO for file storage.
- `location/`
    - Handles location-based logic, including filtering by coordinates or bounding boxes.
- `survey/`
    - Handles survey management (CRUD operations, file uploads, filtering by category).
- `user/`
    - Manages user data, permissions, and account status.

---

## Endpoints

### Admin Endpoints

#### Download Report
- **Endpoint:** `GET /api/v1/admin/report`
- **Description:** Download survey data in Excel format with advanced filtering.
- **Permissions:** Requires `ADMIN` role.

#### Approve/Reject Survey
- **Endpoint:** `PUT /api/v1/admin/{surveyId}/status/{status}`
- **Description:** Accept or reject a survey.
- **Permissions:** Requires `ADMIN` role.

#### Delete User
- **Endpoint:** `DELETE /api/v1/admin/users/{kindeId}/delete`
- **Description:** Deletes a user and all associated surveys.
- **Permissions:** Requires `ADMIN` role.

### Survey Endpoints

#### Create Survey
- **Endpoint:** `POST /api/v1/survey/create`
- **Description:** Create a new survey with optional file upload.
- **Permissions:** Requires authentication.

#### Upload Survey File
- **Endpoint:** `POST /api/v1/survey/upload`
- **Description:** Upload a file associated with a survey.
- **Permissions:** Requires authentication.

#### Get Surveys by Location
- **Endpoint:** `GET /api/v1/survey`
- **Description:** Retrieve surveys filtered by location, radius, and categories.
- **Permissions:** Public.

### User Endpoints

#### Register User
- **Endpoint:** `POST /api/v1/user`
- **Description:** Register a new user.
- **Permissions:** Public.

#### Update User Permissions
- **Endpoint:** `PUT /api/v1/user/update/{kindeId}`
- **Description:** Update a user's permissions.
- **Permissions:** Requires `ADMIN` role.

---

## Security

The application uses **JWT (JSON Web Token)** for authentication, managed by [Kinde](https://kinde.com). 
The backend validates the **JWT token** received from the frontend. Key security features:


- Role-based access (`USER` and `ADMIN`).
- CORS configuration to allow specific origins.
- OAuth2 resource server configuration.
---

## How to Contribute

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/GeoSurveyMap/GeoSurveyMapBackend.git
   ```
2. **Set Up Your Environment:** Follow the [Setup Instructions](#setup-instructions).
3. **Create a Branch:**
   ```bash
   git checkout -b feature/your-feature
   ```
4. **Make Changes:**
    - Follow coding standards.
    - Write unit tests for your changes.
5. **Submit a Pull Request:** Describe your changes and link any related issues.

---

## Setup Instructions

### Prerequisites

- Running Docker.

### How to use the application?

Before running the application, set the executable mode for the `wait-for-it.sh` file from the following directory:
`dev/docker/`
```shell
chmod +x wait-for-it.sh
```

Then just use the `start.sh` script from the `dev/` directory:
```shell
sh start-remote.sh
```

The script above runs `dev/docker/compose-dev.yml` with all needed containers to use the `API`:

- `postgres` with `postgis` extension for managing users, surveys, and geospatial data.
- Backend service written in `Kotlin` and `Spring Boot`.
- `minio` for storing images from surveys (needed bucket is automatically created by the `create-buckets` service).

The local environment will look as follows:

- Backend service API will be available under the following URL: [http://localhost:8080/swagger-ui/index.html#/](http://localhost:8080/swagger-ui/index.html#/)
- MinIO is available under the following URL: [http://localhost:9090/buckets](http://localhost:9090/buckets)

To access MinIO, use the following credentials:

- **User:** `minioadmin`
- **Password:** `minioadmin`


## Architecture
![Architecture](doc/img/architecture.png)
---

Thank you for contributing to GeoSurveyMap! Together, we can build an exceptional tool for geological data management.

