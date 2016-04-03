package com.svw.touchtime;

/**
 * Created by djlee on 4/2/16.
 */
public class CountryStateCityList {
    String Country;
    String State;
    String City;

    // constructors
    public CountryStateCityList() {
        this.Country = "";
        this.State = "";
        this.City = "";
    }

    public CountryStateCityList(CountryStateCityList CountryStateCity) {
        this.Country = CountryStateCity.Country;
        this.State = CountryStateCity.State;
        this.City = CountryStateCity.City;
    }

    // setters
    public void setCountry(String Country) {
        this.Country = Country;
    }
    public void setState(String State) { this.State = State; }
    public void setCity(String City) { this.City = City; }

    // getters
    public String getCountry() {
        return this.Country;
    }
    public String getState() {
        return this.State;
    }
    public String getCity() {
        return this.City;
    }

}
