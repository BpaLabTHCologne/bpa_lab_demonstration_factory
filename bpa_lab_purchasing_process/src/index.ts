import {Camunda8} from '@camunda8/sdk'
import {ChildDto, LosslessDto} from "@camunda8/sdk/dist/lib/index.js";

import chalk from 'chalk'
import * as path from 'path'

import { config } from 'dotenv'
config()

// @ts-ignore
import {getPurchaseOrder, createPurchaseOrder, getVendorsForBikeComponent, storeBikeComponent, updatePurchaseOrder} from "./dbConnection.ts";

//import './zeebeWorkers';


const c8 = new Camunda8({
    ZEEBE_ADDRESS: process.env.ZEEBE_ADDRESS
})

const zbc = c8.getZeebeGrpcApiClient()

const getLogger = (prefix: string, color: any) => (msg: string) => console.log(color(`[${prefix}] ${msg}`))

class BikeComponentDTO extends LosslessDto {
    title! : string
    amount! : number
}

class PurchaseComponentDTO extends LosslessDto {
    purchaseOrderNumber! : string
    productionOrderNumber! : string
    purchaseOrderCorrelation! : string
    selectedVendor!: string
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
        if (!purchaseComponentDTO.productionOrderNumber) {
            // @ts-ignore
            purchaseComponentDTO.productionOrderNumber = "no Order"
        }
        const purchaseOrderNumber = await createPurchaseOrder(purchaseComponentDTO.productionOrderNumber
                                                        , purchaseComponentDTO.purchaseBikeComponent.amount
                                                        , purchaseComponentDTO.purchaseBikeComponent.title);
        // @ts-ignore
        purchaseComponentDTO.purchaseOrderNumber = purchaseOrderNumber;
        console.log(purchaseComponentDTO);
        const vendors = await getVendorsForBikeComponent(purchaseComponentDTO.purchaseBikeComponent.title)
        console.log(vendors);
        // @ts-ignore
        const vendorList = vendors.map(item => ({
            label: item.contact + " " + item.contact + " " + item.price,
            value: item.name
        }))
        console.log(vendorList);
        const purchaseCount = purchaseComponentDTO.purchaseBikeComponent.amount

        return job.complete({
        })
    }
})

console.log(`Creating worker storeBikeComponents`)
zbc.createWorker({
    inputVariableDto: PurchaseComponentDTO,
    taskType: 'storeBikeComponents',
    taskHandler: async job => {
        const log = getLogger('Zeebe Worker', chalk.blueBright)
        log(`handling job of type ${job.type}`)
        const purchaseComponentDTO = job.variables;
        console.log(purchaseComponentDTO);
        const result = await storeBikeComponent(purchaseComponentDTO.purchaseBikeComponent.title,
            purchaseComponentDTO.purchaseBikeComponent.amount)
        console.log(result);
        const selectedVendor = job.variables.selectedVendor;
        const purchaseOrder = await updatePurchaseOrder(purchaseComponentDTO.purchaseOrderNumber
            , purchaseComponentDTO.purchaseBikeComponent.amount
            , selectedVendor);
        console.log(purchaseOrder);
        return job.complete()
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
        if (!purchaseComponentDTO.purchaseOrderCorrelation) {
            console.log("finished sendFinishedPurchaseOrder without sending message");
        } else {
            console.log("purchaseOrderCorrelation: " + purchaseComponentDTO.purchaseOrderCorrelation);
            zbc.publishMessage({
                name: "MsgPurchaseFinished",
                correlationKey: purchaseComponentDTO.purchaseOrderCorrelation,
                // @ts-ignore
                variables: purchaseComponentDTO
            })
        }
        return job.complete()
    }
})
