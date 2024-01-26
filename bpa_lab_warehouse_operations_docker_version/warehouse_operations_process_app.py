import asyncio
import os
import sys
import json
import logging
import paho.mqtt.client as mqtt
from dotenv import load_dotenv
from nanoid import generate
from pyzeebe import (
    ZeebeClient, 
    ZeebeWorker, 
    Job, 
    create_camunda_cloud_channel, 
    create_insecure_channel,
)
import mysql.connector
from mysql.connector import errorcode
import grpc
import requests
from zeebe_grpc import gateway_pb2, gateway_pb2_grpc


# ***********************************************************************************************
# ZEEBE-CLIENT, WORKERS, ERROR_HANDLING AND LOGGING
# ***********************************************************************************************

# Loading the environment variables to acces them
load_dotenv()

# Camunda Cloud or local Channel with Python Client
if (os.getenv('IS_CLOUD') == 'true'):
    channel = create_camunda_cloud_channel(
        client_id=os.getenv('ZEEBE_CLIENT_ID'),
        client_secret=os.getenv('ZEEBE_CLIENT_SECRET'),
        cluster_id=os.getenv('CAMUNDA_CLUSTER_ID'),
        region=os.getenv('CAMUNDA_CLUSTER_REGION'),
    )
else:
    zeebe_adress_host=os.getenv("ZEEBE_ADRESS_HOST")
    zeebe_adress_port=os.getenv("ZEEBE_ADRESS_PORT")
    channel = create_insecure_channel(zeebe_adress_host, zeebe_adress_port)


# Python Client for Zeebe and Worker
client = ZeebeClient(channel)
worker = ZeebeWorker(channel, poll_retry_delay=50)

# Separate channel that connects directly via gRPC (without Python Client) to Zeebe
def get_oauth_token():
    # Request OAuth token from Camunda Cloud
    client_id=os.getenv('ZEEBE_CLIENT_ID')
    client_secret=os.getenv('ZEEBE_CLIENT_SECRET')
    audience=os.getenv('ZEEBE_TOKEN_AUDIENCE')
    oauth_url= os.getenv('CAMUNDA_OAUTH_URL')

    headers = {"Content-Type": "application/x-www-form-urlencoded"}
    
    response = requests.post(
        oauth_url,
        data={
            "client_id": client_id,
            "client_secret": client_secret,
            "audience": audience,
            "grant_type": "client_credentials"
        },
        headers=headers
    )

    response.raise_for_status()
    return response.json()["access_token"]


def create_grpc_channel(oauth_token):
    # Authentication function for gRPC
    def oauth2(token):
        return [("authorization", f"Bearer {token}")]
    
    audience=os.getenv('ZEEBE_TOKEN_AUDIENCE')

    # Create the gRPC channel with authentication
    return grpc.secure_channel(
        audience,
        grpc.composite_channel_credentials(
            grpc.ssl_channel_credentials(), grpc.access_token_call_credentials(oauth_token)
        ),
    )

if (os.getenv('IS_CLOUD') == 'true'):
    oauth_token = get_oauth_token()
    seperateChannel = create_grpc_channel(oauth_token)
else:
    zeebe_adress=os.getenv("ZEEBE_ADRESS")
    seperateChannel = grpc.insecure_channel(zeebe_adress)


async def publish_start_message(**variables):
    variables_json = json.dumps(variables)

    stub = gateway_pb2_grpc.GatewayStub(seperateChannel)
    request = gateway_pb2.PublishMessageRequest()
    request.name = START_WAREHOUSE_ROBOT_MESSAGE_ID
    request.timeToLive = 10000  # 10 seconds in milliseconds
    request.messageId = generate()
    request.variables = variables_json

    response = stub.PublishMessage(request)

    return response

# Configure logging
logging.basicConfig(level=logging.INFO,
                    format='%(asctime)s - %(levelname)s - %(message)s',
                    datefmt='%Y-%m-%d %H:%M:%S',
                    handlers=[logging.StreamHandler()])

