Feature: Managing pricing
  As an owner
  I want to manage the price for a location
  so that my customers are charged adequately

  Background:
    Given a location "Vienna West" exists
    And has prices:
      | Mode | Price per kWh | Price per Minute |
      | AC   | 0.30 EUR      | 0.05 EUR         |
      | DC   | 0.40 EUR      | 0.10 EUR         |

  Scenario: Set a new price
    When a new location "Linz Center" is created
    And owner sets the price for location "Linz Center" to:
      | Mode | Price per kWh | Price per Minute |
      | AC   | 0.32 EUR      | 0.06 EUR         |
      | DC   | 0.42 EUR      | 0.11 EUR         |
    Then the new prices are stored
    And all charging processes started after that at "Linz Center" use the prices

  Scenario: Update an existing price
    When owner sets the price for location "Vienna West" to:
      | Mode | Price per kWh | Price per Minute |
      | AC   | 0.35 EUR      | 0.06 EUR         |
      | DC   | 0.45 EUR      | 0.12 EUR         |
    Then the new prices are stored
    And all charging processes started after that at "Vienna West" use the new prices
    And charging sessions already in progress continue with the old prices


  Scenario: View pricing list for a location
    When owner selects location "Vienna West"
    And selects type AC
    And selects price per minute
    Then he is shown the price 0.05 EUR