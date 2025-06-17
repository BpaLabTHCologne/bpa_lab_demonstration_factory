Message Names:
- MsgProductionFinished
- MsgStartProductionOrder

CorrelationKeys:

    productionOrderCorrelation =
    CustomerOrder.customerOrderNumber + ProductionOrder.productionOrderNumber

JPAConfiguration 

    @Configuration
    @EnableJpaRepositories({"de.thkoeln.inf.bpalab.demofactory"})
    @EntityScan({"de.thkoeln.inf.bpalab.demofactory"})
    @ComponentScan({"de.thkoeln.inf.bpalab.demofactory"})
    public class PersistenceJPAConfig {}