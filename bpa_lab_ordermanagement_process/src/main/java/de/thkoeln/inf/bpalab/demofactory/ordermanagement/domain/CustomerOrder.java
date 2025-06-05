package de.thkoeln.inf.bpalab.demofactory.ordermanagement.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.Proxy;

import java.time.LocalDate;
import java.util.Set;


@Entity
@Proxy(lazy = false)
public class CustomerOrder {

    @Id
    @Column(nullable = false, updatable = false)
    private String customerOrderNumber;

    @Column
    private String customerName;

    @Column
    private String customerEmail;

    @Column
    private String customerAdress;

    @Column
    private String creationDate;

    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private Set<CustomerOrderItem> orderItems;

    public String getCustomerOrderNumber() {
        return customerOrderNumber;
    }

    public void setCustomerOrderNumber(final String customerOrderNumber) {
        this.customerOrderNumber = customerOrderNumber;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(final String creationDate) {
        this.creationDate = creationDate;
    }

    public Set<CustomerOrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(final Set<CustomerOrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerAdress() {
        return customerAdress;
    }

    public void setCustomerAdress(String customerAdress) {
        this.customerAdress = customerAdress;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
