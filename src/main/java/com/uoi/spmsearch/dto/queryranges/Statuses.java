package com.uoi.spmsearch.dto.queryranges;

public class Statuses {

    Status forLease;
    Status forSale;

    public Statuses(Status forLease, Status forSale) {
        this.forLease = forLease;
        this.forSale = forSale;
    }

    public Statuses() {
    }

    public Status getForLease() {
        return forLease;
    }

    public void setForLease(Status forLease) {
        this.forLease = forLease;
    }

    public Status getForSale() {
        return forSale;
    }

    public void setForSale(Status forSale) {
        this.forSale = forSale;
    }
}
