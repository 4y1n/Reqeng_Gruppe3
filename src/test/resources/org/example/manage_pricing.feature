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
