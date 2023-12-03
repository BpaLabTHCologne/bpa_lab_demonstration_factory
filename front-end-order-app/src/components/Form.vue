<template>
    <div class="form-section">
        <div class="container pt-4">
            <div class="form-container p-4">
                <form @submit.prevent="onSubmit">
                    <h2>Place your order now!</h2>
                    <div class="row py-4">
                        <div class="col-6">
                            <div class="mb-3 text-start">
                                <label for="yourName" class="form-label">Name</label>
                                <input class="form-control" id="yourName" type="text" placeholder="Your name"
                                    v-model="name" />

                            </div>
                        </div>
                        <div class="col-6">
                            <div class="mb-3 text-start">
                                <label for="yourEmailAddress" class="form-label">Email</label>
                                <input required class="form-control" id="yourEmailAddress" type="email"
                                    placeholder="Your email Address" v-model="email" />

                            </div>
                        </div>
                        <div class="col-6">
                            <div class="mb-3 text-start">
                                <label for="yourPhoneNumber" class="form-label">Phone</label>
                                <input required class="form-control" id="yourPhoneNumber" type="text"
                                    placeholder="Your phone number" data-sb-validations="" v-model="phone" />
                                <!-- pattern="[a-zA-Z0-9]+" -->

                            </div>
                        </div>
                        <div class="col-6">
                            <div class="mb-3 text-start">
                                <label for="yourAddress" class="form-label">Address</label>
                                <input class="form-control" id="yourAddress" type="text" placeholder="Your address"
                                    v-model="address" />

                            </div>
                        </div>
                        <div class="col-6">
                        <div class="dropdown text-start">
                            <label for="selectProduct" class="form-label">Please select a bicycle: </label>
                            <select class="form-select form-select-sm" id="selectProduct"
                                aria-label=".form-select-sm example" v-model="product" @change="updateMass">
                                <option selected>Open this select menu</option>
                                <option value="Mountain Bicycle">Mountain Bicycle</option>
                                <option value="Hybrid 40000 Bicycle">Hybrid 40000 Bicycle</option>
                                <option value="Speed Thriller Electric 147 Bicycle">Speed Thriller Electric 147
                                    Bicycle</option>
                            </select>
                        </div>
                    </div>
                        <div class="col-6">
              <div class="mb-3 text-start">
                <label for="mass" class="form-label">Mass (in pounds)</label>
                <input class="form-control" id="mass" type="text" v-model="mass" readonly />
              </div>
            </div>
                        <div class="col-6">
                            <div class="dropdown text-start">
                                <label for="selectQuantity" class="form-label">Please select quantity: </label>
                                <select class="form-select form-select-sm" id="selectQuantity"
                                    aria-label=".form-select-sm example" v-model="quantity">
                                    <option selected>Open this select menu</option>
                                    <option value="1">1</option>
                                    <option value="2">2</option>
                                    <option value="3">3</option>
                                </select>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-auto">
                            <button class="btn btn-primary" type="submit">Place order!</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</template>

<script>
import { ROUTE_ORDER } from '../globals'
export default {
    name: 'FormSubmit',
    props: {
        msg: String
    },
    data() {
        return {
            name: 'Mario',
            email: 'mario@skyrocket.com',
            phone: '+44 584 442 994',
            address: '221B Baker Street',
            product: this.product,
            quantity: this.quantity,
            mass: '',
            productMasses: {
                'Mountain Bicycle': 18,
                'Hybrid 40000 Bicycle': 29,
                'Speed Thriller Electric 147 Bicycle': 25
            }

            
        }
    },
    methods: {
        getProcessDefinitionId(processDefinitions, key){
          const [processDefinition] = processDefinitions.filter((definition) => definition.key === key)
          const { id } = processDefinition
          return id
        },
        updateMass() {
      this.mass = this.productMasses[this.product];
    },
        async onSubmit() {
          const processDefinitionsResponse = await fetch('/engine-rest/process-definition')
          const processDefinitions = await processDefinitionsResponse.json()
          const processDefinitionId = this.getProcessDefinitionId(processDefinitions, 'Order-management')
          await fetch(`/engine-rest/process-definition/key/Order-management/start`, {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
            },
            body: JSON.stringify(
                {
                  "variables": {
                    "name": {
                      "value": this.name,
                      "type": "String"
                    },
                    "email": {
                      "value": this.email,
                      "type": "String"
                    },
                    "phoneNumber": {
                      "value": this.phone,
                      "type": "String"
                    },
                    "address": {
                      "value": this.address,
                      "type": "String"
                    },
                    "product": {
                      "value": this.product,
                      "type": "String"
                    },
                    "quantity": {
                      "value": parseInt(this.quantity),
                      "type": "Integer"
                    },
                    "mass": {
                        "value": parseInt(this.mass),
                        "type": "Integer"
                    }
                  },
                  "businessKey": "placeOrder"
                },
            )
          })
            return
            // const userName = this.name;
            // const userEmail = this.email;
            // const userPhone = this.phone;
            // const userAddress = this.address;
            // const userProduct = this.product;
            // const userQuantity = this.quantity;
            // try {
            //     await axios.get("http://localhost:8080", {
            //         name: userName,
            //         email: userEmail,
            //         phone: userPhone,
            //         address: userAddress,
            //         product: userProduct,
            //         quantity: userQuantity,
            //     });
            //     console.log(this.name)
            //     console.log(this.email)
            //     console.log(this.phone)
            //     console.log(this.address)
            //     console.log(this.product)
            //     console.log(this.quantity)
            // } catch (err) {
            //     this.error = err.message;
            // }
        },
    }
}
</script>

<style>
/* .bg-image {
    background-image: url("../assets/bike4.jpg");
    height: 100vh;
    background-position: center;
    background-repeat: no-repeat;
    background-size: cover;
} */



.form-section {
    background-image: url('../assets/bike4.jpg');
    background-size: cover;
}

.form-container {
    background-color: #ffffffbf;
    width: auto;
}

#dropdownMenuButton1 {
    background-color: black !important;
}
</style>

