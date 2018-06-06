# money-transferring-service
RESTful service for transferring money between accounts

## Running application 

To run the app you need to execute the command from the root project directory:
  
`sbt run`

Make sure you have installed sbt build tool https://www.scala-sbt.org/

## Using API of the application 

All active endpoints of this app you can find in the file conf/http.request.examples.
Or you can import postman (https://www.getpostman.com/) collection for these purposes (file  config/money-transferring.postman_collection.json)

### ToDos
- `[I-2]` - Add an opportunity to create an multi-currency account
- `[I-3]` - Add more flexible functionality to play with currencies
- `[I-4]` - Define limit precessing speed by high-load tests