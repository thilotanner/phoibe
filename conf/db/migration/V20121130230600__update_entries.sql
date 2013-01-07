UPDATE Entry e
INNER JOIN Creditor c ON c.creditorEntry_id = e.id
SET e.date = c.dateOfInvoice
WHERE c.creditorStatus = 'PAID';

UPDATE Entry e
INNER JOIN Creditor c ON c.valueAddedTaxEntry_id = e.id
SET e.date = c.dateOfInvoice
WHERE c.creditorStatus = 'PAID';

UPDATE Entry e
INNER JOIN CreditorPayment c ON c.paymentEntry_id = e.id
SET e.date = c.paid;

UPDATE Entry e
INNER JOIN DebitorPaymentReceipt c ON c.paymentEntry_id = e.id
SET e.date = c.paid;