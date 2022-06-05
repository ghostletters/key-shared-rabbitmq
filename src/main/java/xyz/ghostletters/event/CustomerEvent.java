package xyz.ghostletters.event;

public class CustomerEvent {
    String customerId;
    String customerName;

    public CustomerEvent(String id, String name) {
        this.customerId = id;
        this.customerName = name;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
