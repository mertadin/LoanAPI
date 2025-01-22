# Loan API

Loan API is a Spring Boot application designed to manage loan-related operations, such as loan requests, user authentication, and installment management. The application uses Spring Security for secure access control and H2 as an in-memory database for development and testing purposes.

## Features

- **User Authentication**: Role-based access control for Admin and Customer roles.
- **Loan Management**: APIs for managing loans and their details.
- **Installment Management**: APIs for handling loan installment operations.
- **Spring Security Integration**: Secure endpoints with authentication and authorization mechanisms.
- **H2 Database**: Preconfigured in-memory database for development.

## Prerequisites

- Java 17 or higher
- Maven 3.8+
- Spring Boot 3.1+

## Installation

1. Clone the repository:
    ```bash
    git clone <repository-url>
    cd loan-api
    ```
2. Build the application using Maven:
    ```bash
    mvn clean install
    ```
3. Run the application:
    ```bash
    mvn spring-boot:run
    ```

## Default Configuration

- Default roles:
  - `ADMIN`
  - `CUSTOMER`
- Default endpoints:
  - `/admin/**` (Admin access only)
  - `/customer/**` (Customer access only)
- Default user credentials (auto-generated if not configured manually):
  - Username: `user`
  - Password: Check logs for "Using generated security password".

## APIs

### Installment Service Endpoints

#### 1. Create Installment
- **Endpoint**: `/installments`
- **Method**: POST
- **Request Body**:
  ```json
  {
      "loanId": 123,
      "amount": 1000.0,
      "dueDate": "2025-02-15"
  }
  ```
- **Response**:
  ```json
  {
      "id": 1,
      "loanId": 123,
      "amount": 1000.0,
      "dueDate": "2025-02-15",
      "status": "PENDING"
  }
  ```

#### 2. Get Installment by ID
- **Endpoint**: `/installments/{id}`
- **Method**: GET
- **Response**:
  ```json
  {
      "id": 1,
      "loanId": 123,
      "amount": 1000.0,
      "dueDate": "2025-02-15",
      "status": "PENDING"
  }
  ```

#### 3. Update Installment Status
- **Endpoint**: `/installments/{id}/status`
- **Method**: PUT
- **Request Body**:
  ```json
  {
      "status": "PAID"
  }
  ```
- **Response**:
  ```json
  {
      "id": 1,
      "loanId": 123,
      "amount": 1000.0,
      "dueDate": "2025-02-15",
      "status": "PAID"
  }
  ```

#### 4. Delete Installment
- **Endpoint**: `/installments/{id}`
- **Method**: DELETE
- **Response**: HTTP 204 No Content

## How to Contribute

1. Fork the repository.
2. Create a feature branch:
    ```bash
    git checkout -b feature-name
    ```
3. Commit your changes:
    ```bash
    git commit -m "Add new feature"
    ```
4. Push to the branch:
    ```bash
    git push origin feature-name
    ```
5. Open a pull request.

## License

This project is licensed under the MIT License. See the LICENSE file for details.

