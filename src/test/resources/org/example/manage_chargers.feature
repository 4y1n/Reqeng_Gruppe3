Feature: Manage Chargers
  As the network owner
  I want to manage chargers
  so that the charging network can be operated efficiently

  Background:
    Given a new FillingStationNetwork
    And the following locations exist:
      | Name                   | Address                     |
      | Vienna West Station    | Mariahilfer Str. 120        |
      | Linz Center Garage     | Landstraße 45               |
      | Graz Main Square       | Herrengasse 3               |
      | Salzburg City Mall     | Alpenstraße 75              |
      | Innsbruck Main Station | Südtiroler Platz 1          |
      | Klagenfurt Lakeside    | Universitätsstraße 67       |
      | St. Pölten Center      | Kremser Landstraße 8        |
      | Villach West           | Ossiacher Zeile 17          |
      | Wiener Neustadt Plaza  | Zehnergürtel 12             |
      | Eisenstadt Central     | Hauptstraße 2               |


  Scenario: create and view a newly created charger
    When owner creates a charger with ID "CHG-001" at location "Vienna West Station"
    And sets the charger type to "AC"
    And sets the charger status to "available"
    When owner views the list of all chargers
    Then viewing the charger list shows:
      """
      Chargers:
        CHG-001: AC - available (Location: Vienna West Station)
      """


  Scenario: create multiple chargers and view them
    Given the following chargers exist:
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


  Scenario: update charger status
    Given the following chargers exist:
      | ID      | Type | Status    | Location             |
      | CHG-010 | DC   | available | Graz Main Square     |
    When owner updates the status of charger "CHG-010" to "unavailable"
    Then the charger "CHG-010" has status "unavailable"


  Scenario: delete a charger
    Given the following chargers exist:
      | ID      | Type | Status    | Location             |
      | CHG-500 | AC   | available | Klagenfurt Lakeside  |
    When owner deletes the charger "CHG-500"
    Then the charger "CHG-500" no longer exists in the charger list

# Error und Edge Cases:
  Scenario: ERROR - create a charger with duplicate ID
    Given the following charger exists:
      | ID      | Type | Status    | Location      |
      | CHG-001 | AC   | available | Vienna Center |
    When owner attempts to create a charger with ID "CHG-001" at location "Vienna Center"
    Then an error message for charger is shown: "Charger already exists: CHG-001"


  Scenario: EDGE - create a charger with extremely long ID
    Given the following charger exists:
      | ID      | Type | Status    | Location      |
      | CHG-BASE | AC   | available | Vienna Center |
    When owner attempts to create a charger with long ID "CHG-" followed by 500 "A" characters at location "Vienna Center"
    Then the charger with long ID "CHG-" followed by 500 "A" characters is part of the charger list