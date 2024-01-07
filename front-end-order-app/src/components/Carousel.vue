<template>
    <section id="hero" class="position-relative overflow-hidden py-4"
        style="background: url(../src/assets/bike4-modified.jpg) no-repeat center center / cover;">
        <!-- <div class="pattern-overlay pattern-right position-absolute">
          <img src="images/pattern-hero.png" alt="pattern">
      </div> -->
        <div class="container py-5 mt-5">
            <div class="row align-items-center py-5 mt-5">
                <div class="col-md-6 mb-5 mb-md-0">
                    <h6 class="text-white">QUALITY BICYCLE MANUFACTURERS</h6>
                    <h2 class="text-white fw-bold display-2">Get your favourite bicycle today!</h2>
                    <p class="text-white">Welcome to NewBike GmbH — where craftsmanship meets innovation. Explore
                        our range of meticulously crafted bicycles, designed for peak performance and enduring style.</p>
                    <ul class="list-unstyled">
                        <li class="text-white fw-bold">
                            <svg xmlns="http://www.w3.org/2000/svg" width="23" height="23" viewBox="0 0 23 23" fill="none">
                                <g clip-path="url(#clip0_1_359)">
                                    <path
                                        d="M11.5 0.359375C5.34719 0.359375 0.359375 5.34719 0.359375 11.5C0.359375 17.6528 5.34719 22.6406 11.5 22.6406C17.6528 22.6406 22.6406 17.6528 22.6406 11.5C22.6406 5.34719 17.6528 0.359375 11.5 0.359375ZM11.5 2.51562C16.4653 2.51562 20.4844 6.53393 20.4844 11.5C20.4844 16.4653 16.4661 20.4844 11.5 20.4844C6.5347 20.4844 2.51562 16.4661 2.51562 11.5C2.51562 6.5347 6.53393 2.51562 11.5 2.51562ZM17.7982 8.36746L16.7859 7.34693C16.5762 7.13557 16.2349 7.13418 16.0235 7.34387L9.67375 13.6426L6.98778 10.9349C6.77813 10.7235 6.43681 10.7221 6.22545 10.9318L5.20487 11.9441C4.99352 12.1538 4.99212 12.4951 5.20182 12.7065L9.27987 16.8176C9.48952 17.0289 9.83084 17.0303 10.0422 16.8206L17.7952 9.12983C18.0065 8.92014 18.0079 8.57882 17.7982 8.36746Z"
                                        fill="white" />
                                </g>
                                <defs>
                                    <clipPath id="clip0_1_359">
                                        <rect width="23" height="23" fill="white" />
                                    </clipPath>
                                </defs>
                            </svg>
                            Fully legal, licensed and insured
                        </li>
                        <li class="text-white fw-bold">
                            <svg xmlns="http://www.w3.org/2000/svg" width="23" height="23" viewBox="0 0 23 23" fill="none">
                                <g clip-path="url(#clip0_1_359)">
                                    <path
                                        d="M11.5 0.359375C5.34719 0.359375 0.359375 5.34719 0.359375 11.5C0.359375 17.6528 5.34719 22.6406 11.5 22.6406C17.6528 22.6406 22.6406 17.6528 22.6406 11.5C22.6406 5.34719 17.6528 0.359375 11.5 0.359375ZM11.5 2.51562C16.4653 2.51562 20.4844 6.53393 20.4844 11.5C20.4844 16.4653 16.4661 20.4844 11.5 20.4844C6.5347 20.4844 2.51562 16.4661 2.51562 11.5C2.51562 6.5347 6.53393 2.51562 11.5 2.51562ZM17.7982 8.36746L16.7859 7.34693C16.5762 7.13557 16.2349 7.13418 16.0235 7.34387L9.67375 13.6426L6.98778 10.9349C6.77813 10.7235 6.43681 10.7221 6.22545 10.9318L5.20487 11.9441C4.99352 12.1538 4.99212 12.4951 5.20182 12.7065L9.27987 16.8176C9.48952 17.0289 9.83084 17.0303 10.0422 16.8206L17.7952 9.12983C18.0065 8.92014 18.0079 8.57882 17.7982 8.36746Z"
                                        fill="white" />
                                </g>
                                <defs>
                                    <clipPath id="clip0_1_359">
                                        <rect width="23" height="23" fill="white" />
                                    </clipPath>
                                </defs>
                            </svg>
                            100% trustworthy company
                        </li>
                        <li class="text-white fw-bold">
                            <svg xmlns="http://www.w3.org/2000/svg" width="23" height="23" viewBox="0 0 23 23" fill="none">
                                <g clip-path="url(#clip0_1_359)">
                                    <path
                                        d="M11.5 0.359375C5.34719 0.359375 0.359375 5.34719 0.359375 11.5C0.359375 17.6528 5.34719 22.6406 11.5 22.6406C17.6528 22.6406 22.6406 17.6528 22.6406 11.5C22.6406 5.34719 17.6528 0.359375 11.5 0.359375ZM11.5 2.51562C16.4653 2.51562 20.4844 6.53393 20.4844 11.5C20.4844 16.4653 16.4661 20.4844 11.5 20.4844C6.5347 20.4844 2.51562 16.4661 2.51562 11.5C2.51562 6.5347 6.53393 2.51562 11.5 2.51562ZM17.7982 8.36746L16.7859 7.34693C16.5762 7.13557 16.2349 7.13418 16.0235 7.34387L9.67375 13.6426L6.98778 10.9349C6.77813 10.7235 6.43681 10.7221 6.22545 10.9318L5.20487 11.9441C4.99352 12.1538 4.99212 12.4951 5.20182 12.7065L9.27987 16.8176C9.48952 17.0289 9.83084 17.0303 10.0422 16.8206L17.7952 9.12983C18.0065 8.92014 18.0079 8.57882 17.7982 8.36746Z"
                                        fill="white" />
                                </g>
                                <defs>
                                    <clipPath id="clip0_1_359">
                                        <rect width="23" height="23" fill="white" />
                                    </clipPath>
                                </defs>
                            </svg>
                            Most genuine and transparent
                        </li>
                    </ul>
                </div>
                <div class=" col-md-5 offset-md-1">
                    <div class="form-container p-4">
                        <form @submit.prevent="submitForm">
                            <h2>Order bicycle now!</h2>
                            <div class="row py-4">
                                <div class="col-6">
                                    <div class="mb-3 text-start">
                                        <label for="yourName" class="form-label">Name</label>
                                        <input class="form-control" id="yourName" type="text" placeholder="Your name"
                                            v-model="customerName" />
                                    </div>
                                </div>
                                <div class="col-6">
                                    <div class="mb-3 text-start">
                                        <label for="yourEmailAddress" class="form-label">Email</label>
                                        <input required class="form-control" id="yourEmailAddress" type="email"
                                            placeholder="Your email Address" v-model="customerEmail" />
                                    </div>
                                </div>
                                <div class="col-6">
                                    <div class="mb-3 text-start">
                                        <label for="yourPhoneNumber" class="form-label">Phone</label>
                                        <input required class="form-control" id="yourPhoneNumber" type="text"
                                            placeholder="Your phone number" data-sb-validations=""
                                            v-model="customerPhone" />
                                        <!-- pattern="[a-zA-Z0-9]+" -->
                                    </div>
                                </div>
                                <div class="col-6">
                                    <div class="mb-3 text-start">
                                        <label for="yourAddress" class="form-label">Address</label>
                                        <input class="form-control" id="yourAddress" type="text" placeholder="Your address"
                                            v-model="customerAddress" />
                                    </div>
                                </div>
                                <div class="col-6">
                                    <div class="dropdown text-start">
                                        <label for="selectProduct" class="form-label">Please select type of bicycle:
                                        </label>
                                        <select class="form-select" id="selectProduct" aria-label=".form-select-sm example"
                                            v-model="customerProduct" @change="updateMass">
                                            <option value="option1" selected>Select bicycle</option>
                                            <option value="Mountain Bike">Mountain Bike</option>
                                            <option value="Hybrid 40000 Bicycle">Hybrid 40000 Bicycle</option>
                                            <option value="Speed Thriller Electric 147 Bicycle">Speed Thriller Electric 147
                                                Bicycle</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-6">
                                    <div class="mb-3 text-start">
                                        <label for="mass" class="form-label">Mass (in pounds)</label>
                                        <input class="form-control" id="mass" type="text" v-model="productMass" readonly />
                                    </div>
                                </div>
                                <div class="col-6">
                                    <div class="dropdown text-start">
                                        <label for="selectQuantity" class="form-label">Please select quantity:</label>
                                        <select class="form-select" id="selectQuantity" aria-label=".form-select-sm example"
                                            v-model="customerQuantity" @change="updateOrderType">
                                            <option value="option2" selected>Select quantity</option>
                                            <option value="1">1</option>
                                            <option value="2">2</option>
                                            <option value="3">3</option>
                                            <option value="4">4</option>
                                            <option value="5">5</option>
                                            <option value=">5">More than 5</option>
                                        </select>
                                    </div>
                                </div>
                                <!-- <div class="col-6">
                                    <div class="mb-3 text-start">
                                        <label for="mass" class="form-label">Order Type</label>
                                        <input class="form-control" id="orderType" type="text" :value="customerOrderType"
                                            readonly />
                                    </div>
                                </div> -->
                            </div>
                            <div class="row">
                                <!-- <div class="col-auto mx-auto">
                                    <button class="btn btn-primary" type="submit">Place order!</button>
                                </div> -->
                                <div class="col-auto mx-auto">
                                    <button class="btn btn-primary" type="submit" :disabled="isSubmitDisabled">Place
                                        order!</button>
                                </div>
                            </div>
                        </form>
                    </div>

                </div>
            </div>
        </div>
    </section>
