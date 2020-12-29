package net.bugfixers.e_commerce.models;

public class Order {

    private String orderId;
    private long time;
    private String address;
    private String city;
    private String zipCode;

    public Order() {

    }

    public Order(String orderId, long time, String address, String city, String zipCode) {
        this.orderId = orderId;
        this.time = time;
        this.address = address;
        this.city = city;
        this.zipCode = zipCode;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
}
