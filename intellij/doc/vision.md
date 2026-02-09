
# NextGen Point Of Sale system - Vision document 

## 1. Introduction

We envision a robust Point-Of-Sale (POS) application, NextGen POS, with the flexibility to support multiple terminal and user interface mechanisms, and integrate with multiple third- party support systems.

## 2. Business case
Our POS software addresses customer needs that other products do not:
1. It can continue processing sales even when external services fail.
2. It integrates with accounting and inventory systems to simplify stocking logistics.

## 3. Key functionality
- Sales capture and auditing.
- Multiple payment method support (credit, debit, check, cash).
- System administration for users, security, discount rules, etc.
- Real time inventory updating through 3rd party system connection.
- Automatic offline sales processing support when external systems fail.

## 4. Stakeholder goals summary
- **Cashier**: process sales, handle returns, cash in, cash out
- **Administrator**: manage users, configure system
- **Sales activity system**: analyze sales data
- **Tax agency**: collect correct amount of tax for each sale


## Use case diagram

```plantuml
@startuml
skin rose

' human actors
actor "Administrator" as admin
actor "Cashier" as cashier

' system actors
actor "Accounting system" <<system>> as accountingSystem
actor "Inventory system" <<system>> as inventorySystem

' list all use cases in package
package NextGenPOS{
    usecase "Authenticate" as authenticate
    usecase "Start admin session" as administerSystem
    usecase "Start cashier session" as startCashierSession
    
    usecase "Manage users" as manageUsers
    usecase "Configure settings" as configureSettings
    usecase "Process sale" as processSale
    usecase "Process return" as processReturn
    usecase "Look up item" as lookUpItem
}

' list relationships between actors and use cases
admin --> administerSystem
cashier --> startCashierSession

startCashierSession --> authenticate : <<includes>>
administerSystem --> authenticate : <<includes>>
administerSystem <|-- manageUsers : <<extends>>
administerSystem <|-- configureSettings : <<extends>>

processSale <|-right- lookUpItem : <<extends>>
processReturn <|-left- lookUpItem : <<extends>>

startCashierSession <|-- processSale : <<extends>>
startCashierSession <|-- processReturn : <<extends>>

' system actors
processSale --> accountingSystem
processSale --> inventorySystem
processReturn --> accountingSystem
processReturn --> inventorySystem
@enduml
```