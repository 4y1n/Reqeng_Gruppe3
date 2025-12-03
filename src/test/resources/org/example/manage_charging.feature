Feature: Vehicle Charging
  As a customer
  I want to charge my car
  so that it has enough energy to drive

  Background:
    Given a customer "Alissa" with customer account balance of 20.00 exists
    And a charging location "Vienna West" exists
    And the price is 0.10 EUR per minute at charging location "Vienna West"
    And a charger "CHG-01" with type DC is "available"


  Scenario: Successful charging session
    When customer "Alissa" starts charging at charger "CHG-01" for 20 minutes
    Then the charging session for customer "Alissa" at charger "CHG-01" is completed
    And customer "Alissa" customer account balance is reduced according to consumed energy

  Scenario: Charger becomes unavailable during charging
    When customer "Alissa" starts charging at charger "CHG-01" for 10 minutes
    Then charger "CHG-01" status is "unavailable"

  Scenario: Error - start charging when point is unavailable
    Given a charger "CHG-01" with type DC is "unavailable"
    When the customer tries to start a charging session at charger "CHG-01"
    Then the system denies the charging session

  Scenario: Error – customer has insufficient balance
    Given a customer "Eduard" with customer account balance of 1.00 exists
    When customer "Eduard" starts charging at charger "CHG-01" for 60 minutes
    Then an error message is sent to the customer
    And the system denies the charging session
