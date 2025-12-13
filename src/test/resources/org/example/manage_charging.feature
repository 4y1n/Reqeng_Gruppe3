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
      | Id  | Name          | Email             |
      | 001 | Alissa Strom  | alissa@strom.at   |
      | 002 | Eduard Power  | eduard@power.at   |
    And customer "001" has balance of "20.00" EUR
    And customer "002" has balance of "1.00" EUR



  Scenario: Reduce customer balance after charging session
    When customer "001" starts charging at charger "CHG-054" for 20 minutes
    Then the charging session for customer "001" at charger "CHG-054" is completed
    And customer "001" customer account balance is reduced according to consumed energy and time charged


  Scenario: Charger becomes occupied during charging
    When customer "001" starts charging at charger "CHG-054" for 10 minutes
    Then charger "CHG-054" status is "occupied"