async def error_handler(exception: Exception, job: Job):
    logging.error(f"Exception: {exception}, Job: {job}")
    await job.set_error_status(f"Failed to handle job {job}. Error: {str(exception)}")

async def logging_decorator(job: Job) -> Job:
    logging.info(job)
    return job


# ***********************************************************************************************
# DATABASE
# ***********************************************************************************************

# Establishes a connection to the MySQL database
async def connect_to_db():
    return mysql.connector.connect(
        host='mysql-db',
        user=os.getenv('MYSQL_USER'),
        password=os.getenv('MYSQL_PASSWORD'),
        database='warehouse'
    )

# Handles potential database errors
async def handle_db_error(job: Job, err):
    # Using a mapping dictionary to handle different error codes and their respective messages
    error_messages = {
        errorcode.ER_ACCESS_DENIED_ERROR: "DB ACCESS DENIED: Can't connect to database",
        errorcode.ER_BAD_DB_ERROR: "DB does not exist: No Database! BAD ERROR",
    }
    
    # Check if the error code exists in our mapping, otherwise use the full error message
    error_message = error_messages.get(err.errno, err._full_msg)
    
    logging.error(error_message)
    await job.set_failure_status(message=error_message)


# ***********************************************************************************************
# MQTT
# ***********************************************************************************************

# Topics under which the operation should be started and completion is reported
pub_topic = "Warehouse_Operations/start"
sub_topic = "Warehouse_Operations/ready"

# Global variable to check whether the storage or retrieval has been carried out
action_completed = False

def on_message(client, userdata, msg):
    global action_completed

    msg = json.loads(msg.payload.decode())
    if msg['task_type'] == 'completed':
        action_completed = True

async def manage_item(function, place_id, task_type):
    global action_completed

    action_completed = False  # Resetting the value for each new operation

    mqtt_message = json.dumps({
        'function': function,
        'place_id': place_id,
        'task_type': task_type
    })

    MqttClient.publish(pub_topic, mqtt_message)  # Publish the message under the topic

    # Wait in the loop until action_completed is True
    while not action_completed:
        await asyncio.sleep(1)  


# Connect to the MQTT Broker and receive messages
MqttClient = mqtt.Client()
MqttClient = mqtt.Client()
MqttClient.on_message = on_message 


# ***********************************************************************************************
# JOB WORKERS
# ***********************************************************************************************

START_WAREHOUSE_ROBOT_MESSAGE_ID = "Msg_StartWarehouseRobot"

# Retrieval Tasks
CHECK_BICYCLE_AVAILABILITY_ID = "check-bicycle-availability"
GET_BICYCLE_FROM_SHELF_TASK_ID = "get-bicycle-from-shelf"
FETCH_BICYCLE_TASK_ID = "fetch-bicycle"
PUT_BICYCLE_TASK_ID = "put-bicycle"
MOVED_TO_DOCK_TASK_ID = "moved-bicycle-to-dock"
MOVED_TO_DOCK_MESSAGE_ID = "Msg_MovedBicycleToDock"
START_WAREHOUSE_ROBOT_MESSAGE_ID = "Msg_StartWarehouseRobot"
UPDATE_BICYCLE_STATUS_ID = "update-bicycle-status"

# Storage Tasks
PICK_BICYCLE_TASK_ID = "pick-bicycle"
STORE_BICYCLE_TASK_ID = "store-bicycle"
STORED_TO_SHELF_TASK_ID = "stored-to-shelf"
STORED_TO_SHELF_MESSAGE_ID = "Msg_StoredToShelf"
STORE_BICYCLE_TO_SHELF_TASK_ID = "store-bicycle-to-shelf"
UPDATE_SHELF_STATUS_ID = "update-shelf-status"


#*************************************************************************
# WORKERS FOR RETRIEVAL
#*************************************************************************

