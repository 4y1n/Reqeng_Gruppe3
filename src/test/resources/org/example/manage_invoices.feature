Feature: Manage Invoices
  As a customer
  I want to be able to view my invoice
  so I can get additional information about my past charging process.

  As an owner
  I want to be able to monitor invoices
  so that I can get information about my business.


  Background:
    Given a new FillingStationNetwork
    And the following customers exist:
      | Id  | Name          | Email             | Credit |
      | 001 | Alissa Strom  | alissa@strom.at   |  25.0  |
      | 002 | Eduard Power  | eduard@power.at   |  10.0  |
    And the following invoices exist:
      | InvoiceId | CustomerId | Date        | Amount |
      | 1001      | 001        | 2025-01-10  | 12.50  |
      | 1002      | 001        | 2025-02-01  |  8.40  |
      | 2001      | 002        | 2025-02-15  | 15.90  |


  Scenario: customer views their own invoice
    When customer with id "001" views invoice "1001"
    Then the invoice amount is 12.50
    And the invoice date is "2025-01-10"
    And the invoice belongs to customer "001"


  Scenario: owner views the list of all invoices
    When owner views all invoices
    Then the invoice list shows:
      """
      Invoices:
        1001: Customer 001 - 12.50 EUR - 2025-01-10
        1002: Customer 001 - 8.40 EUR - 2025-02-01
        2001: Customer 002 - 15.90 EUR - 2025-02-15
      """


  Scenario: customer tries to access another customer's invoice
    When customer with id "002" attempts to view invoice "1001"
    Then an access error occurs with message "Access denied: invoice does not belong to this customer."
