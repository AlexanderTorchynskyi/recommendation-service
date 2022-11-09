# Recommendation service
### Start up
In order to run service locally execute next commands
```shell script
./mvnw clean package

java -jar target/recommendation-0.0.1.jar
```
or you can go with one command:
```shell script
./mvnw spring-boot:run 
```
To run the service in docker execute next commands:
```shell script
./mvnw clean package

docker build -t recommendation-service .

docker run -ti -p8080:8080 recommendation-service:latest
```
### API 
The service has swagger documentation in order to access it - follow the next steps:
```shell script
# start the service
./mvnw spring-boot:run 

# open next url in browser
http://localhost:8080/swagger-ui/index.html#/
```
### Implementation notes and future improvements:

- The implementation of calculating `oldest/newest/min/max for each crypto  for the whole month` 
was done based on an assumption each file would have only one month of data, if a user provided wider range
the wider would  be taken.
- Following time frames - the last bullet of the part `Things to consider` was not clear to me, 
should the recommendation service be able to handle wider range of dates, 
or does it mean the user should be able to select the time frame filters to get normalised ranges? 
I went with more simple solution due to lacking of time. 
- I covered the main business service with unit tests: `CryptoDetailsService` but 
I have not had enough time to cover the other services and the controller.
- Last but not least I did an extra mile bullet with application dockerization which was not tough as 
I do such staff on a daily basis. The last bullet was not implemented for couple of reasons: I would not be able to complete 
it in time. Based on my expirience such functionality is usually implemented on client-facing side for example following 
microservice pattern Api Gateway we could keep logic there: Apigee has that under the hood: https://docs.apigee.com/api-platform/develop/rate-limiting

Implementing core logic was fun, but in order to finish each part of the task with flexible, extendable
and performance friendly code - two days is enough at least for me. 
    