</template>

<script>
export default {
    data() {
        return {
            customerName: 'Mario',
            customerEmail: 'mario@skyrocket.com',
            customerPhone: '+44 584 442 994',
            customerAddress: '221B Baker Street',
            customerProduct: 'option1',
            customerQuantity: 'option2',
            productMass: '',
            productMasses: {
                'Mountain Bike': 18,
                'Hybrid 40000 Bicycle': 29,
                'Speed Thriller Electric 147 Bicycle': 25
            },
            orderStatus: "ORDER_RECEIVED",
            isSubmitDisabled: true,
            customerOrderType: '',
        };
    },
    computed: {
        isSubmitDisabled() {
            return this.customerProduct === 'option1' || this.customerQuantity === 'option2';
        },
        updateOrderType() {
            // Update customerOrderType based on the current quantity
            const quantity = parseInt(this.customerQuantity);

            if (quantity === 1) {
                this.customerOrderType = 'singleOrder';
            } else if (quantity > 1 && quantity <= 5) {
                this.customerOrderType = 'multipleOrder';
            } else if (quantity > 5) {
                this.customerOrderType = 'notAllowed';
            } else {
                this.customerOrderType = '';
            }
        },
    },
    methods: {
        updateMass() {
            this.productMass = this.productMasses[this.customerProduct];
        },
        submitForm() {
            if (!this.isSubmitDisabled) {
                // Your form submission logic
            }
        },

        async submitForm() {
            try {
                const response = await fetch('http://localhost:3000/start-process-server', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({
                        workflowKey: 'Process_1gu1lel',
                        variables: {
                            customerName: this.customerName,
                            customerEmail: this.customerEmail,
                            customerPhone: this.customerPhone,
                            customerAddress: this.customerAddress,
                            customerProduct: this.customerProduct,
                            customerQuantity: this.customerQuantity,
                            customerOrderType: this.customerOrderType,
                            mass: this.mass,
                            productMass: this.productMasses,
                            orderStatus: this.orderStatus,
                            task: "retrieve",
                            place_id: 3,
                            shelf_id: 1,
                            transactionId: 0,
                            //orderType: this.orderType,
                            //key: 'value', //To send additional variables
                        },
                    }),
                });

                const data = await response.json();
                console.log(data);
            } catch (error) {
                console.error('Error:', error);
            }

        },
    },
};
</script>

<style scoped>
/* Add your component-specific styles here */
</style>
