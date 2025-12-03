Feature: Project Creation
    As a student
    I want to create new projects
    So that I can organize my academic work

    Scenario: Attempt to create project without required fields
        Given the user "geraldin@udea.edu.co" is authenticated
        And the user is on the projects page
        When the user clicks on create project button
        And the user fills partial project form with:
            | field      | value                  |
            | name       | Proyecto Incompleto    |
            | objectives | Validar validaciones   |
        And the user submits the project form
        Then a validation error should be displayed with message "La descripción es obligatoria"
        And the user clicks cancel button
        And the user confirms cancel action

    Scenario Outline: Create project with all required fields
        Given the user is on the projects page
        When the user clicks on create project button
        And the user fills the complete project form with:
            | field         | value            |
            | name          | <project_name>   |
            | description   | <description>    |
            | objectives    | <objectives>     |
            | delivery_date | <delivery_date>  |
            | team          | <team>           |
        And the user submits the project form
        Then the project should appear in the projects list

        Examples:
            | project_name           | description                      | objectives                    | delivery_date | team     |
            | Proyecto QA E2E        | Prueba automatizada completa     | Validar funcionalidad E2E     | 2025-12-30    | Equipo 1 |
            | Sistema de Biblioteca  | Gestión de préstamos de libros   | Automatizar préstamos         | 2025-12-25    | Equipo 2 |
            | Portal Académico       | Plataforma de gestión estudiantil| Centralizar información       | 2025-12-28    | Equipo 3 |
