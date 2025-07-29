import mysql from 'mysql2';

export let con = mysql.createConnection({
    host: "localhost",
    port: 3070,
    user: "root",
    password: "P4ssword!",
    database: "bpa_lab_demonstration_factory"
});

export function getPurchaseOrder(purchaseOrderNumber : string) {
    return new Promise((resolve, reject) => {
        const query = `SELECT * FROM purchase_order where purchase_order_number = ?`;
        con.query(query, [purchaseOrderNumber], (err, result) => {
            if (err) return reject(err);
            resolve(result ? result[0] : null);
        })
    })
}

export function getVendorsForBikeComponent(bikeComponent : string) {
    return new Promise((resolve, reject) => {
        const query = `select v.vendor_name as name, v.vendor_contact as contact, vb.price as price from bike_component as bc
                        left join vendor_bike_component as vb on bc.title = vb.bikecomponent_title
                        left join vendor as v on vb.vendor_name = v.vendor_name
                        where bc.title = ?;`
        con.query(query, [bikeComponent], (err, result) => {
            if (err) return reject(err);
            resolve(result ? result : null);
        })
    })
}

function getBikeComponentQuantity(bikeComponent : string) {
    return new Promise((resolve, reject) => {
        const query = `SELECT quantity
                       FROM bike_component
                       where title = ?`;
        con.query(query, [bikeComponent], (err, result) => {
            if (err) return reject(err);
            resolve(result ? result[0].quantity : 0);
        })
    })
}

export function storeBikeComponent(bikeComponent : string, quantity: number) {
    return new Promise(async (resolve, reject) => {
        const bikeComponentQuantity = await getBikeComponentQuantity(bikeComponent)
        // @ts-ignore
        let updateQuantity: number = bikeComponentQuantity + quantity
        const queryUpdate = `update bike_component
                             set quantity = ?
                             where title = ?`;
        con.query(queryUpdate, [updateQuantity, bikeComponent], (err, result) => {
            if (err) return reject(err);
            resolve(result ? result : null);
        })
    })
}