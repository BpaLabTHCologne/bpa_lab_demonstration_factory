# Contributing Guidelines

## Coding Standards

### Naming Conventions for BPMN process models

- **Process names (display name)**: Name of the process with "process" as the last word (e.g. `Warehouse robot process`, `Purchasing process`)
- **Process ID (technical name)**: CamelCase with a clear, descriptive name (e.g. `WarehouseRobotProcess`, `PurchasingProcess`)
- **Name of the BPMN file**: Same name as the Process ID (e.g. `WarehouseRobotProcess.bpmn`, `PurchasingProcess.bpmn`)
- **Task IDs**: Name of the task type in the CamelCase, then an underscore and finally the task name in the CamelCase (e.g. `UserTask_CheckCustomerOrder`, `ServiceTask_StoreCustomerOrder`)
- **Gateway IDs**: (Only divergent gateways are meant!) "Gateway" as first word then underscore with the name of the gateway as CamelCase (e.g. `Gateway_ProductionOrderRequired`)
- **Sequence Flow IDs**: (Only sequence flows after a divergent gateway are meant!) (Only divergent gateways are meant!) "SequenceFlow" as first word then underscore with the name of the gateway as CamelCase and with a "Yes" or "No" at the end, depending on the case (e.g. `SequenceFlow_ProductionOrderRequiredYes`, `SequenceFlow_ProductionOrderRequiredYes`)

Please follow these conventions to keep the BPMN models and the process data understandable.

