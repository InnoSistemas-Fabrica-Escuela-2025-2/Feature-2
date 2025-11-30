Feature: User Authentication
    As a platform user
    I want to authenticate with my credentials
    So that I can access the platform features

    Background:
        Given the user is on the login page

    Scenario: Successful login with valid credentials
        When the user enters username "juan.perez"
        And the user enters password "Test123!"
        And the user clicks the login button
        Then the user should be redirected to the dashboard
        And the welcome message should be displayed

    Scenario: Failed login with invalid password
        When the user enters username "juan.perez"
        And the user enters password "wrongpassword"
        And the user clicks the login button
        Then an error message should be displayed
        And the user should remain on the login page

    Scenario: Login attempt with empty fields
        When the user clicks the login button
        Then a validation message for username should appear
        And the login button should be disabled
