# Customer Service API

## Overview
The Customer Service API is responsible for **managing customer records**, including customer creation, retrieval, deactivation, and loan limit settings.

## Features
- **Customer Management**
    - Create, update, and deactivate customer accounts
    - Retrieve customer details with filtering options
- **Loan Limit Management**
    - Set loan limits for customers
- **Loan Product Management**
    - Manage loan products including tenure, description, and activation status

## Technology Stack
- **Java 21**
- **Spring Boot 3.3.4**
- **MySQL**

## Database Schema

### `customers` – Stores customer details
| Column Name     | Type             | Description                          |  
|---------------|-----------------|--------------------------------|  
| `id`          | `BIGINT`         | Primary key, auto-generated |  
| `credit_score` | `INT`            | Credit score of the customer |  
| `email`       | `VARCHAR(255)`   | Email address (unique)       |  
| `name`        | `VARCHAR(255)`   | Full name                    |  
| `phone_number`| `VARCHAR(255)`   | Phone number                 |  
| `active`      | `INT`            | Active status                |  
| `date_created` | `DATETIME(6)`   | Date of record creation      |  
| `date_modified` | `DATETIME(6)`  | Date of last modification    |  
| `first_name`  | `VARCHAR(255)`   | First name                   |  
| `last_name`   | `VARCHAR(255)`   | Last name                    |  
| `national_id` | `VARCHAR(255)`   | National identification number |  
| `currency`    | `VARCHAR(255)`   | Default currency             |

### `loan_limits` – Stores loan limit details for customers
| Column Name     | Type             | Description                              |  
|---------------|-----------------|----------------------------------|  
| `id`          | `BIGINT`         | Primary key, auto-generated   |  
| `customer_id` | `BIGINT`         | Foreign key (customer ID)     |  
| `available_limit` | `DECIMAL(38,2)` | Available loan limit          |  
| `credit_limit` | `DECIMAL(38,2)`  | Maximum credit limit assigned |
| `currency`    | `VARCHAR(255)`   | Loan currency                  |

## API Endpoints

### Customer Management
- **Create Customer**
    - `POST /customer`
    - **Request Body:** `CustomerRequest`
    - **Response:** Returns created customer details

- **Retrieve Customers (with Filters)**
    - `GET /customer`
    - **Query Params:** Filters from `CustomerFilterRequest`
    - **Response:** List of customers

- **Deactivate Customer**
    - `PUT /customer/{id}/deactivate`
    - **Path Param:** `id` (Customer ID)
    - **Response:** Success message on deactivation

### Loan Limit Management
- **Set Loan Limit for Customer**
    - `POST /customer/{customerId}/set-loan-limit`
    - **Path Param:** `customerId` (Customer ID)
    - **Request Body:** `LoanLimitsDto`
    - **Response:** Confirmation message

### Loan Product Management
- **Create Loan Product**
    - `POST /loan-products`
    - **Request Body:** `LoanProductRequest`
    - **Response:** Confirmation message

- **Retrieve Loan Products**
    - `GET /loan-products`
    - **Response:** List of loan products

## Sample Request & Response


### Create Customer Request
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "+254712345671",
  "nationalId": "4234159",
  "income": 500,
  "currency":"KES",
  "customerNotificationPreferencesDto": {
    "emailNotifications": true,
    "smsNotifications": false,
    "pushNotifications": true
  }
}

```

### Create Customer Response
```json
{
    "success": true,
    "message": "Customer created successfully",
    "data": {
        "firstName": "John",
        "lastName": "Doe",
        "email": "john.doe@example.com",
        "phoneNumber": "+254712345671",
        "nationalId": "4234159",
        "income": 500,
        "loanLimits": {
            "creditLimit": 10000,
            "availableLimit": 0,
            "currency": "KES"
        },
        "customerPreferences": {
            "smsNotifications": false,
            "emailNotifications": true,
            "pushNotifications": true
        }
    }
}
```

### Fetch Customer Response
```json
{
    "success": true,
    "message": "Customer retrieved",
    "data": {
        "success": true,
        "message": "Customers retrieved successfully",
        "data": {
            "content": [
                {
                    "id": 8,
                    "firstName": "John",
                    "lastName": "Doe",
                    "email": "john.doe@example.com",
                    "phoneNumber": "+254712345671",
                    "nationalId": "4234159",
                    "income": 500.00,
                    "loanLimits": {
                        "creditLimit": 100000.00,
                        "availableLimit": 5250.00,
                        "currency": null
                    },
                    "customerPreferences": {
                        "smsNotifications": false,
                        "emailNotifications": true,
                        "pushNotifications": true
                    }
                }
            ],
            "pageNo": 0,
            "pageSize": 10,
            "totalElements": 1,
            "totalPages": 1,
            "last": true
        }
    }
}
```
### Running the Application
Ensure Java 21 is installed.
Run the application using:

`mvn spring-boot:run`

The service will start on `http://localhost:8083`.

## Docker Setup

To containerize the application, follow these steps:

### Build the JAR file:
```sh
mvn clean package -DskipTests
```

### Create a `Dockerfile` in the project root:
```dockerfile
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/customer-service-api.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Build and run the Docker container:
```sh
docker build -t customer-service-api .
docker run -p 8080:8080 customer-service-api
```

