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
        const query = `select v.vendor_name as name, v.vendor_contact as contact, vb.price from bike_component as bc
                        left join vendor_bikecomponent as vb on bc.title = vb.bikecomponent_title
                        left join vendor as v on vb.vendor_name = v.vendor_name
                        where bc.title = ?;`
        con.query(query, [bikeComponent], (err, result) => {
            if (err) return reject(err);
            resolve(result ? result : null);
        })
    })
}