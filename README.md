## API wrapper demo application

This application is a java spring boot application that exposes one API to retrieve and filter data from an external URL.

To start checkout the project  and run 

```docker-compose up```


Alternatively use the docker command to build the image and run it. The container exposes its java service on port 8080 under the root context 

```host:8080/api-wrapper```


Actuators are exposed at 

```
host:8080/api-wrapper/actuator/info
host:8080/api-wrapper/actuator/health
host:8080/api-wrapper/actuator/metrics

```

Open API/swagger documentation is available at 

```http://localhost:8080/api-wrapper/swagger-ui.html```