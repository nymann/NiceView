package dtu.niceview;

import dk.dtu.imm.fastmoney.BankPortType;
import dk.dtu.imm.fastmoney.BankSecureService;
import dk.dtu.imm.fastmoney.CreditCardFaultMessage;
import dk.dtu.imm.fastmoney.types.CreditCardInfoType;
import dk.dtu.imm.fastmoney.types.CreditCardInfoType.ExpirationDate;
import dtu.niceview.model.Hotel;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.WebServiceRef;

/**
 *
 * @author Nymann
 * https://github.com/nymann/NiceView
 */


@WebService(serviceName = "HotelReservationService")
public class HotelReservationService {
    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/fastmoney.imm.dtu.dk_8080/BankSecureService.wsdl")
    
    private BankSecureService service;
    private final BankPortType bankWebService;
    
    static HashMap<Integer, Hotel> hotels = new HashMap<>();
    static HashMap<Integer, List<String>> bookings = new HashMap<>();
    private final DateFormat format;
    
    
    public HotelReservationService() {
        format = new SimpleDateFormat("dd/MM/yyyy");
        bankWebService = new BankSecureService().getBankSecurePort();
        
        // Create some hotel listings and add them to the list.
        Hotel h = new Hotel("Lotus Inn", "90 Emou St., Athens, Attiki, 10554 Grækenland", 
                633094, 558, true);
        
        hotels.put(h.getBookingNumber(), h);
        
        h = new Hotel("Athens Tiare Hotel", "2 Pireos Street, Athens, Attiki, 10431 Grækenland",
                172742, 522, false);
        
        hotels.put(h.getBookingNumber(), h);
    }
    
    /**
     * Web service operation
     * @param city
     * @param arrivalDate
     * @param departureDate
     * @return 
     * @throws java.text.ParseException 
     */
    @WebMethod(operationName = "getHotels")
    public List<Hotel> getHotels(@WebParam(name="city") String city,
            @WebParam(name="arrivalDate") String arrivalDate,
            @WebParam(name="departureDate") String departureDate) throws ParseException {
        
        Date _arrivalDate = format.parse(arrivalDate);
        Date _departureDate = format.parse(departureDate);
        
        List<Hotel> _hotels = new ArrayList<>();
        
        
        /*hotels.values().stream().filter((h) -> (h.getHotelAddress().contains(city))).map((h) -> {
            int priceOfOneNight = h.getPrice();
            h.setPrice(priceOfOneNight * numberOfNightsBetweenDates(_arrivalDate, _departureDate));
            return h;
        }).forEach((h) -> {
            _hotels.add(h);
        });*/

        for (Hotel h : hotels.values()) {
            
            if (h.getHotelAddress().contains(city)) {
                int priceOfOneNight = h.getPriceOfOneNight();
                h.setPriceTotal(priceOfOneNight * numberOfNightsBetweenDates(_arrivalDate, _departureDate));
                _hotels.add(h);                
            }
        }
        
        
        return _hotels;
    }
    
    @WebMethod(operationName = "bookHotel")
    public boolean bookHotel(@WebParam(name = "bookingNumber") int bookingNumber,
            @WebParam(name = "creditCardNumber") String creditCardNumber,
            @WebParam(name = "creditcardName") String creditcardName, 
            @WebParam(name = "expirationMonth") int expirationMonth,
            @WebParam(name = "expirationYear") int expirationYear,
            @WebParam(name = "creditCardGuaranteedRequired") boolean creditCardGuaranteedRequired) throws Exception {
        
        CreditCardInfoType creditCard = createCreditCard(creditcardName, creditCardNumber, expirationMonth, expirationYear);        
        
        if (creditCardGuaranteedRequired) {
            int price = 0;
            
            for (Hotel h : hotels.values()) {
                if (h.getBookingNumber() == bookingNumber) {
                    price = h.getPriceTotal();
                }
            }
            
            if(creditCardValidated(price, creditCard)) {
                // We shouldn't charge the customer, the hotel does that manually.
                /*AccountType account = new AccountType();
                account.setName("NiceView");
                account.setNumber("50208811"); // Arbitary number.
                bankWebService.chargeCreditCard(14, creditCard, (int) price, account);*/
                
                if(bookings.containsKey(bookingNumber)) {
                    bookings.get(bookingNumber).add(creditCardNumber);
                }
                
                else {
                    List<String> l = new ArrayList<>();
                    l.add(creditCardNumber);
                    bookings.put(bookingNumber, l);
                }
            } else {
                throw new Exception("Credit Card declined.");
            }
        }
        
        return true;
    }
    
    @WebMethod(operationName = "cancelHotel")
    public boolean cancelHotel(@WebParam(name="bookingNumber") int bookingNumber) throws Exception {
        if (bookings.containsKey(bookingNumber)) {
            bookings.remove(bookingNumber);
            
            return true;
        }
        
        throw new Exception("Couldn't remove booking.");
    }
    
    // Helper methods.
    
    private int numberOfNightsBetweenDates(Date arrivalDate, Date departureDate) {
        // We want to return the number of nights there is between two dates, hence the - 1.
        // 1 day in milliseconds = 1000*60*60*24 = 86400000
        return (int) (Math.round((departureDate.getTime() - arrivalDate.getTime()) / 86400000)) - 1;
    }
    
    private boolean creditCardValidated(int price, CreditCardInfoType creditCard) {
        try {
            return bankWebService.validateCreditCard(14, creditCard, (int) price);
                    } catch (CreditCardFaultMessage ex) {
            Logger.getLogger(HotelReservationService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }
    
    private CreditCardInfoType createCreditCard(String creditCardName, String creditCardNumber, int expirationMonth, int expirationYear) {
        CreditCardInfoType creditCard = new CreditCardInfoType();
        creditCard.setName(creditCardName);
        creditCard.setNumber(creditCardNumber);
        ExpirationDate expirationDate = new ExpirationDate();
        
        expirationDate.setMonth(expirationMonth);
        expirationDate.setYear(expirationYear);
        creditCard.setExpirationDate(expirationDate);
        
        return creditCard;
    }
    
}
