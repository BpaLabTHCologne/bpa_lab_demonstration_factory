import {Camunda8} from '@camunda8/sdk'
import {ChildDto, LosslessDto} from "@camunda8/sdk/dist/lib/index.js";

import chalk from 'chalk'
import * as path from 'path'

import { config } from 'dotenv'
config()

const c8 = new Camunda8()
const zbc = c8.getZeebeGrpcApiClient()
const operate = c8.getOperateApiClient()
const optimize = c8.getOptimizeApiClient() // unused
const tasklist = c8.getTasklistApiClient()

const getLogger = (prefix: string, color: any) => (msg: string) => console.log(color(`[${prefix}] ${msg}`))

import mysql from 'mysql2';

let con = mysql.createConnection({
    host: "localhost",
    port: 3070,
    user: "root",
    password: "P4ssword!",
    database: "bpa_lab_demonstration_factory"
});

function getPurchaseOrder(purchaseOrderNumber : string) {
    return new Promise((resolve, reject) => {
        const query = `SELECT * FROM purchase_order where purchase_order_number = ?`;
        con.query(query, [purchaseOrderNumber], (err, result) => {
            if (err) return reject(err);
            resolve(result ? result[0] : null);
        })
    })
}

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
        console.log(purchaseComponentDTO.purchaseOrderNumber)
        console.log(purchaseComponentDTO.purchaseBikeComponent.title)
        const purchaseOrder = await getPurchaseOrder(purchaseComponentDTO.purchaseOrderNumber);
        console.log(purchaseOrder);
        return job.complete({
            // @ts-ignore
            serviceTaskOutcome: purchaseComponentDTO
        })
    }
})

console.log(`Creating worker sendFinishedBikeModelProductionOrder`)
zbc.createWorker({
    inputVariableDto: PurchaseComponentDTO,
    taskType: 'sendFinishedBikeModelProductionOrder',
    taskHandler: async job => {
        const log = getLogger('Zeebe Worker', chalk.blueBright)
        log(`handling job of type ${job.type}`)
        const purchaseComponentDTO = job.variables;
        zbc.publishMessage({
            name: "MsgPurchaseFinished",
            correlationKey: purchaseComponentDTO.purchaseOrderCorrelation,
            // @ts-ignore
            variables: purchaseComponentDTO
            })
        return job.complete()
    }
})
