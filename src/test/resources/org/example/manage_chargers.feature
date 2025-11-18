Feature: Manage Chargers
  As the network owner
  I want to create, view, update and delete chargers
  So that the charging network can be operated efficiently


  Scenario: Create a new charger at an existing location
    Given a new FillingStationNetwork
    And a location "Vienna West Station" exists for chargers
    When owner creates a charger with ID "CHG-001" at location "Vienna West Station"
    And sets the charger type to "AC"
    And sets the charger status to "available"
    Then the charger "CHG-001" is part of the charger list
    And the charger type is "AC"
    And the charger status is "available"
    And the charger belongs to location "Vienna West Station"



  Scenario: View all chargers
    Given a new FillingStationNetwork
    And the following locations exist:
      | Name                 | Address          | Chargers |
      | Vienna West Station  | Europaplatz 1    | 0        |
      | Linz Center Garage   | Centerstr. 50    | 0        |
    And the following chargers exist:
      | ID      | Type | Status      | Location             |
      | CHG-001 | AC   | available   | Vienna West Station  |
      | CHG-002 | DC   | unavailable | Vienna West Station  |
      | CHG-003 | AC   | available   | Linz Center Garage   |
    When owner views the list of all chargers
    Then viewing the charger list shows:
      """
      Chargers:
        CHG-001: AC - available (Location: Vienna West Station)
        CHG-002: DC - unavailable (Location: Vienna West Station)
        CHG-003: AC - available (Location: Linz Center Garage)
      """



  Scenario: Update charger status
    Given a new FillingStationNetwork
    And a location "Linz Center Garage" exists for chargers
    And the following chargers exist:
      | ID      | Type | Status    | Location           |
      | CHG-010 | DC   | available | Linz Center Garage |
    When owner updates the status of charger "CHG-010" to "unavailable"
    Then the charger "CHG-010" has status "unavailable"



  Scenario: Delete a charger
    Given a new FillingStationNetwork
    And a location "Vienna West Station" exists for chargers
    And the following chargers exist:
      | ID      | Type | Status    | Location             |
      | CHG-500 | AC   | available | Vienna West Station  |
    When owner deletes the charger "CHG-500"
    Then the charger "CHG-500" no longer exists in the charger list
