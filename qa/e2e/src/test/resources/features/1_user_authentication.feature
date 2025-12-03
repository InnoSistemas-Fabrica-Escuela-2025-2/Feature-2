Feature: User Authentication
  As a user
  I want to authenticate myself
  So that I can access the system

  Scenario: Unsuccessful login with invalid credentials
    Given I am on the login page
    When I enter the email "emmanuel@udea.edu.co" and the password "calidadqa2025"
    And I click the login button
    Then I should see the error message

  Scenario: Successful login with valid credentials
    Given I am on the login page
    When I enter the email "geraldin@udea.edu.co" and the password "geraldin"
    And I click the login button
    Then I should see the user name displayed



