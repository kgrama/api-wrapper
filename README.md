## Openbanking ATM API wrapper

This application is a java spring boot application that exposes one API to retrieve and filter data from an external openbanking URL.

To start checkout the project  and run

```docker-compose up```


Alternatively use the docker command to build the image and run it. The container exposes its java service under the root context

```
/api-wrapper
```

Actuators are exposed at

```
/api-wrapper/actuator/info
/api-wrapper/actuator/health
/api-wrapper/actuator/metrics

```

Open API/swagger documentation is available at

```
/api-wrapper/swagger-ui.html
```

### Querying openbanking ATM respources

Once the service is running via docker it currently supports the filtering of openbanking ATM resources via the following API,
```
/api-wrapper/v1/{url safe base64 encoded lookup url}/{ATM identifier}
```

 for example to query for the full details of ATM *LFFFBC11* from [Lloyds bank open banking API](https://api.lloydsbank.com/open-banking/v2.2/atms) use the following url:
```
/api-wrapper/v1/aHR0cHM6Ly9hcGkubGxveWRzYmFuay5jb20vb3Blbi1iYW5raW5nL3YyLjIvYXRtcw==/LFFFBC11
```

### TODO
- Implement update lookup for cached data
