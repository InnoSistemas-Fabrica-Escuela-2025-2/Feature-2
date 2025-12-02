Feature: Task Management
    As a student
    I want to create and manage tasks within my projects
    So that I can track project progress

    Background:
        Given the user "geraldin@udea.edu.co" is authenticated
        And a project named "Software Quality Project" exists
        And the user has opened the project dashboard
        And the user is inside the project "Software Quality Project"

    Scenario: Create a task with complete information
        When the user clicks on add task button
        And the user fills the task form with:
            | field       | value                    |
            | name        | Design test scenarios    |
            | description | Create Gherkin scenarios |
            | project     | Software Quality Project |
            | responsible | Juan Perez               |
            | due_date    | 2025-12-10               |
        And the user saves the task
        Then the task should appear in the task list
        And a confirmation message should be displayed


