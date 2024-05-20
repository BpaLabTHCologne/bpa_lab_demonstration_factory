# Contributing Guidelines

## Coding Standards

### Naming Conventions

- **Process names (display name)**: Name des Prozesses mit "process" als letztes Wort (z.B. `Warehouse robot process`, `Purchasing process`)
- **Process ID (technical name)** CamelCase mit klarem, beschreibendem Namen (z.B. `WarehouseRobotProcess`, `PurchasingProcess`)
- **Name of the BPMN file** Gleicher Name wie die Process ID (z.B. `WarehouseRobotProcess.bpmn`, `PurchasingProcess.bpmn`)
- 
- **Funktionen**: lower_snake_case und sollten als Verben formuliert werden (z.B. `calculate_total`, `validate_user`)
- **Variablen**: lower_snake_case, klar und präzise (z.B. `user_count`, `image_path`)
- **Konstanten**: UPPER_SNAKE_CASE (z.B. `MAX_RETRY`, `DEFAULT_TIMEOUT`)

Please follow these conventions to keep the code understandable.

