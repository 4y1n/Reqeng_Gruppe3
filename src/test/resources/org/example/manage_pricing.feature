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
    When a new location "Vienna East" is created
    And owner sets the price for location "Vienna East" to:
      | Mode | Price per kWh | Price per Minute |
      | AC   | 0.32 EUR      | 0.06 EUR         |
      | DC   | 0.42 EUR      | 0.11 EUR         |
    Then the new prices are stored
    And all charging processes started after the at "Vienna East" use the prices

  Scenario: Update an existing price
    When owner sets the price for location "Vienna West" to:
      | Mode | Price per kWh | Price per Minute |
      | AC   | 0.35 EUR      | 0.06 EUR         |
      | DC   | 0.45 EUR      | 0.12 EUR         |
    Then the new prices are stored
    And all charging processes started after the update use the new prices
    And charging sessions already in progress continue with the old prices

  Scenario: Multiple updates in one day
    Given owner updated prices at "Vienna West" at 09:00
    And owner updated prices again at 15:00
    When a customer starts charging at 14:30
    Then the system applies the 09:00 prices
    And not the 15:00 prices

  Scenario: Different locations have different prices
    Given location "Vienna East" has AC tariff 0.28 EUR/kWh
    And location "Vienna West" has AC tariff 0.35 EUR/kWh
    When a customer charges at "Vienna East"
    Then the system applies the price of "Vienna East"
    And does not apply price from "Vienna West"

    Scenario: View pricing list for a location
      When owner selects location "Vienna West"
      And selects type AC
      And selects price per minute
      Then he is shown the price 0.05 EUR