# CheckBicycleAvailability =========================================================
@worker.task(task_type=CHECK_BICYCLE_AVAILABILITY_ID, exception_handler=error_handler)
async def check_inventory(job: Job, **variables) -> dict:
    try:

        piMySqlDB = await connect_to_db()
        logging.info(piMySqlDB)
        mycursor = piMySqlDB.cursor()

        shelf_id= variables.get("shelf_id")
        place_id= variables.get("place_id")
        item = variables.get("item")
        
        # Check whether the requested product is available in the warehouse
        sql_check_item = "SELECT EXISTS (SELECT * FROM place WHERE item = %s)"
        mycursor.execute(sql_check_item, (item,))

        item_exists = mycursor.fetchone()[0]
        
        logging.info(f"item_exists: {item_exists}")
        logging.info(f"shelf_id: {shelf_id}")
        logging.info(f"place_id: {place_id}")
        logging.info(f"item: {item}")

        if item_exists == 1:
            variables = {"stock": "stock"}
        else: 
            variables = {"stock": None}
            #shutdown_flag = True

        return variables
    
    except mysql.connector.Error as err:
        await handle_db_error(job, err)

    finally:
        piMySqlDB.close()

# GetBicycleFromShelf ============================================================
@worker.task(task_type=GET_BICYCLE_FROM_SHELF_TASK_ID, exception_handler=error_handler)
async def get_bicycle_from_shelf(job: Job, **variables)-> dict:
    logging.info(f'{GET_BICYCLE_FROM_SHELF_TASK_ID}:: {variables}')

    await publish_start_message(**variables)

    return variables

# FetchProduct ================================================
@worker.task(task_type=FETCH_BICYCLE_TASK_ID, exception_handler=error_handler, max_jobs_to_activate=1, max_running_jobs=1, timeout_ms=30000)
async def fetch_bicycle(job: Job, **variables) -> dict:
    logging.info(f'{FETCH_BICYCLE_TASK_ID}:: {variables}')

    place_id = variables.get("place_id")
    await manage_item('fetch_bicycle_from_shelf', int(place_id), 'exstore-order')

    return variables

# PutProduct ================================================
@worker.task(task_type=PUT_BICYCLE_TASK_ID, exception_handler=error_handler, max_jobs_to_activate=1, max_running_jobs=1, timeout_ms=30000)
async def put_bicycle(job: Job, **variables) -> dict:
    logging.info(f'{PUT_BICYCLE_TASK_ID}:: {variables}')
    
    place_id = variables.get("place_id")
    await manage_item('move_bicycle_to_dock', int(place_id), 'exstore-order')

    return variables

# Ready for pickup ================================================
@worker.task(task_type=MOVED_TO_DOCK_TASK_ID, exception_handler=error_handler)
async def pickup_ready_throw(job: Job, **variables) -> dict:
    logging.info(f'{MOVED_TO_DOCK_TASK_ID}:: {variables}')

    # Ensure activites are not too fast to distinguish.
    await client.publish_message(
        name=MOVED_TO_DOCK_MESSAGE_ID,
        message_id=generate(),
        correlation_key=str(variables.get("transactionId")),
        variables=variables
    )

    return variables

# UpdateBicycleStatus =======================================================
@worker.task(task_type=UPDATE_BICYCLE_STATUS_ID, exception_handler=error_handler)
async def update_retrieve_inventory(job: Job, place_id):
    try:

        piMySqlDB = await connect_to_db()
        mycursor = piMySqlDB.cursor()

        sql_update = "UPDATE place SET item = 'empty', status = 0, last_user = 'Warehouse Robot 1' WHERE place_id = %s"
        mycursor.execute(sql_update, (place_id,))
    
        piMySqlDB.commit()
    except mysql.connector.Error as err:
        await handle_db_error(job, err)

    finally:
        piMySqlDB.close()


#****************************************************************************
# WORKERS FOR STORAGE
#****************************************************************************

