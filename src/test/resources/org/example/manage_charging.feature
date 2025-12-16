Feature: Vehicle Charging
  As a customer
  I want to charge my car
  so that it has enough energy to drive

  Background:
    Given a new FillingStationNetwork
    And the following locations exist:
      | Name                |
      | Innsbruck City Mall |
      | Graz Main Square  |
    And the following prices exist:
      | Mode | Price per kWh | Price per Minute |
      | AC   | 0.32          | 0.06             |
      | DC   | 0.42          | 0.11             |
    And the following chargers exist:
      | ID      | Type | Status    | Location             |
      | CHG-054  | DC   | available | Innsbruck City Mall |
      | CHG-055  | AC   | available | Innsbruck City Mall |
    And the following customers exist:
      | Id  | Name          | Email             | Credit     |
      | 005 | Alissa Strom  | alissa@strom.at   | 20.00 EUR  |
      | 006 | Eduard Power  | eduard@power.at   | 1.00 EUR   |



  Scenario: Reduce customer balance after charging session
    When customer "005" charges at charger "CHG-054" for 20 minutes
    Then the charging session for customer "005" at charger "CHG-054" is completed
    And customer "005" customer account balance is reduced according to consumed energy


  Scenario: Charger becomes occupied during charging
    When customer "005" charges at charger "CHG-054" for 10 minutes
    Then charger "CHG-054" status is "occupied"


  Scenario: Error – customer has insufficient balance
    When customer "006" charges at charger "CHG-054" for 60 minutes
    Then an error message is sent to the customer
    And the system denies the charging session


  Scenario: Edge Case - 0 minutes charging (valid)
    When customer "005" charges at charger "CHG-055" for 0 minutes
    Then the charging session for customer "005" at charger "CHG-055" is completed
    And customer "005" customer account balance is reduced according to consumed energy


  Scenario: Error Case - end time before start time (invalid)
    When a charging process is created for customer "005" at charger "CHG-055" with mode "AC" starting at "2025-01-01T10:00" and ending at "2025-01-01T09:59" with energy 5.0 kWh
    Then an error about invalid charging session is raised
