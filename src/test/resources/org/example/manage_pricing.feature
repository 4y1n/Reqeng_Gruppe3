Feature: Managing pricing
  As an owner
  I want to manage the price for a location
  so that my customers are charged adequately

  Background:
    Given a new FillingStationNetwork
    And the location "Vienna West Station" exists
    And the location "Vienna West Station" has pricing:
      | Mode | Price per kWh | Price per Minute |
      | AC   | 0.30 EUR      | 0.05 EUR         |
      | DC   | 0.40 EUR      | 0.10 EUR         |

  Scenario: Set a new price for a newly created location
    When the owner creates a new location "Linz Center Garage"
    And the owner sets the pricing for "Linz Center Garage" to:
      | Mode | Price per kWh | Price per Minute |
      | AC   | 0.32 EUR      | 0.06 EUR         |
      | DC   | 0.42 EUR      | 0.11 EUR         |
    Then the pricing for "Linz Center Garage" is stored
    And all charging processes started after the update at "Linz Center Garage" use the stored prices
    And the pricing of "Vienna West Station" remains unchanged

  Scenario: Update an existing price
    When the owner sets the pricing for "Vienna West Station" to:
      | Mode | Price per kWh | Price per Minute |
      | AC   | 0.35 EUR      | 0.06 EUR         |
      | DC   | 0.45 EUR      | 0.12 EUR         |
    Then the new pricing is stored
    And all charging processes started after the update use the new pricing
    And charging processes already in progress continue using the old pricing

  Scenario: Error case – negative pricing is not allowed
    When the owner tries to set the pricing for "Vienna West Station" to:
      | Mode | Price per kWh | Price per Minute |
      | AC   | -0.10 EUR     | 0.05 EUR         |
    Then the system rejects the pricing update
    And an error message "Invalid price: negative values are not allowed" is shown
    And the old pricing remains unchanged

  Scenario: View pricing list for a location
    When the owner selects location "Vienna West Station"
    And the owner selects type AC
    And the owner selects price per minute
    Then the owner is shown the price 0.05 EUR

    # Error und Edge Cases:

  Scenario: Edge Case - request pricing for a mode that does not exist
    Given a new FillingStationNetwork
    And the location "Vienna West Station" exists
    When the owner requests pricing for mode "CHAdeMO" at "Vienna West Station"
    Then no pricing is returned

  Scenario: Error Case - creating a duplicate pricing for same mode
    Given a new FillingStationNetwork
    And the location "Vienna West Station" exists
    And the owner creates pricing for mode "AC" with 0.30 EUR per kWh and 0.05 EUR per minute
    When the owner attempts to create pricing for mode "AC" with 0.32 EUR per kWh and 0.06 EUR per minute
    Then an error about duplicate pricing is raised

