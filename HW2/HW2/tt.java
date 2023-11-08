然后我的code：
import java.time.LocalDateTime;

class CargoFlight implements Flight {
    String flightNo;
    String departureAirport;
    String arrivalAirport;
    String aircraftType;
    LocalDateTime departureTime;
    LocalDateTime arrivalTime;
    int maxULDs;
    int numULDsBooked;
    int maxWeightLbs;
    int cargoWeightLbs;
    double costPerULD;

    CargoFlight(String flightNo, String departureAirport, String arrivalAirport, String aircraftType,
                 LocalDateTime departureTime, LocalDateTime arrivalTime, int maxULDs, int maxWeightLbs,
                 double costPerULD) {
        this.flightNo = flightNo;
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        this.aircraftType = aircraftType;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.maxULDs = maxULDs;
        this.maxWeightLbs = maxWeightLbs;
        this.costPerULD = costPerULD;
    }

    public String getFlightPlanString() {
        return flightNo + ": " + departureAirport + "/" + arrivalAirport + " " + aircraftType +
                " ULD" + numULDsBooked + "/" + maxULDs + " WT" + cargoWeightLbs + "/" + maxWeightLbs;
    }

    public boolean isFull() {
        return numULDsBooked == maxULDs || cargoWeightLbs == maxWeightLbs;
    }

    public boolean isPassengerFlight() {
        return false;
    }
}

class Shipment implements Booking {
    Client client;
    CargoFlight flight;
    int weightLbs;
    LocalDateTime purchaseTime;
    LocalDateTime cancelDeadline;
    boolean cancelled;

    Shipment(Client client, CargoFlight flight, int weightLbs) {
        this.client = client;
        this.flight = flight;
        this.weightLbs = weightLbs;
        this.purchaseTime = LocalDateTime.now();
        this.cancelDeadline = flight.departureTime.minusDays(3);
        this.cancelled = false;
    }
    public boolean cancel() {
        if (cancelled) {
           return true;  
         }

        if (LocalDateTime.now().isAfter(cancelDeadline)) {
            return false;
        }

        double refundAmount = flight.costPerULD * weightLbs;

        client.cashBalance += refundAmount;  
        flight.numULDsBooked -= 1;          
        flight.cargoWeightLbs -= weightLbs;  
        cancelled = true;                   // Mark the shipment as canceled
        return true;
}
}
class Client implements User {
    int userId;
    String name;
    double cashBalance;

    Client(int userId, String name, double cashBalance) {
        this.userId = userId;
        this.name = name;
        this.cashBalance = cashBalance;
    }

    public String getUserInfoString() {
        return name + " [" + userId + "] Current balance: " + cashBalance;
    }

    public boolean isPassenger() {
        return false;
    }

    public Shipment book(CargoFlight flight, int weightLbs) {
        if (flight.numULDsBooked >= flight.maxULDs || flight.cargoWeightLbs + weightLbs > flight.maxWeightLbs || cashBalance < flight.costPerULD) {
            return null;
        }

        cashBalance -= flight.costPerULD;
        flight.numULDsBooked++;
        flight.cargoWeightLbs += weightLbs;
        return new Shipment(this, flight, weightLbs);
    }
}import java.time.*;

/**
 * IMPORTANT
 * Test cases will be manually reviewed by course staff
 * and are worth 20% of your PA 4 grade.
 * Please make sure you have good coverage.
 */

public class CargoTests {

    public static void main(String[] args) {
        testShipmentCancel();
        testClientBook();
    }

    public static void testShipmentCancel() {
        System.out.println("Testing Shipment cancel method...");

        Shipment shipment1 = createShipment();
        shipment1.cancel();
        if (shipment1.cancel()) {
            System.out.println("Test 1: Shipment already canceled - Pass");
        } else {
            System.out.println("Test 1: Shipment already canceled - Fail");
        }

        Shipment shipment2 = createShipment();
        shipment2.setCancelDeadline(LocalDateTime.now().minusDays(2));
        if (!shipment2.cancel()) {
            System.out.println("Test 2: Shipment canceled past deadline - Pass");
        } else {
            System.out.println("Test 2: Shipment canceled past deadline - Fail");
        }

        Shipment shipment3 = createShipment();
        double initialClientBalance = shipment3.getClient().getCashBalance();
        int initialULDsBooked = shipment3.getFlight().getNumULDsBooked();
        int initialCargoWeight = shipment3.getFlight().getCargoWeightLbs();

        if (shipment3.cancel()) {
            double refundAmount = shipment3.getFlight().getCostPerULD();
            double expectedClientBalance = initialClientBalance + refundAmount;
            int expectedULDsBooked = initialULDsBooked - 1;
            int expectedCargoWeight = initialCargoWeight - shipment3.getWeightLbs();

            if (Math.abs(shipment3.getClient().getCashBalance() - expectedClientBalance) < 1e-6 &&
                shipment3.getFlight().getNumULDsBooked() == expectedULDsBooked &&
                shipment3.getFlight().getCargoWeightLbs() == expectedCargoWeight) {
                System.out.println("Test 3: Normal Shipment Cancellation - Pass");
            } else {
                System.out.println("Test 3: Normal Shipment Cancellation - Fail");
            }
        } else {
            System.out.println("Test 3: Normal Shipment Cancellation - Fail");
        }
    }

