import { createRouter, createWebHistory } from "vue-router";
import HomePage from "../pages/HomePage.vue";
import OrderPage from "../pages/OrderPage.vue";
import { ROUTE_ORDER } from '../globals';
import axios from 'axios';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "home",
      component: HomePage,
    },
    {
      path: ROUTE_ORDER,
      name: "order",
      component: OrderPage,
      beforeEnter: async (to, from, next) => {
        // Add logic to start BPMN process here
        const processKey = 'OrderManagementProcess'; // Replace with your actual BPMN process key
        const variables = {
          // Include the necessary variables here
        };

        try {
          const response = await axios.post('http://localhost:3005/start-process-server', {
            workflowKey: processKey,
            variables
          });

          console.log('Workflow Instance Key:', response.data.response.workflowInstanceKey);
          next(); // Continue with navigation
        } catch (error) {
          console.error('Error starting BPMN process:', error);
          // Handle error as needed
          next(error); // Pass error to the next middleware/handler
        }
      },
    },
  ],
});

export default router;
