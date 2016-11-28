/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dtu.niceview.model;

/**
 *
 * @author Nymann
 */
public class Hotel {
    private String hotelName;
    private String hotelAddress;
    private int bookingNumber;
    private int priceOfOneNight;
    private int priceTotal;

    public int getPriceOfOneNight() {
        return priceOfOneNight;
    }

    public void setPriceOfOneNight(int priceOfOneNight) {
        this.priceOfOneNight = priceOfOneNight;
    }

    public int getPriceTotal() {
        return priceTotal;
    }

    public void setPriceTotal(int priceTotal) {
        this.priceTotal = priceTotal;
    }
    private boolean creditCardGuaranteedRequired;
    
    public Hotel(String hotelName, String hotelAddress, int bookingNumber, int price, boolean creditCardGuaranteedRequired) {
        this.hotelName = hotelName;
        this.hotelAddress = hotelAddress;
        this.bookingNumber = bookingNumber;
        this.priceOfOneNight = price;
        this.priceTotal = 0;
        this.creditCardGuaranteedRequired = creditCardGuaranteedRequired;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public String getHotelAddress() {
        return hotelAddress;
    }

    public void setHotelAddress(String hotelAddress) {
        this.hotelAddress = hotelAddress;
    }

    public int getBookingNumber() {
        return bookingNumber;
    }

    public void setBookingNumber(int bookingNumber) {
        this.bookingNumber = bookingNumber;
    }

    public boolean isCreditCardGuaranteedRequired() {
        return creditCardGuaranteedRequired;
    }

    public void setCreditCardGuaranteedRequired(boolean creditCardGuaranteedRequired) {
        this.creditCardGuaranteedRequired = creditCardGuaranteedRequired;
    }   
}
