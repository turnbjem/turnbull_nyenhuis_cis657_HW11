package com.example.cis657_hw4;

import org.joda.time.DateTime;
import org.parceler.Parcel;

@Parcel
public class LocationLookup {

    public String get_key() {
        return _key;
    }

    public void set_key(String _key) {
        this._key = _key;
    }

    public DateTime getCalculationDate() {
        return calculationDate;
    }

    public void setCalculationDate(DateTime calculationDate) {
        this.calculationDate = calculationDate;
    }

    public double getOrigLat() {
        return origLat;
    }

    public void setOrigLat(double origLat) {
        this.origLat = origLat;
    }

    public double getOrigLong() {
        return origLong;
    }

    public void setOrigLong(double origLong) {
        this.origLong = origLong;
    }

    public double getEndLat() {
        return endLat;
    }

    public void setEndLat(double endLat) {
        this.endLat = endLat;
    }

    public double getEndLong() {
        return endLong;
    }

    public void setEndLong(double endLong) {
        this.endLong = endLong;
    }

    String _key;
    DateTime calculationDate ;
    double origLat;
    double origLong;
    double endLat;
    double endLong;

}
