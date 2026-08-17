Feature: Manage tasks

  Scenario: Creating a task adds it to the list
    Given the task list is empty
    When I create a task titled "Write the ADR"
    Then the task list contains a task titled "Write the ADR" with status "TODO"

  Scenario: Changing a task's status updates it
    Given I have created a task titled "Write the ADR"
    When I change the status of the task titled "Write the ADR" to "DONE"
    Then the task list contains a task titled "Write the ADR" with status "DONE"

  Scenario: A task cannot be moved to its current status again
    Given I have created a task titled "Write the ADR"
    And I have changed the status of the task titled "Write the ADR" to "DONE"
    When I change the status of the task titled "Write the ADR" to "DONE" again
    Then the request fails with a conflict

  Scenario: Labels can be added and searched
    Given I have created a task titled "Write the ADR"
    When I add the label "backend" to the task titled "Write the ADR"
    Then searching by label "backend" returns the task titled "Write the ADR"
