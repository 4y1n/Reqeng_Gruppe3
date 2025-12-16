Feature: Manage customer accounts
  As a customer
  I want to manage my account and credit
  so that I can use the chargers

  Background:
    Given a new FillingStationNetwork


  Scenario: create a new customer account
    When customer creates a customer account with the unique id "001"
    And sets the customer name to "Alissa Strom"
    And sets the customer email to "alissa@strom.at"
    Then the customer account "001" is part of the customer account list
    And the customer name is "Alissa Strom"
    And the customer email is "alissa@strom.at"
    And the customer credit is 0.0


  Scenario: view an existing customer account
    Given the following customer accounts exist:
      | Id  | Name         | Email            | Credit |
      | 001 | Alissa Strom | alissa@strom.at  |  50.0  |
      | 002 | Eduard Power | eduard@power.at  |   0.0  |
    When customer views the customer account with id "001"
    Then the customer name is "Alissa Strom"
    And the customer email is "alissa@strom.at"
    And the customer credit is 50.0



  Scenario: update an existing customer account
    Given the following customer accounts exist:
      | Id  | Name         | Email            | Credit |
      | 001 | Alissa Strom | alissa@strom.at  |  50.0  |
    When customer updates the customer name of "001" to "Alissa Power"
    And customer updates the customer email of "001" to "alissa@power.at"
    Then the customer name is "Alissa Power"
    And the customer email is "alissa@power.at"



  Scenario: delete an existing customer account
    Given the following customer accounts exist:
      | Id  | Name         | Email            | Credit |
      | 001 | Alissa Strom | alissa@strom.at  |  50.0  |
      | 002 | Eduard Power | eduard@power.at  |   0.0  |
    When customer deletes the customer account "002"
    Then the customer account "002" no longer exists
    And the number of customer accounts is 1



  Scenario: add credit to an existing customer account
    Given an existing customer account with id "003"
    And the customer has a credit of 10.0
    When customer adds 15.0 credit to the customer account
    Then the customer account "003" has a credit of 25.0

# Error und Edge Cases:
  Scenario: Edge Case - updateCustomer mit null-Werten ändert nichts
    Given an existing customer account with id "C1"
    And sets the customer name to "Max Mustermann"
    And sets the customer email to "max@example.com"
    When customer updates the customer name of "C1" to null
    Then the customer name is "Max Mustermann"
    And the customer email is "max@example.com"


  Scenario: Error Case - creating a duplicate customer id
    Given an existing customer account with id "DUP1"
    When customer attempts to create a customer account with the existing id "DUP1"
    Then an error about duplicate customer is raised
