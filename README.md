# ecommerce-api-client-runner
Client that randomly calls the exposed methods on the [Udacity eCommerce REST API Project](https://github.com/biscaboy/ecommerce).

## Operation
This client runs a thread for a given user attmepting 20 different calls to the API.  

#### To Build:

```
.mvn clean package
```

#### To Run .

After starting the eCommerce REST API server, run this application with the following command:
```
java -jar ./target/exercise-api-0.0.1-SNAPSHOT-spring-boot.jar 
```

##### Usage
```
java -jar ./target/exercise-api-0.0.1-SNAPSHOT-spring-boot.jar <api-hostname>:<port> <username-to-create> [, <username-to-create> [, ...]]
```

If run with no paramters, the application will look for a default server running at ```http://localhost:8081``` and create a default username and password for the session.  Only one thread will be created.

If the server is running on a different server or port, you can specify any url as the first parameter.  

If you want to create multiple users, a new thread will be spawned for each username supplied on the command line after the url.

##### Example
This command will launch four threads for each of four users and execute 20 API calls in each thread.  (The console output is piped to null and the process runs in the background.)
```
java -jar ./target/exercise-api-0.0.1-SNAPSHOT-spring-boot.jar http://localhost:8081 User1 User2 User3 User4 2>&1 1>/dev/null &
```
