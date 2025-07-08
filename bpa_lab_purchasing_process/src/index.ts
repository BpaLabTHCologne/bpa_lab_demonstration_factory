import {Camunda8} from '@camunda8/sdk'
import {ChildDto, LosslessDto} from "@camunda8/sdk/dist/lib/index.js";

import chalk from 'chalk'
import * as path from 'path'

import { config } from 'dotenv'
config()

// @ts-ignore
import {getPurchaseOrder, getVendorsForBikeComponent} from "./dbConnection.ts";
import {parseVariablesAndCustomHeadersToJSON} from "@camunda8/sdk/dist/zeebe/lib";

//import './zeebeWorkers';


const c8 = new Camunda8()
const zbc = c8.getZeebeGrpcApiClient()
const operate = c8.getOperateApiClient()
const optimize = c8.getOptimizeApiClient() // unused
const tasklist = c8.getTasklistApiClient()

const getLogger = (prefix: string, color: any) => (msg: string) => console.log(color(`[${prefix}] ${msg}`))

class BikeComponentDTO extends LosslessDto {
    title! : string
    amount! : number
}

class PurchaseComponentDTO extends LosslessDto {
    purchaseOrderNumber! : string
    productionOrderNumber! : string
    purchaseOrderCorrelation! : string
    @ChildDto(BikeComponentDTO)
    purchaseBikeComponent! : BikeComponentDTO
}

console.log(`Creating worker getPurchaseOrder`)
zbc.createWorker({
    inputVariableDto: PurchaseComponentDTO,
    taskType: 'getPurchaseOrder',
    taskHandler: async job => {
        const log = getLogger('Zeebe Worker', chalk.blueBright)
        log(`handling job of type ${job.type}`)
        const purchaseComponentDTO = job.variables;
        console.log(purchaseComponentDTO);
        const purchaseOrder = await getPurchaseOrder(purchaseComponentDTO.purchaseOrderNumber);
        console.log(purchaseOrder);
        const vendorList = await getVendorsForBikeComponent(purchaseComponentDTO.purchaseBikeComponent.title)
        console.log(vendorList);
        return job.complete({
            // @ts-ignore
            serviceTaskOutcome: purchaseComponentDTO, vendorList
        })
    }
})

console.log(`Creating worker sendFinishedPurchaseOrder`)
zbc.createWorker({
    inputVariableDto: PurchaseComponentDTO,
    taskType: 'sendFinishedPurchaseOrder',
    taskHandler: async job => {
        const log = getLogger('Zeebe Worker', chalk.blueBright)
        log(`handling job of type ${job.type}`)
        const purchaseComponentDTO = job.variables;
        console.log("purchaseOrderCorrelation: " + purchaseComponentDTO.purchaseOrderCorrelation);
        zbc.publishMessage({
            name: "MsgPurchaseFinished",
            correlationKey: purchaseComponentDTO.purchaseOrderCorrelation
            // @ts-ignore
//            variables: purchaseComponentDTO
            })
        return job.complete()
    }
})
