IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'onboarding_portal')
BEGIN
    CREATE DATABASE onboarding_portal;
END
