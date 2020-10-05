package com.uoi.spmsearch.dto;

public class AddressRequest {

    String address = null;

    public AddressRequest() {}

    public AddressRequest(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