# CheckSpaceAvailability ====================================================
@worker.task(task_type="check-space-availability", exception_handler=error_handler)
async def check_storage(job: Job, **variables)-> dict:
    try:
        piMySqlDB = await connect_to_db()
        logging.info(piMySqlDB)
        mycursor = piMySqlDB.cursor()

        shelf_id= variables.get("shelf_id")
        place_id= variables.get("place_id")

        # Status mit für die Einlagerung eines neues Items auf 0 stehen.
        sql_select_check = "SELECT status FROM place WHERE shelf_id = %s AND place_id = %s"

        mycursor.execute(sql_select_check, (shelf_id, place_id))
        data = mycursor.fetchone()
        status = int(data[0])

        if status == 0:
            variables = {"space": "space"}
        else: 
            variables = {"space": None}

        return variables
    
    except mysql.connector.Error as err:
        await handle_db_error(job, err)

    finally:
        piMySqlDB.close()
        
# StoreBicycleToShelf
@worker.task(task_type=STORE_BICYCLE_TO_SHELF_TASK_ID, exception_handler=error_handler)
async def store_bicycle_to_shelf(job: Job, **variables)-> dict:
    logging.info(f'{STORE_BICYCLE_TO_SHELF_TASK_ID}:: {variables}')

    await publish_start_message(**variables)
    
    return variables

# PickProduct ================================================
@worker.task(task_type=PICK_BICYCLE_TASK_ID, exception_handler=error_handler, max_jobs_to_activate=1, max_running_jobs=1, timeout_ms=30000)
async def robot_stopped_throw(job: Job, **variables) -> dict:
    logging.info(f'{PICK_BICYCLE_TASK_ID}:: {variables}')

    place_id = variables.get("place_id")
    await manage_item('pick_bicycle', int(place_id), 'store-order')

    return variables

# StoreProduct ================================================
@worker.task(task_type=STORE_BICYCLE_TASK_ID, exception_handler=error_handler, max_jobs_to_activate=1, max_running_jobs=1, timeout_ms=30000)
async def robot_stopped_throw(job: Job, **variables) -> dict:
    logging.info(f'{STORE_BICYCLE_TASK_ID}:: {variables}')

    place_id = variables.get("place_id")
    await manage_item('store_bicycle_to_shelf', int(place_id), 'store-order')

    return variables

# Stored successfully ================================================
@worker.task(task_type=STORED_TO_SHELF_TASK_ID, exception_handler=error_handler)
async def store_success_throw(job: Job, **variables) -> dict:
    logging.info(f'{STORED_TO_SHELF_TASK_ID}:: {variables}')

    # Ensure activites are not too fast to distinguish.
    await client.publish_message(
        name=STORED_TO_SHELF_MESSAGE_ID,
        message_id=generate(),
        correlation_key=str(variables.get("transactionId")),
        variables=variables
    )

    return variables

# UpdateShelfStatus =======================================================
@worker.task(task_type=UPDATE_SHELF_STATUS_ID, exception_handler=error_handler)
async def update_storage_inventory(job: Job, item, place_id):
    try:

        piMySqlDB = await connect_to_db()
        mycursor = piMySqlDB.cursor()
        
        sql_update = "UPDATE place SET item = %s, status = 1, last_user = 'Warehouse Robot 1' WHERE place_id = %s"

        mycursor.execute(sql_update, (item, place_id))
        piMySqlDB.commit()

    except mysql.connector.Error as err:
        await handle_db_error(job, err)

    finally:
        piMySqlDB.close()


# ***********************************************************************************************
if __name__ == '__main__':
    logging.info("Starting application")

    # Check if the connection to the MQTT broker is successful
    if (os.getenv('IS_PROD') == 'true'):
        if MqttClient.connect("10.0.0.21", 1883, 60) != 0:
            logging.error("Could not establish connection with the MQTT broker!")
            sys.exit(-1)
    else:
        if MqttClient.connect("mqtt-broker", 1883, 60) != 0:
            logging.error("Could not establish connection with the MQTT broker!")
            sys.exit(-1)

    # Start a new thread that handles incoming MQTT messages
    MqttClient.loop_start()

    # Subscribe to the topic to receive messages
    MqttClient.subscribe(sub_topic)

    # Begin worker process
    loop = asyncio.get_event_loop()

    #asyncio.ensure_future(check_shutdown(loop))
    loop.run_until_complete(worker.work())