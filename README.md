# <img src="https://github.com/user-attachments/assets/642b4bfd-cd04-49c9-8701-329a132bfb7d" width ="30" alt="Security GIF" title="Security GIF"> Spring Security Templates

A collection of Spring Boot projects demonstrating different user authentication strategies using **Spring Security**. This repository serves as a starting point for developers looking to implement secure and scalable authentication in their Spring applications.

1. [Simple JWT](https://github.com/mashisdev/spring-security-templates/tree/main/simple) authentication with secret key 🗝️

     ✅ **User Authentication**: user registration and login using JSON Web Tokens (JWT).

     ✅ **User Management**: CRUD operations for user entities. Users can only perform CRUD operations on their own user data, not on other users data.

     ✅ **Data Persistence** with Spring Data JPA and MySQL.

     ✅ **Mapping**: mapping between entity, DTO and requests for clean data transfer.

     ✅ **Validation**: input validation to maintain data integrity and improve API reliability.

     ✅ **Global Exception Handling**: includes a `@RestControllerAdvice` that provides standardized and descriptive error messages for the API. It returns a consistent JSON response including a timestamp, status, exception name, message, and the request path for easier debugging.

     ✅ **Rate Limiter**: implemented with Resilience4j to control the number of requests a client can make within a specific time frame, protecting the application from abuse and ensuring fair usage.

     ✅ **Swagger documentation** on `http://localhost:8080/swagger-ui/index.html`
   
3. [Role-based access control (RBAC)](https://github.com/mashisdev/spring-security-templates/tree/main/roles) with USER and ADMIN managed access to API endpoints 👑. Easy implementation with `@PreAuthorize("hasAuthority('ADMIN')")`
4. [RBAC with Email validation](https://github.com/mashisdev/spring-security-templates/tree/main/roles-email), with Java Mail Sender dependency 📬.
   
     ✅ **Email Verification**: User identity is verified via a 6-digit code sent to the user's email, which is then validated on the `/verify` endpoint. This verification code has an expiration time and can be refreshed by making a request to the `/resend` endpoint.

     ✅ **Password Reset**: initiated by a POST request to `/redeem-password`. This action sends a secure link with a unique token to the user's email, which is then used by the frontend to submit a POST request to the `/reset-password` endpoint to change the password.

6. [Multiple OAuth2 Providers](https://github.com/mashisdev/spring-security-templates/tree/main/multi-auth) including Google, Facebook, GitHub and LinkedIn 🔗

## Dependencies

#### Core Frameworks & Libraries
- Spring Boot
- Spring Data JPA
- MySql
- Spring Security
- Java JWT (JJWT)
- Spring Boot Starter Validation
- Lombok
- MapStruct

<img width="80" alt="Spring Boot image" src="https://github.com/user-attachments/assets/9f9c00e7-67f5-402c-9c51-0fe42d81f8c4" />
<img width="80" alt="Spring Data JPA image" src="https://github.com/user-attachments/assets/c4e065c9-0d16-4d1a-8294-e5b6a971fd4e" />
<img width="80" alt="MySql image" src="https://github.com/user-attachments/assets/eab77bfb-fa00-4f39-a2b2-e185152e620a" />
<img width="80" alt="Spring Security image" src="https://github.com/user-attachments/assets/669d73d0-f454-4c4f-a2ab-0f5ca3ff9ea3" />
<img width="80" alt="JJWT image" src="https://github.com/user-attachments/assets/c42b78cd-247e-4787-9cd8-d93b48e0e9dc" />
<img width="80" alt="Lombok image" src="https://github.com/user-attachments/assets/ed58df73-2c0f-4e4a-a421-c038d4a60fb2" />
<img width="80" alt="MapStruct image" src="https://github.com/user-attachments/assets/e8166396-65e4-4a1a-a725-158371cc3a06" />

#### Testing Tools
- JUnit + Mockito
- H2 Database

<img width="80" alt="JUnit image" src="https://github.com/user-attachments/assets/4f24e420-20d5-4607-afd7-f249a3a7ae8a" />
<img width="100" alt="Mockito image" src="https://github.com/user-attachments/assets/e2f3d467-b5cc-4367-8949-7dbe60bc1dfc" />
<img width="80" alt="H2 image" src="https://github.com/user-attachments/assets/7a179686-ce60-4250-aea7-13b9dfecc5d7" />

#### Devops & Docs
- Docker
- Swagger

<img width="80" alt="Docker image" src="https://github.com/user-attachments/assets/a45de7fe-6234-4734-be20-4efd48bd9207" />
<img width="80" alt="Swagger image" src="https://github.com/user-attachments/assets/7aad1e5b-6500-4a11-b705-7c2fe876e319" />

#### Situational dependencies
- Java Mail Sender
- OAuth2 Client

<img width="80" alt="Java Mail Sender" src="https://github.com/user-attachments/assets/78233584-ec8b-4851-83e9-7224da7ac478" />
<img width="80" alt="OAuth2 image" src="https://github.com/user-attachments/assets/e1aa011d-c2e5-4fa7-a4a2-74700004f083" />

## Getting Started
This guide will help you set up and run the project locally. You have 2 main options:

<details>
  <summary><b>Option 1: Using a local .env file</b></summary>

  
  1.  In the root of the project, create a new file named `.env` (that will be ignored by Git).
  
  2.  Copy the contents from the provided `.env.example` file into your new `.env` file.

  3.  Replace the placeholder values with your specific configurations (e.g., database credentials, secret key, token expirations, etc.)
  
      > **Note:** if the application requires some external service (like [Google App Passwords](https://support.google.com/mail/answer/185833?hl=en) for email functionality) you must generate the corresponding environment variables and replace the placeholders. Otherwise, the application will not work.

  4.  **Run the application** in your IDE with env variables

      <details>
        <summary><b>How to inject Enviroment Variables in IntelliJ IDEA:</b></summary>


        <img width="500" alt="step 1" src="https://github.com/user-attachments/assets/3fbf0d29-6544-42ed-80fd-bf727f32101f" />

        <img width="500" alt="step 2" src="https://github.com/user-attachments/assets/d17dd60b-68a8-4f2c-aff4-0ad0c48cbb77" />

      </details>

</details>

<details>
  <summary><b>Option 2: Using Docker 🐋</b></summary>


  1. Ensure you have [Docker](https://www.docker.com/) and [Docker Compose](https://docs.docker.com/compose/) installed on your machine.
  
  2. Replace all the enviroments variables you want on `docker-compose.yml` file:

      ```yaml
      services:
        app:
          # ...
          environment:
      ```

      > **Note:** if the application requires some external service (like [Google App Passwords](https://support.google.com/mail/answer/185833?hl=en) for email functionality) you must generate the corresponding environment variables and replace the placeholders. Otherwise, the application will not work.

  3. Run: `docker compose up`






