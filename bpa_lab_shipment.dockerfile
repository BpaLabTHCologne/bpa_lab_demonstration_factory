FROM node:lts-alpine

# make the 'app' folder the current working directory
WORKDIR /app

# copy both 'package.json' and 'package-lock.json' (if available)
COPY bpa_lab_shipment_process/package.json ./
COPY bpa_lab_shipment_process/package-lock.json ./

# copy project files and folders to the current working directory (i.e. 'app' folder)
COPY bpa_lab_shipment_process .

# install project dependencies
RUN npm install

CMD [ "npm", "run", "start" ]