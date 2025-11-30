Feature: Vehicle Charging
  As a customer
  I want to charge my car
  so that it has enough energy to drive

  Background:
    Given a customer "Alice" with customer account balance of 20.00 exists
    And a location "Vienna West" exists
    And the price is 0.10 EUR per minute at "Vienna West"
    And a charger "Charger-1" with type DC is "available"


  Scenario: Successful charging session
    When customer "Alice" starts charging at "Charger-1" for 20 minutes
    Then the charging session for "Alice" at "Charger-1" is completed
    And customer "Alice" customer account balance is reduced according to consumed energy

  Scenario: Charger becomes occupied during charging
    When customer "Alice" starts charging at "Charger-1" for 10 minutes
    Then charger "Charger-1" status is "occupied"

  Scenario: Error - start charging when point is out of service
    Given a charger "Charger-1" with type DC is "out of service"
    When the customer tries to start a charging session at "Charger-1"
    Then the system denies the charging session


  Scenario: Error – customer has insufficient balance
    Given a customer "Bob" with customer account balance of 1.00 exists
    When customer "Bob" starts charging at "Charger-1" for 60 minutes
    Then an error message is sent
    And the system denies the charging session