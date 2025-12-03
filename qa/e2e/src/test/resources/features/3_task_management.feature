Feature: Task Management
    As a student
    I want to create and manage tasks within my projects
    So that I can track project progress

    Scenario: Create a task with complete information
        Given the user "geraldin@udea.edu.co" is authenticated
        And the user is on the projects page
        When the user opens the project "Proyecto QA E2E"
        And the user clicks on new task button
        And the user fills the task form with:
            | field       | value                      |
            | title       | Diseñar escenarios         |
            | description | Crear escenarios Gherkin   |
            | project     | Proyecto QA E2E            |
            | due_date    | 2025-12-15                 |
        And the user selects any responsible person
        And the user sets the task due date
        And the user submits the task form
        Then the task should appear in the task list


