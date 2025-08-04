import mysql from 'mysql2';

export let con = mysql.createConnection({
    host: "localhost",
    port: 3070,
    user: "root",
    password: "P4ssword!",
    database: "bpa_lab_demonstration_factory"
});

export function getBikeInstancesForOrderNumber(orderNumber : string) {
    return new Promise((resolve, reject) => {
        const query = `SELECT * FROM bike_instance where customer_order_number = ?`;
        con.query(query, [orderNumber], (err, result) => {
            if (err) return reject(err);
            resolve(result ? result : null);
        })
    })
}

export function getBikeInstancesNoOrderNumberAndShippedFalse() {
    return new Promise((resolve, reject) => {
        const query = `SELECT * FROM bike_instance where customer_order_number is null and shipped = false`;
        con.query(query, [], (err, result) => {
            if (err) return reject(err);
            resolve(result ? result : null);
        })
    })
}

export function shipBikeInstances(orderNumber : string) {
    return new Promise(async (resolve, reject) => {
        const queryUpdate = `update bike_instance
                             set shipped = true
                             where customer_order_number = ?`;
        con.query(queryUpdate, [orderNumber], (err, result) => {
            if (err) return reject(err);
            resolve(result ? result : null);
        })
    })
}

export function shipBikeInstance(serialNumber : string) {
    return new Promise(async (resolve, reject) => {
        const queryUpdate = `update bike_instance set shipped = true where serial_number = ?`;
        con.query(queryUpdate, [serialNumber], (err, result) => {
            if (err) return reject(err);
            resolve(result ? result : null);
        })
    })
}