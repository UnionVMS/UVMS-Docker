# Wildfly Release
Dockerfile for wildfly with latest released artifacts.

## Creating tags
When a new release is available, a new tag should be pushed to Docker hub, or some other Docker repo.

1. docker build -t uvms/wildfly-release:<release version> .
2. docker push uvms/wildfly-release:<release version>

If Docker authentication is required, run docker login and follow the instructions, then re run step 2 above.