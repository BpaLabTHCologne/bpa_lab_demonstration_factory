import {Camunda8} from '@camunda8/sdk'
import {ChildDto, LosslessDto} from "@camunda8/sdk/dist/lib/index.js";

import chalk from 'chalk'
import * as path from 'path'

import { config } from 'dotenv'
config()

// @ts-ignore
import {getBikeInstancesForOrderNumber, shipBikeInstances} from "./dbConnection.ts";
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
        const productList = await getBikeInstancesForOrderNumber(orderNumber.toString());
        console.log(productList);
        return job.complete({
            // @ts-ignore
            serviceTaskOutcome: orderNumber, productList
        })
    }
})

console.log(`Creating worker shipBikeInstances`)
zbc.createWorker({
//    inputVariableDto: PurchaseComponentDTO,
    taskType: 'shipBikeInstances',
    taskHandler: async job => {
        const log = getLogger('Zeebe Worker', chalk.blueBright)
        log(`handling job of type ${job.type}`)
        const orderNumber  = job.variables.orderNumber;
        console.log(job.variables);
        const result = await shipBikeInstances(orderNumber.toString());
        console.log(result);
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
        const shipmentOrderCorrelation = job.variables.shipmentOrderCorrelation.toString()
        console.log("shipmentOrderCorrelation: " + shipmentOrderCorrelation);
        zbc.publishMessage({
            name: "MsgPurchaseFinished",
            correlationKey: shipmentOrderCorrelation
            // @ts-ignore
//            variables: purchaseComponentDTO
            })
        return job.complete()
    }
})

