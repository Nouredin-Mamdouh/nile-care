# 🌊 NileCare: Learning & Wellness Platform

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge\&logo=java\&logoColor=white)
![Spring Framework](https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge\&logo=spring\&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge\&logo=springsecurity\&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge\&logo=mysql\&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-005F0F?style=for-the-badge\&logo=Thymeleaf\&logoColor=white)
![Bootstrap](https://img.shields.io/badge/Bootstrap-563D7C?style=for-the-badge\&logo=bootstrap\&logoColor=white)

NileCare is a comprehensive, full-stack web application designed to bridge the gap between academic progress and student mental health. It provides a secure, centralized environment where students can access learning materials, schedule counseling sessions, and track their wellness — all overseen by a robust administrative backend.

---

## 🚀 Key Features

### 🛡️ Core Infrastructure & Security (My Focus)

* **Strict Role-Based Access Control (RBAC):**
  Multi-tiered security architecture distinguishing between `STUDENT`, `COUNSELOR`, and `ADMIN` roles, utilizing **Spring Security** to protect endpoints and sensitive data.

* **Admin Command Center:**
  A real-time dashboard featuring system health statistics, pending user verifications, and quick-action management tools.

* **Intelligent Error Handling:**
  Custom controller-based error routing that gracefully handles 404/500 errors while maintaining security contexts and safely redirecting users to their respective role-based dashboards.

* **Robust User Management:**
  Full CRUD capabilities for system users, including a secure verification pipeline for new counselors and staff members.

---

### 📚 Learning & Wellness Modules (Team Contributions)

* **Learning Management System (LMS):**
  Interactive learning modules with structured content delivery and student progress tracking.

* **Counseling Services:**
  Dynamic time-slot generation and secure appointment scheduling between students and counselors.

* **Assessments & AI Support:**
  Integrated student assessments and an AI-powered chatbot for immediate assistance.

* **Analytics & Feedback:**
  System-wide reporting with optimized CSV data streaming and a closed-loop feedback system.

---

## 🛠️ Technical Stack

### Backend

* Java 17+
* Spring Framework (Spring MVC)
* Hibernate ORM
* HikariCP Connection Pooling

### Security

* Spring Security

  * Session Management
  * Ant Matchers
  * Role-Based Authorization

### Database

* MySQL 8.0 (Standalone Service)

### Frontend

* HTML5
* CSS3
* Vanilla JavaScript
* Bootstrap 5

### Template Engine

* Thymeleaf
* Thymeleaf Spring Security Dialect

---

## 📸 System Previews

* Login Page
<img width="2554" height="1361" alt="image" src="https://github.com/user-attachments/assets/532cfae8-d6e6-43ee-94a2-cbfb1eb9b450" />

* Admin Dashboard (Stats & Quick Actions)
<img width="2556" height="1360" alt="image" src="https://github.com/user-attachments/assets/3baef06a-fca0-4407-a3e6-63d760376345" />

* Counselor Dashboard
<img width="2526" height="1359" alt="image" src="https://github.com/user-attachments/assets/629af36a-778f-48e5-8e34-83b0b7cd61b2" />

* Student Learning View
<img width="2559" height="1362" alt="image" src="https://github.com/user-attachments/assets/0c2aeb5a-660a-4d96-b357-e6713d3acd82" />


---

## ⚙️ Local Installation & Setup

### Prerequisites

* Java 17+
* Maven
* Apache Tomcat
* MySQL 8.0

---

### 1️⃣ Clone the Repository

```bash
git clone https://github.com/yourusername/nilecare.git
cd nilecare
```

---

### 2️⃣ Configure the Database

1. Create a new MySQL database:

```sql
CREATE DATABASE nilecare_db;
```

2. Open your database configuration file (`RootConfig.java` or `application.properties`).

3. Update the credentials:

```properties
db.url=jdbc:mysql://localhost:3306/nilecare_db?useSSL=false&serverTimezone=UTC
db.username=root
db.password=YOUR_LOCAL_PASSWORD
hibernate.hbm2ddl.auto=update
```

> ⚠️ Ensure your MySQL service is running and requires a password.
> Passwordless root users may cause HikariCP connection rejections.

---

### 3️⃣ Build and Deploy

Since this is a standard Spring MVC application, build the WAR file:

```bash
mvn clean install
```

Deploy the generated `.war` file to your Apache Tomcat `webapps` directory and start the server.

---

### Alternative: Run via Maven Plugin

If your team uses a Maven Tomcat/Cargo plugin:

```bash
mvn cargo:run
# OR
mvn tomcat7:run
```

---

### 🌐 Access the Application

```
http://localhost:8080/
```

*(Or your configured application context path)*

---

## 👥 Team & Work Breakdown

This project was developed collaboratively by a team of five developers:

* **Ammar**
  System Architecture, Spring Security (Authentication & RBAC), Admin Dashboard, User Management.

* **Ahmed**
  Learning Modules (LMS) & Student Progress Tracking.

* **Ali**
  Counseling Services (Time-slot Generation & Appointments) & Help Requests.

* **Nouredin**
  Student Assessments & Chatbot Integration.

* **Amr**
  Analytics, CSV Data Export & Student Feedback Loop.
