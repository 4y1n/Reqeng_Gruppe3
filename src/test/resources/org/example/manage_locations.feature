Feature: Manage Locations
  As an owner
  I want to manage locations
  so that I can keep my charging network up to date.

  Background:
    Given a new FillingStationNetwork


  Scenario: create and view the base location list
    When the following locations are created:
      | Name                    | Address                                  |
      | Vienna West Station     | Mariahilfer Str. 120, 1070 Vienna         |
      | Linz Center Garage      | Landstraße 45, 4020 Linz                  |
      | Graz Main Square        | Herrengasse 3, 8010 Graz                  |
      | Salzburg Airport        | Innsbrucker Bundesstraße 95, 5020 Salzburg|
      | St. Pölten Forum Garage | Kremser Gasse 22, 3100 St. Pölten         |
      | Innsbruck City Mall     | Museumstraße 38, 6020 Innsbruck           |
      | Klagenfurt Lakeside Park| Lakeside B02, 9020 Klagenfurt             |
      | Villach Süd             | Maria-Gailer-Straße 42, 9500 Villach      |
      | Wiener Neustadt Center  | Stadionstraße 13, 2700 Wr. Neustadt       |
      | Eisenstadt Downtown     | Hauptstraße 4, 7000 Eisenstadt            |
    And owner views the list of all locations
    Then the number of locations is 10
    And viewing the location list shows the following output:
      """
      Locations:
        Vienna West Station: Mariahilfer Str. 120, 1070 Vienna
        Linz Center Garage: Landstraße 45, 4020 Linz
        Graz Main Square: Herrengasse 3, 8010 Graz
        Salzburg Airport: Innsbrucker Bundesstraße 95, 5020 Salzburg
        St. Pölten Forum Garage: Kremser Gasse 22, 3100 St. Pölten
        Innsbruck City Mall: Museumstraße 38, 6020 Innsbruck
        Klagenfurt Lakeside Park: Lakeside B02, 9020 Klagenfurt
        Villach Süd: Maria-Gailer-Straße 42, 9500 Villach
        Wiener Neustadt Center: Stadionstraße 13, 2700 Wr. Neustadt
        Eisenstadt Downtown: Hauptstraße 4, 7000 Eisenstadt
      """


  Scenario: update a location
    Given an existing location "Vienna West Station"
    When owner updates the address to "Mariahilfer Gürtel 210, 1150 Vienna"
    Then the location "Vienna West Station" has the new address "Mariahilfer Gürtel 210, 1150 Vienna"


  Scenario: delete a location
    Given an existing location "Linz Center Garage"
    When owner deletes the location "Linz Center Garage"
    Then the location "Linz Center Garage" is no longer part of the location list


  Scenario: Edge Case - update location to the same name
    Given an existing location "Vienna West Station"
    When owner renames the location from "Vienna West Station" to "Vienna West Station"
    Then the location "Vienna West Station" still exists
    And the number of locations is 1


  Scenario: Error Case - creating a duplicate location
    When the following locations are created:
      | Name                | Address                           |
      | Vienna West Station | Mariahilfer Str. 120, 1070 Vienna |
    And owner attempts to create a location "Vienna West Station"
    Then an error about duplicate location is raised
