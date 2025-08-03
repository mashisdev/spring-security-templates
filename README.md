# <img src="https://github.com/user-attachments/assets/642b4bfd-cd04-49c9-8701-329a132bfb7d" width ="30" alt="Security GIF" title="Security GIF"> Spring Security Templates

A collection of Spring Boot projects demonstrating different user authentication strategies using **Spring Security**. This repository serves as a starting point for developers looking to implement secure and scalable authentication in their Spring applications.

1. [Simple JWT](https://github.com/mashisdev/spring-security-templates/tree/main/simple) authentication with secret key 🗝️
2. [Role-based access control (RBAC)](https://github.com/mashisdev/spring-security-templates/tree/main/roles) with USER and ADMIN managed access to API endpoints 👑. Easy implementation with `@PreAuthorize("hasAuthority('ADMIN')")`
3. [RBAC with Email validation](https://github.com/mashisdev/spring-security-templates/tree/main/roles-email), that enables new user registration with a 6-digit email verification code, ensuring the authenticity of the user's email address 📬.
4. [Multiple OAuth2 Providers](https://github.com/mashisdev/spring-security-templates/tree/main/multi-auth) including Google, Facebook, GitHub and LinkedIn 🔗

### Getting Started
This guide will help you set up and run the project locally. You have 2 main options:

<details>
  <summary><b>Option 1: Using a local .env file</b></summary>

  
  1.  In the root of the project, create a new file named `.env` (that will be ignored by Git).
  
  2.  Copy the contents from the provided `.env.example` file into your new `.env` file.

  3.  Replace the placeholder values with your specific configurations (e.g., database credentials, secret key, token expirations, etc.)
  
      > Note: if the application requires some external service (like [Google App Passwords](https://support.google.com/mail/answer/185833?hl=es) for mailing) you must generate them and replace the example file ones. 

  4.  **Run the application** in your IDE with env variables

      <details>
        <summary><b>How to inject Enviroment Variables in IntelliJ IDEA:</b></summary>

        
      </details>

</details>

<details>
  <summary><b>Option 2: Using Docker 🐋</b></summary>


  1. Ensure you have [Docker](https://www.docker.com/) and [Docker Compose](https://docs.docker.com/compose/) installed on your machine.
  
  2. Replace all the enviroments variables nedeed on `docker-compose.yml` file:

      ```yaml
      services:
        app:
          # ...
          environment:
      ```

  3. Run: `docker compose up`
