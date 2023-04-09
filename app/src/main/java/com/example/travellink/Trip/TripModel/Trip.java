package com.example.travellink.Trip.TripModel;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "trip")
public class Trip {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String Trip_name;
    private String Trip_departure;
    private String Trip_arrival;
    private String Trip_status;
    private String Trip_start_date;
    private String Trip_end_date;
    private String Note;

    public Trip() {
    }

    public Trip(int id, String trip_name, String trip_departure, String trip_arrival, String trip_status, String trip_start_date, String trip_end_date, String note) {
        this.id = id;
        Trip_name = trip_name;
        Trip_departure = trip_departure;
        Trip_arrival = trip_arrival;
        Trip_status = trip_status;
        Trip_start_date = trip_start_date;
        Trip_end_date = trip_end_date;
        Note = note;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTrip_name() {
        return Trip_name;
    }

    public void setTrip_name(String trip_name) {
        Trip_name = trip_name;
    }

    public String getTrip_departure() {
        return Trip_departure;
    }

    public void setTrip_departure(String trip_departure) {
        Trip_departure = trip_departure;
    }

    public String getTrip_arrival() {
        return Trip_arrival;
    }

    public void setTrip_arrival(String trip_arrival) {
        Trip_arrival = trip_arrival;
    }

    public String getTrip_status() {
        return Trip_status;
    }

    public void setTrip_status(String trip_status) {
        Trip_status = trip_status;
    }

    public String getTrip_start_date() {
        return Trip_start_date;
    }

    public void setTrip_start_date(String trip_start_date) {
        Trip_start_date = trip_start_date;
    }

    public String getTrip_end_date() {
        return Trip_end_date;
    }

    public void setTrip_end_date(String trip_end_date) {
        Trip_end_date = trip_end_date;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }
}
