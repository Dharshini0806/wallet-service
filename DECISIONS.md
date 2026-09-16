# Design Decisions

## 1. How I handled the concurrency race condition

To prevent race conditions during concurrent debit transactions, I used pessimistic locking with JPA.

The WalletRepository uses a database lock (`PESSIMISTIC_WRITE`) when fetching a wallet. This ensures that only one transaction can modify a wallet's balance at a time.

The transaction processing method is annotated with `@Transactional`, so checking the balance, updating the wallet, and saving the transaction happen within a single database transaction.

To verify this behavior, I added a concurrent integration test that sends multiple debit requests simultaneously and confirms that only the allowed transactions succeed while the remaining requests fail due to insufficient balance.

---

## 2. AI Assistant Reflection

I used ChatGPT as a learning assistant throughout this assignment.

Initially, some suggestions focused on adding extra features that were not required by the assignment. I decided to keep the implementation aligned with the assignment requirements instead of adding unnecessary complexity.

I verified every code change by running integration tests and making sure the application behaved as expected before committing the changes.