
# NextGen Point Of Sale system - Non-functional specification 

## Usability
- Text should be easily visible from 1 meter if a screen with a diagonal of at least 20" is used.
- Colors associated with common forms of color blindness should be avoided.
- Error messages should have an associated sound because cashier may not be looking at screen.

## Reliability - recoverability
- If external systems fail, try to solve with a local solution. E.g.,:
  - Payment authorization service failure: accept cash payment.
  - Inventory system failure: store sale info locally, try again when there is another sale, or when cashing out.

## Performance
- Buyers want quick sales processing. One bottleneck is external payment authorization. Timeout after 30 seconds.

## Supportability
- Internationalization of displayed text (text, units, number and date formatting).

## Implementation
- Software must run on Android devices. 
- Software must be written using Java.

## External interfaces
- Must connect to inventory system to update inventory after a sale.
- Must connect to credit card authorization service to validate payments.