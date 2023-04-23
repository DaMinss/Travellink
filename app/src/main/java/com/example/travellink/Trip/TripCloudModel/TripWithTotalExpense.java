package com.example.travellink.Trip.TripCloudModel;

import com.example.travellink.Trip.TripModel.Trip;

public class TripWithTotalExpense {
    Trip trips;
    float Price;

    public TripWithTotalExpense(Trip trips, float price) {
        this.trips = trips;
        Price = price;
    }

    public TripWithTotalExpense() {

    }

    public Trip getTrips() {
        return trips;
    }

    public void setTrips(Trip trips) {
        this.trips = trips;
    }

    public float getPrice() {
        return Price;
    }

    public void setPrice(float price) {
        Price = price;
    }
}
