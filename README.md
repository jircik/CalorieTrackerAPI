# Calorie Tracker API

A backend REST API for tracking meals and nutritional information, built with **Java, Spring Boot, and PostgreSQL**.
The application integrates with the **FatSecret API** to retrieve nutritional data and implements caching and persistence strategies to optimize performance and reduce external API calls.

This project was designed as a **progressively evolving system (V1 → V2 → V3)**, focusing on clean architecture, scalability, and maintainability.

---

# Features

### User Management

* Create users
* Retrieve user information
* Retrieve user daily nutrition summary

### Meal Management

* Create meals
* Retrieve meals by date
* Retrieve meal summaries

### Food Tracking

* Add foods to meals
* Automatic nutritional calculation based on quantity
* Integration with FatSecret food database

### Nutritional Calculations

* Calories
* Carbohydrates
* Protein
* Fat

### Performance Optimizations

* **In-memory caching (Caffeine)**
* **Persistent food cache in database**
* Avoid unnecessary calls to FatSecret API
* Efficient query design

### External Integration

* **FatSecret API (OAuth2 Client Credentials)**

### Testing

* Controller layer tests using **Spring Boot Test / MockMvc**

---

# Tech Stack

* **Java 21**
* **Spring Boot**
* Spring Web
* Spring Data JPA
* Spring Validation
* PostgreSQL
* WebClient (Reactive HTTP client)
* Caffeine Cache
* Maven
* JUnit / MockMvc

---

# Architecture

The project follows a layered architecture:

```
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

Key principles used:

* Separation of concerns
* DTO-based API responses
* External API abstraction
* Repository pattern
* Clean service logic

---

# Database Model

Main entities:

```
User
 └── Meal
       └── MealFood
```

Additionally, the system stores external nutrition data to reduce API calls:

```
FoodNutritionCache
```

This allows the system to:

* reuse nutritional data
* minimize requests to FatSecret

---

# API Endpoints

## Users

Create user

```
POST /users
```

Get user

```
GET /users/{id}
```

Get all users

```
GET /users
```

Get daily nutrition summary

```
GET /users/{id}/daily-summary?date=YYYY-MM-DD
```

Get meals by date

```
GET /users/{id}/meals?date=YYYY-MM-DD
```

---

## Meals

Create meal

```
POST /meals
```

Get meal summary

```
GET /meals/{mealId}/summary
```

---

## Meal Foods

Add food to meal

```
POST /meals/{mealId}/foods
```

---

# Example Response

Example response for retrieving meals by date:

```json
{
  "userId": 1,
  "date": "2026-03-06",
  "meals": [
    {
      "mealId": 1,
      "dateTime": "2026-03-06T12:30:00",
      "foods": [
        {
          "foodName": "rice",
          "quantity": 150,
          "unit": "g",
          "calories": 193,
          "carbs": 41.85,
          "protein": 3.99,
          "fat": 0.42
        }
      ]
    }
  ]
}
```

---

# External API Integration

The application integrates with the **FatSecret API** to retrieve nutritional information.

Flow:

```
Food name
   ↓
Search FatSecret API
   ↓
Retrieve Food ID
   ↓
Fetch nutritional details
   ↓
Calculate nutrition for requested quantity
```

Caching layers prevent redundant external requests.

---

# Caching Strategy

Two levels of caching are implemented:

### 1. In-Memory Cache (Caffeine)

Used for quick access to frequently requested foods.

### 2. Persistent Cache (Database)

Stores previously retrieved nutritional data.

Benefits:

* Reduces external API calls
* Improves performance
* Allows reuse of known foods

---

# Roadmap

## V1 (Current)

Core backend features:

* Users
* Meals
* Food tracking
* Nutritional calculations
* FatSecret integration
* Caching
* Automated tests

---

## V2 (Planned)

* MealType (Breakfast, Lunch, Dinner, Snacks)
* Delete meal
* Delete meal food
* Update meal food quantity
* Weekly and monthly summaries
* User nutritional targets
* Automatic meal creation

---

## V3 (Planned)

* Frontend application
* JWT authentication
* User login
* Full client integration

---

# Author

Arthur Jircik Cronemberger
