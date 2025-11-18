Feature: Manage customer accounts
  As a customer
  I want to manage my account and credit
  so that I can use the chargers

  Background:
    Given a new FillingStationNetwork

  # US: create customer account
  Scenario: create a new customer account
    When customer creates a customer account with the unique id "001"
    And sets the customer name to "Alissa Strom"
    And sets the customer email to "alissa@strom.at"
    Then the customer account "001" is part of the customer account list
    And the customer name is "Alissa Strom"
    And the customer email is "alissa@strom.at"
    And the customer credit is 0.0

  # US: view customer account
  Scenario: view an existing customer account
    Given the following customer accounts exist:
      | Id  | Name         | Email            | Credit |
      | 001 | Alissa Strom | alissa@strom.at  |  50.0  |
      | 002 | Eduard Power | eduard@power.at  |   0.0  |
    When customer views the customer account with id "001"
    Then the customer name is "Alissa Strom"
    And the customer email is "alissa@strom.at"
    And the customer credit is 50.0

  # US: add customer credit to customer account
  Scenario: add credit to an existing customer account
    Given an existing customer account with id "003"
    And the customer has a credit of 10.0
    When customer adds 15.0 credit to the customer account
    Then the customer account "003" has a credit of 25.0

  # Optional: view all customer accounts as a list
  Scenario: view all customer accounts
    Given the following customer accounts exist:
      | Id  | Name         | Email            | Credit |
      | 001 | Alissa Strom | alissa@strom.at  |  50.0  |
      | 002 | Eduard Power | eduard@power.at  |   0.0  |
    When owner views the list of all customer accounts
    Then the number of customer accounts is 2
    And viewing the customer account list shows the following output:
      """
      Customers:
        001: Alissa Strom (alissa@strom.at) - credit: 50.0
        002: Eduard Power (eduard@power.at) - credit: 0.0
      """
