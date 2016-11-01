# ActiveMQ
Dockerfile for ActiveMQ. Default version of ActiveMQ is 5.13.2

## Creating tags
If UVMS moves to a new version of ActiveMQ there are a few steps to update this container.

1. docker build -t uvms/activemq:<activemq version> --build-arg ACTIVEMQ_VERSION=<activemq version> .
2. docker push uvms/activemq:<activemq version>

If Docker authentication is required, run docker login and follow the instructions, then re run step 2 above.