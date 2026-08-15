Feature: Manage tasks

  Scenario: Creating a task adds it to the list
    Given the task list is empty
    When I create a task titled "Write the ADR"
    Then the task list contains a task titled "Write the ADR" that is not completed

  Scenario: Completing a task marks it as done
    Given I have created a task titled "Write the ADR"
    When I complete the task titled "Write the ADR"
    Then the task list contains a task titled "Write the ADR" that is completed

  Scenario: A task cannot be completed twice
    Given I have created a task titled "Write the ADR"
    And I have completed the task titled "Write the ADR"
    When I complete the task titled "Write the ADR" again
    Then the request fails with a conflict
