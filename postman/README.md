# postman

# Overview 

This container application will be responsible for sending messages to a message endpoint. The application will be built as a Docker container and will be deployed to a Kubernetes cluster.

## Building the Postman

To build the Postman applicaton run the below Maven command:
```
mvn clean install
```

## Configuring the Postman 

The Postman can be configured to send messages to a Message Endpoint for a specified period of time e.g 20mins. The configuration will be in a Yaml file and should be located in the conf/local foler. To configure use the below properties

| Property             | Description                                                       |
| -------------------- | ----------------------------------------------------------------- |
| name                 | The name of the instance                                          |
| timeToComplete       | time in minutes of how long the postman will be running           |
| healthCheckReadyPort | Port which will be used when checking if the application is ready |

-- Broker 

| Property     | Description                                       |
| ------------ | ------------------------------------------------- |
| name         | the name of the message Broker                    |
| hosts        | the connection details for the message broker     |

-- Route

| Property         | Description                                                |
| ---------------- | ---------------------------------------------------------- |
| name             | the route name where the postman will send the messages    |
| numberOfMessages | the number of messages to be sent to the route             |
| routeType        | type if route e.g. Queue or Topic                          |

## Configuring The Postman on Kubernetes 
To configure the application we will use a Kubernetes configmap
```
#run kubectl create configmap <configmap name> --from-file <path to event-collector.yml file> e.g.
 
 kubectl create configmap postman-config --from-file postman/conf/local/postman.yml
 
 #replace the config map
 kubectl create configmap postman-config --from-file postman.yml -o yaml --dry-run | kubectl replace -f -
 
```
## Deploying to a Kubernetes cluster
To deploy to a Kubernetes cluster a [deploy file](../kubernetes/postman/deploy/postman-deploy.yml) has been created. Run the kubectl command as below

```
kubectl apply -f postman-deploy.yml
```
<br/>

