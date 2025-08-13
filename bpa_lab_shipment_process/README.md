# BPALab Shipment Process
## with Camunda 8 SDK for Node.js

It requires running the Self-Managed stack via docker-compose.

## Setup

- Clone the repository locally
- Install dependencies `npm install`
- Run the application with `npm run start`

## Environment

Self-hosted:

.env used if running localy
```
CAMUNDA_SECURE_CONNECTION=false
CAMUNDA_AUTH_STRATEGY='NONE'
MYSQL_HOST=localhost
MYSQL_PORT=3070
```
see docker-compose-processes.yml if running in container
``` 
    environment:
      ZEEBE_ADDRESS: zeebe:26500
      CAMUNDA_AUTH_STRATEGY: NONE
      MYSQL_HOST: mysql
      MYSQL_PORT: 3306
```
