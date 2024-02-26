# account-manager

1. Tech Stack
- JDK 11
- Maven
- Spring Boot 2.5.2

2. account-manager exposes RESTFUL API at port 8443 for two capabilities
- check account balance
- transfer money

3. Assumptions
- transfer money happens under the same customer
- currency is HKD for all accounts
- authentication is out of scope, client is already authenticated to call the endpoints

4. To run up account-manager locally and test its endpoints:
`mvn clean spring-boot:run`

`curl -i -X POST localhost:8443/account/balance -H "content-type:application/json"  -d '{"customerId": "tester", "accountNumber":"12345678"}'`

`curl -i -X POST localhost:8443/account/transfer -H "content-type:application/json" -H "idempotency-key:5cb2786a-b2c8-4a24-8334-898c1874be8F" -d '{"customerId": "tester", "sendingAccountNumber":"12345678", "receivingAccountNumber":"88888888", "amount":"100000"}'`

6. data.sql is used to initialize H2 DB when account-manager is running for the first time, disable its loading in the subsequent running by commenting out the following line in application.properties
spring.datasource.initialization-mode=always

7. Test
`mvn clean test`