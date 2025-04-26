Job Recruitment Platform: A Spring Boot-powered job recruitment system that enables admins, recruiters, and candidates to manage job postings, applications, and user roles effectively.

Features:

1.Authentication & Authorization
  Secure JWT-based authentication.
  Role-based access control (ADMIN, RECRUITER, CANDIDATE).

2.Admin Panel
  Manage users (enable/disable accounts).
  View user statistics and system analytics.
  Soft delete jobs posted by recruiters.

3.Recruiter Functionality
  Post, update, and delete job listings.
  Fetch only their own job postings.
  View candidate applications and update their status.

4.Candidate Actions
  Search for jobs with filters (title, location).
  Apply to jobs.
  View and delete their own applications.

5.Additional Features
  Soft deletion for job postings.
  Application tracking (pending, accepted, rejected).
  User statistics dashboard for admins.

Tech Stack:
  Backend: Java, Spring Boot, Spring Security, JPA/Hibernate
  Database: PostgreSQL
  Authentication: JWT
  Build Tools: Maven