    public static void testClientBook() {
        System.out.println("Testing Client book method...");

        CargoFlight flight1 = createCargoFlight(5, 10000, 1000.0);
        flight1.setNumULDsBooked(flight1.getMaxULDs());
        Client client1 = createClient(500.0);
        Shipment shipment1 = client1.book(flight1, 500);

        if (shipment1 == null) {
            System.out.println("Test 1: Booking with no ULDs left - Pass");
        } else {
            System.println("Test 1: Booking with no ULDs left - Fail");
        }

        CargoFlight flight2 = createCargoFlight(5, 10000, 1000.0);
        flight2.setCargoWeightLbs(flight2.getMaxWeightLbs());
        Client client2 = createClient(5000.0);
        Shipment shipment2 = client2.book(flight2, 1500);

        if (shipment2 == null) {
            System.out.println("Test 2: Booking exceeding cargo weight capacity - Pass");
        } else {
            System.out.println("Test 2: Booking exceeding cargo weight capacity - Fail");
        }

        CargoFlight flight3 = createCargoFlight(5, 10000, 1000.0);
        Client client3 = createClient(500.0);
        Shipment shipment3 = client3.book(flight3, 1000);

        if (shipment3 == null) {
            System.out.println("Test 3: Booking with insufficient cash balance - Pass");
        } else {
            System.out.println("Test 3: Booking with insufficient cash balance - Fail");
        }

        CargoFlight flight4 = createCargoFlight(5, 10000, 1000.0);
        Client client4 = createClient(5000.0);
        int initialULDsBooked = flight4.getNumULDsBooked();
        int initialCargoWeight = flight4.getCargoWeightLbs();
        double initialClientBalance = client4.getCashBalance();

        Shipment shipment4 = client4.book(flight4, 500);

        if (shipment4 != null) {
            double costPerULD = flight4.getCostPerULD();
            double expectedClientBalance = initialClientBalance - costPerULD;
            int expectedULDsBooked = initialULDsBooked + 1;
            int expectedCargoWeight = initialCargoWeight + 500;

            if (Math.abs(client4.getCashBalance() - expectedClientBalance) < 1e-6 &&
                flight4.getNumULDsBooked() == expectedULDsBooked &&
                flight4.getCargoWeightLbs() == expectedCargoWeight) {
                System.out.println("Test 4: Successful booking - Pass");
            } else {
                System.out.println("Test 4: Successful booking - Fail");
            }
        } else {
            System.out.println("Test 4: Successful booking - Fail");
        }
    }

    // Helper methods for creating objects
    private static Shipment createShipment() {
        CargoFlight flight = createCargoFlight(5, 10000, 1000.0);
        Client client = createClient(5000.0);
        return new Shipment(client, flight, 500);
    }

    private static CargoFlight createCargoFlight(int maxULDs, int maxWeightLbs, double costPerULD) {
        return new CargoFlight("Flight123", "AirportA", "AirportB", "Aircraft123",
                LocalDateTime.now(), LocalDateTime.now().plusHours(2), maxULDs, maxWeightLbs, costPerULD);
    }

    private static Client createClient(double cashBalance) {
        return new Client(1, "ClientName", cashBalance);
    }
    public void testCancelSuccess() {
    CargoFlight cargoFlight = new CargoFlight("FDX 123", "KSAN", "KMEM", "Boeing 767-300",
            LocalDateTime.now(), LocalDateTime.now().plusHours(2), 10, 100000, 150000, 2000.0);

    Client client = new Client(14358, "SoCal Express, Ltd.", 100000.0);

    Shipment shipment = new Shipment(client, cargoFlight, 100);

    boolean result = shipment.cancel();

    double refundAmount = cargoFlight.getCostPerULD() * 100;
    double expectedClientBalance = 100000.0 + refundAmount;

    assertEquals(expectedClientBalance, client.getCashBalance(), 0.01); // Use an appropriate delta for double comparisons
}

}