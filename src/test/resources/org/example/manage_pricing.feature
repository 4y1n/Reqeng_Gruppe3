Feature: Managing pricing
  As an owner
  I want to manage the price for a location
  so that my customers are charged adequately

  Background:
    Given a new FillingStationNetwork
    Given the current time is "2025-12-09 08:00"
    And the location "Vienna West Station" exists
    And the location has prices:
      | Mode | Price per kWh | Price per Minute |
      | AC   | 0.30 EUR      | 0.05 EUR         |
      | DC   | 0.40 EUR      | 0.10 EUR         |

  Scenario: set price for a new location
    When a new location "Linz Center Garage" is created
    And the owner sets the pricing for "Linz Center Garage" to:
      | Mode | Price per kWh | Price per Minute |
      | AC   | 0.28 EUR      | 0.04 EUR         |
      | DC   | 0.38 EUR      | 0.09 EUR         |
    Then the pricing for "Linz Center Garage" is stored

  Scenario: Update price
    When the owner sets the pricing for "Vienna West Station" to:
      | Mode | Price per kWh | Price per Minute |
      | AC   | 0.33 EUR      | 0.07 EUR         |
      | DC   | 0.43 EUR      | 0.13 EUR         |
    Then the pricing for "Vienna West Station" is stored
    And all charging processes started after the update at "2025-12-09 08:00" use the stored prices
    And charging processes already in progress continue using the old pricing

  Scenario: View current pricing for a location
    When the owner selects location "Vienna West Station"
    Then the system shows:
  """
  ------------------------------------
  Pricing for: Vienna West Station
  As of: 2025-12-09 08:00

  Mode: AC
    Price per kWh:     0.30 EUR
    Price per Minute:  0.05 EUR

  Mode: DC
    Price per kWh:     0.40 EUR
    Price per Minute:  0.10 EUR
  ------------------------------------
  """
  
  Scenario: Edge Case - request pricing for a mode that does not exist
    When the owner requests pricing for mode "CHAdeMO" at "Vienna West Station"
    Then no pricing is returned

  Scenario: Error Case - creating a duplicate pricing for same mode
    When the owner attempts to create pricing for mode "AC" with 0.32 EUR per kWh and 0.06 EUR per minute
    Then an error about duplicate pricing is raised
