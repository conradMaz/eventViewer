# event-collecter

# Overview

Event-collector will play the role of the message consumer. The application will be packaged as a Docker image and deployed to a Kubernetes cluster. 

The Event Collector can be configured to listen for messages on a queue or a topic.

## Building the event-collector Docker Image
To build the event-collector image run the command

```
mvn clean install
```

To check the image has been built successfully run

```
docker images
```
 
## Configuration

To configure the event-collector. Create a event-collector.yml file and add it to the conf/local directory. In Kubernetes
run the command below to create a configmap: 

```
 #run kubectl create configmap <configmap name> --from-file <path to event-collector.yml file> e.g.
 
 kubectl create configmap event-collector-config --from-file event-collector/conf
 
 #replace the config map
 kubectl create configmap event-collector-config --from-file event-collector.yml -o yaml --dry-run | kubectl replace -f -
 
```

The configmap can then be used when configuring how the event-collector will be deployed to Kubernetes

### Configuration properties

The table below describes which properties can be used when configuring the event-collector

| Property       | Description                                                                             |
| -------------- | --------------------------------------------------------------------------------------- |
| name           | name of the event-collector instance                                                    |
| livenessProbe  | TCP port which will be used to check if the event-collector is alive                    |

 **Broker config**

| Property       | Description                                                                             |
| ---------------| --------------------------------------------------------------------------------------- |
| name           | broker name                                                                             |
| hosts          | connection details of the broker                                                        |

**Event Source**

| Property       | Description                                                                             |
| -------------- | --------------------------------------------------------------------------------------- |
| name           | name of the event-source i.e name of queue or topic on broker                           |
| sourceType     | queue/topic

The config file is a Yaml file and will have to be indented using the YAML format. Please see an example of the file [here](../kubernetes/event-collector/configmap/event-collector.yml)

## Deploying to Kubernetes

To deploy the event-collector to a Kubernetes cluster a deployment file will need to be created. The [deployment file](../kubernetes/event-collector/deploy/event-collector-deploy.yml) can be deployed using the command.
 
```
kubectl apply -f event-collector-deploy.yml

```