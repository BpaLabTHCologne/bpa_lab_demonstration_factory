import {Camunda8} from '@camunda8/sdk'
import {ChildDto, LosslessDto} from "@camunda8/sdk/dist/lib/index.js";

import chalk from 'chalk'
import * as path from 'path'

import { config } from 'dotenv'
config()

import {
    getBikeInstancesForOrderNumber,
    getBikeInstancesNoOrderNumberAndShippedFalse,
    shipBikeInstances,
    shipBikeInstance
// @ts-ignore
} from "./dbConnection.ts";
import {parseVariablesAndCustomHeadersToJSON} from "@camunda8/sdk/dist/zeebe/lib";

//import './zeebeWorkers';
class OrderCustomerDTO extends LosslessDto {
    name! : string
    email! : string
    adress! : string
}
class OrderDTO extends LosslessDto {
    orderNumber! : string
    @ChildDto(OrderCustomerDTO)
    orderCustomer! : OrderCustomerDTO
}
class BikeDTO extends LosslessDto {
    serial_number! : string
    bike_model_id? : string
}
class ProductListDTO extends LosslessDto {
    orderNumber! : string
    @ChildDto(BikeDTO)
    productList!: Array<BikeDTO>
}

const c8 = new Camunda8({
    ZEEBE_GRPC_ADDRESS: process.env.ZEEBE_ADDRESS,
    // @ts-ignore
    CAMUNDA_AUTH_STRATEGY: process.env.CAMUNDA_AUTH_STRATEGY
})
const zbc = c8.getZeebeGrpcApiClient(
)

const getLogger = (prefix: string, color: any) => (msg: string) => console.log(color(`[${prefix}] ${msg}`))

console.log(`Creating worker checkProductInformation`)
zbc.createWorker({
//    inputVariableDto: PurchaseComponentDTO,
    taskType: 'checkProductInformation',
    taskHandler: async job => {
        const log = getLogger('Zeebe Worker', chalk.blueBright)
        log(`handling job of type ${job.type}`)
        const orderNumber  = job.variables.orderNumber;
        console.log(job.variables);
        const shippmentOrderCorrelation = job.variables.shipmentOrderCorrelation
        var productList;
        if (orderNumber)
            productList = await getBikeInstancesForOrderNumber(orderNumber.toString());
        else
            productList = await getBikeInstancesNoOrderNumberAndShippedFalse();
        console.log(productList);
        return job.complete({
            // @ts-ignore
            serviceTaskOutcome: orderNumber, productList
        })
    }
})

console.log(`Creating worker shipBikeInstances`)
zbc.createWorker({
    inputVariableDto: ProductListDTO,
    taskType: 'shipBikeInstances',
    taskHandler: async job => {
        const log = getLogger('Zeebe Worker', chalk.blueBright)
        log(`handling job of type ${job.type}`)
        const orderNumber  = job.variables.orderNumber;
        console.log(job.variables);
        if (orderNumber)
            await shipBikeInstances(orderNumber.toString());
        else
            {   const productList = job.variables.productList
                productList.forEach(bike => {
                    console.log(bike.serial_number);
                    shipBikeInstance(bike.serial_number)
                })
            }
        return job.complete({
            // @ts-ignore
            serviceTaskOutcome: orderNumber
        })
    }
})

console.log(`Creating worker sendShippedEmail`)
zbc.createWorker({
    inputVariableDto: OrderDTO,
    taskType: 'sendShippedEmail',
    taskHandler: async job => {
        const log = getLogger('Zeebe Worker', chalk.blueBright)
        log(`handling job of type ${job.type}`)
        const orderNumber  = job.variables.orderNumber;
        console.log(orderNumber);
        const orderDTO = job.variables
        console.log(orderDTO.orderCustomer);
        return job.complete()
    }
})

console.log(`Creating worker sendFinishedShipment`)
zbc.createWorker({
    taskType: 'sendFinishedShipment',
    taskHandler: async job => {
        const log = getLogger('Zeebe Worker', chalk.blueBright)
        log(`handling job of type ${job.type}`)
        if (job.variables.shipmentOrderCorrelation) {
            const shipmentOrderCorrelation = job.variables.shipmentOrderCorrelation.toString()
            console.log("shipmentOrderCorrelation: " + shipmentOrderCorrelation);
            zbc.publishMessage({
                name: "MsgShippingFinished",
                correlationKey: shipmentOrderCorrelation
                // @ts-ignore
//                variables: shipmentOrderCorrelation
            })
        }
        return job.complete()
    }
})

async function deployProcessFiles() {
    var deploy = await zbc.deployResource({
        processFilename: path.join(process.cwd(), "bpmn/bpa_lab_shipment-process.bpmn")
    });
    console.log(
        `[Zeebe] Deployed process ${deploy.deployments[0].process.bpmnProcessId}`
    );
    deploy = await zbc.deployResource({
        processFilename: path.join(process.cwd(), "bpmn/shipmentInputData.form")
    });
    console.log(
        `[Zeebe] Deployed bpmn/shipmentInputData.form`
    );
    deploy = await zbc.deployResource({
        processFilename: path.join(process.cwd(), "bpmn/checkInformation.form")
    });
    console.log(
        `[Zeebe] Deployed bpmn/checkInformation.form`
    );
}

deployProcessFiles();