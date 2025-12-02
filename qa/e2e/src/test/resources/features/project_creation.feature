Feature: Project Creation
    As a student
    I want to create new projects
    So that I can organize my academic work

    Background:
        Given the user "juan.perez" is authenticated
        And the user is on the projects page

    Scenario: Create a project with all required fields
        When the user clicks on create project button
        And the user fills the project form with:
            | field         | value                            |
            | name          | Software Quality Project         |
            | description   | Automated testing implementation |
            | objectives    | Implement E2E tests              |
            | delivery_date | 2025-12-30                       |
            | team          | Quality Assurance                |
        And the user submits the project form
        Then the project should appear in the projects list
        And a success message should be displayed
        And the project name should be "Software Quality Project"

    Scenario: Create project with only mandatory fields
        When the user clicks on create project button
        And the user fills the project name with "Quick Test Project"
        And the user fills the delivery date with "2025-12-01"
        And the user submits the project form
        Then the project "Quick Test Project" should be created
        And the project should appear in the projects list

    Scenario: Attempt to create project without required fields
        When the user clicks on create project button
        And the user submits the project form
        Then validation errors should be displayed
        And the project should not be created
