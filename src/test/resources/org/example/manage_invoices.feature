Feature: Manage Invoices
  As a customer I want to be able to view my invoice so I can get additional information about my past charging process.
  As an owner I want to be able to monitor invoices so that I can get information about my business.


  Background:
    Given a new FillingStationNetwork

    And the following locations exist:
      | Name                |
      | Vienna West Station |
      | Linz Center Garage  |

    And owner sets pricing:
      | Mode | Price per kWh | Price per Minute |
      | AC   | 0.32          | 0.06             |
      | DC   | 0.42          | 0.11             |

    And the following customers exist:
      | Id  | Name          | Email             |
      | 001 | Alissa Strom  | alissa@strom.at   |
      | 002 | Eduard Power  | eduard@power.at   |

    And customer "001" tops up 30.00 EUR
    And customer "002" tops up 20.00 EUR

    And the following chargers exist:
      | ID      | Type | Status    | Location             |
      | CHG-001 | AC   | available | Vienna West Station  |
      | CHG-002 | DC   | available | Vienna West Station  |


  Scenario: Create and view invoices for multiple customers charging
    When customer "001" charges at charger "CHG-001" for:
      """
      Mode: AC
      Energy: 10.0 kWh
      Start: 2025-01-10 14:00
      End:   2025-01-10 14:20
      """

    And customer "002" charges at charger "CHG-002" for:
      """
      Mode: DC
      Energy: 12.0 kWh
      Start: 2025-02-15 16:00
      End:   2025-02-15 16:18
      """

    Then invoice "INV-001" is created for customer "001"
    And invoice "INV-002" is created for customer "002"

    When customer with id "001" views invoice "INV-001"
    Then the invoice shows:
  """
  ------------------------------------
  Invoice ID: INV-001
  Charging Date: 2025-01-10 14:20

  Customer ID: 001
  Charger: CHG-001
  Location: Vienna West Station
  Mode: AC

  Charging Amount:
    10.0 kWh * 0.32 EUR = 3.20 EUR
    20 minutes * 0.06 EUR = 1.20 EUR

  Total Price: 4.40 EUR
  Balance after transaction: 25.60 EUR
  ------------------------------------
  """

    When customer with id "002" views invoice "INV-002"
    Then the invoice shows:
  """
  ------------------------------------
  Invoice ID: INV-002
  Charging Date: 2025-02-15 16:18

  Customer ID: 002
  Charger: CHG-002
  Location: Vienna West Station
  Mode: DC

  Charging Amount:
    12.0 kWh * 0.42 EUR = 5.04 EUR
    18 minutes * 0.11 EUR = 1.98 EUR

  Total Price: 7.02 EUR
  Balance after transaction: 12.98 EUR
  ------------------------------------
  """


  Scenario: owner views all invoices
    When customer "001" charges at charger "CHG-001" for:
      """
      Mode: AC
      Energy: 10.0 kWh
      Start: 2025-01-10 14:00
      End:   2025-01-10 14:20
      """

    And customer "002" charges at charger "CHG-002" for:
      """
      Mode: DC
      Energy: 12.0 kWh
      Start: 2025-02-15 16:00
      End:   2025-02-15 16:18
      """

    Then invoice "INV-001" is created for customer "001"
    And invoice "INV-002" is created for customer "002"

    When owner views all invoices
    Then the invoice list shows:
  """
  Invoices:

    ------------------------------------
    Invoice ID: INV-001
    Charging Date: 2025-01-10 14:20

    Customer ID: 001
    Charger: CHG-001
    Location: Vienna West Station
    Mode: AC

    Charging Amount:
      10.0 kWh * 0.32 EUR = 3.20 EUR
      20 minutes * 0.06 EUR = 1.20 EUR

    Total Price: 4.40 EUR
    Balance after transaction: 25.60 EUR
    ------------------------------------

    ------------------------------------
    Invoice ID: INV-002
    Charging Date: 2025-02-15 16:18

    Customer ID: 002
    Charger: CHG-002
    Location: Vienna West Station
    Mode: DC

    Charging Amount:
      12.0 kWh * 0.42 EUR = 5.04 EUR
      18 minutes * 0.11 EUR = 1.98 EUR

    Total Price: 7.02 EUR
    Balance after transaction: 12.98 EUR
    ------------------------------------
  """

  Scenario: Charging with unknown pricing mode fails
    When customer "001" charges at charger "CHG-001" for:
      """
      Mode: FAST
      Energy: 10.0 kWh
      Start: 2025-03-01 10:00
      End:   2025-03-01 10:20
      """

    Then an access error occurs with message "No pricing defined for mode FAST"

  # Error und Edge Cases:

  Scenario: Edge Case - owner views all invoices when none exist
    Given a new FillingStationNetwork
    When owner views all invoices
    Then the invoice list shows:
      """
      Invoices:
      """

  Scenario: Error Case - creating a duplicate invoice id
    Given a new FillingStationNetwork
    And the following customers exist:
      | Id  | Name         | Email           |
      | 001 | Alissa Strom | alissa@strom.at |
    And owner sets pricing:
      | Mode | Price per kWh | Price per Minute |
      | AC   | 0.32          | 0.06             |
    And the following chargers exist:
      | ID      | Type | Status    | Location             |
      | CHG-001 | AC   | available | Vienna West Station  |
    And customer "001" tops up 1.00 EUR
    When customer "001" charges at charger "CHG-001" for:
    """
    Mode: AC
    Energy: 1.0 kWh
    Start: 2025-01-01 10:00
    End:   2025-01-01 10:01
    """
    And owner attempts to create an invoice with id "INV-001" for customer "001" at charger "CHG-001"
    Then an access error occurs with message "Invoice with ID INV-001 already exists."

