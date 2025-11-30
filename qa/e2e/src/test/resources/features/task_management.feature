Feature: Task Management
    As a student
    I want to create and manage tasks within my projects
    So that I can track project progress

    Background:
        Given the user "juan.perez" is authenticated
        And a project named "Software Quality Project" exists
        And the user has opened the project dashboard

    Scenario: Create a task with complete information
        When the user clicks on add task button
        And the user fills the task form with:
            | field       | value                    |
            | name        | Design test scenarios    |
            | description | Create Gherkin scenarios |
            | status      | Pending                  |
            | priority    | High                     |
            | due_date    | 2025-12-10               |
        And the user saves the task
        Then the task should appear in the task list
        And a confirmation message should be displayed
        And the task status should show "Pending"

    Scenario: Create a basic task with minimum required data
        When the user clicks on add task button
        And the user enters task name "Code review"
        And the user selects status "Pending"
        And the user saves the task
        Then the task "Code review" should be visible
        And the task should appear with default priority

    Scenario: Update task status
        Given a task "Implement tests" exists with status "Pending"
        When the user clicks on the task
        And the user changes status to "In Progress"
        And the user saves the changes
        Then the task status should be updated to "In Progress"
        And the dashboard should reflect the change
