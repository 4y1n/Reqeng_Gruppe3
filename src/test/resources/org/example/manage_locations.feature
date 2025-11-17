Feature: Manage Locations
  As an owner
  I want to manage locations
  so that I can keep my charging network up to date.

  Background:
    Given a new FillingStationNetwork

  # --- Create Location ---
  Scenario: create a new location
    When owner creates a location with the unique name "Vienna West Station"
    And sets the address to "Mariahilfer Str. 120, 1070 Vienna"
    And sets the number of chargers to 4
    Then the location "Vienna West Station" is part of the location list
    And the address is "Mariahilfer Str. 120, 1070 Vienna"
    And the number of chargers is 4

  # --- View Location List ---
  Scenario: view all existing locations
    Given the following locations exist:
      | Name                | Address                              | Chargers |
      | Vienna West Station | Mariahilfer Str. 120, 1070 Vienna    | 4        |
      | Linz Center Garage  | Landstraße 45, 4020 Linz             | 2        |
      | Graz Main Square    | Herrengasse 3, 8010 Graz             | 6        |
    When owner views the list of all locations
    Then the number of locations is 3
    And viewing the location list shows the following output:
      """
      Locations:
        Vienna West Station: Mariahilfer Str. 120, 1070 Vienna (4 chargers)
        Linz Center Garage: Landstraße 45, 4020 Linz (2 chargers)
        Graz Main Square: Herrengasse 3, 8010 Graz (6 chargers)
      """

  # --- Update Location ---
  Scenario: update a location
    Given an existing location "Vienna West Station"
    When owner updates the address to "Mariahilfer Gürtel 210, 1150 Vienna"
    Then the location "Vienna West Station" has the new address "Mariahilfer Gürtel 210, 1150 Vienna"

  # --- Delete Location ---
  Scenario: delete a location
    Given the location "Linz Center Garage" exists
    When owner deletes the location "Linz Center Garage"
    Then the location "Linz Center Garage" is no longer part of the location list
