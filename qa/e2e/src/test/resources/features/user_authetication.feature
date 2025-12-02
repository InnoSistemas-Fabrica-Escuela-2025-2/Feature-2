Feature: User Authentication
  As a user
  I want to authenticate myself
  So that I can access protected resources

  Scenario: Successful login with valid credentials
    Given I am on the login page
    When I enter valid credentials
    Then I should be redirected to the dashboard

  Scenario: Unsuccessful login with invalid credentials
    Given I am on the login page
    When I enter invalid credentials
    And I click the login button
    Then I should see an error message indicating invalid login

  Scenario: Attempt number reset after successful login
    Given I am on the login page
    When and my account is not blocked
    And I send invalid credentials three times
    And I log in with valid credentials
    And I log out
    Then the attempt counter should be reseted

  Scenario Outline: Validations when attempting to log in without revealing information
    Given I am on the login page
    When I enter the email "<email>" and the password "<password>"
    And I send the credentials
    Then the message "<expectedMessage>" should be displayed
    Examples:
      | email                  | password     | expectedMessage                                     |
      | ""                     | ""           | "Por favor, completa todos los campos obligatorios" |
      | ""                     | "5encalidad" | "Por favor, completa todos los campos obligatorios" |
      | "mail@mail.com"        | ""           | "Por favor, completa todos los campos obligatorios" |
      | "mail@mail.com"        | "5encalidad" | "La contraseña debe tener al menos 6 caracteres"    |
      | "mailmail.com"         | "5encalidad" | "Por favor, ingresa un correo electrónico válido"   |
      | "nicolas@udea.edu.co"  | "5encalidad" | "Correo o contraseña incorrectos"                   |
      | "nicolas2@udea.edu.co" | "5encalidad" | "Correo o contraseña incorrectos"                   |

  Scenario: Account block after multiple failed attempts
    Given I am on the login page
    And my account is not blocked
    When I send invalid credentials four more times
    Then my account should be blocked
    And I should see a message indicating my account is locked



