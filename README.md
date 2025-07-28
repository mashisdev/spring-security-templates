# <img src="https://github.com/user-attachments/assets/642b4bfd-cd04-49c9-8701-329a132bfb7d" width ="30" alt="Security GIF" title="Security GIF"> Spring Security Templates

A collection of Spring Boot projects demonstrating different user authentication strategies using **Spring Security**. This repository serves as a starting point for developers looking to implement secure and scalable authentication in their Spring applications.

1. [Simple JWT](https://github.com/mashisdev/spring-security-templates/tree/main/simple) authentication with secret key 🗝️
2. [Role-based access control (RBAC)](https://github.com/mashisdev/spring-security-templates/tree/main/roles) with USER and ADMIN managed access to API endpoints 👑. Easy implementation with `@PreAuthorize("hasAuthority('ADMIN')")`
3. [RBAC with Email validation](https://github.com/mashisdev/spring-security-templates/tree/main/roles-email), that enables new user registration with a 6-digit email verification code, ensuring the authenticity of the user's email address 📬.
4. [Multiple OAuth2 Providers](https://github.com/mashisdev/spring-security-templates/tree/main/multi-auth) including Google, Facebook, GitHub and LinkedIn 🔗
