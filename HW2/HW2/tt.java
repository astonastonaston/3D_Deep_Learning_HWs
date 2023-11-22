import java.time.*;

class Ticket {
    Passenger passenger;
    Flight flight;
    LocalDateTime purchaseTime;
    LocalDateTime cancelDeadline;
    boolean purchasedWithSkyPoints;
    boolean cancelled;

    // TODO: Add row/seat fields
    int row;
    char seat;

    Ticket(Passenger passenger, Flight flight, boolean purchasedWithSkyPoints, int row, char seat) {
        this.passenger = passenger;
        this.flight = flight;
        this.purchasedWithSkyPoints = purchasedWithSkyPoints;

        this.cancelled = false;
        this.purchaseTime = LocalDateTime.now();
        this.cancelDeadline = purchaseTime.plusDays(1);

        // TODO: Initialize row/seat fields.
        this.row = row;
        this.seat = seat;
    }

    boolean cancel() {
        if (this.cancelled || LocalDateTime.now().isAfter(this.cancelDeadline)) {
            return this.cancelled;
        }

        if (this.purchasedWithSkyPoints) {
            this.passenger.skyPoints += this.flight.costInSkyPoints();
        } else {
            this.passenger.cashBalance += this.flight.cost * 0.95;
        }

        // TODO: Remove ticket from seatmap.
        this.flight.isOccupied(this.row, this.seat);

        this.cancelled = true;
        return this.cancelled;
    }

    /**
     * Return info string for ticket
     * <flightNo> (<departureAirport>/<arrivalAirport>) @<row><seat> <lastname>,
     * <firstname> <cancelled ? "[CANCELLED]" : "">
     */
    String getTicketInfo() {
        String cancelledStatus = cancelled ? " [CANCELLED]" : "";
        return flight.flightNo + " (" + flight.departureAirport + "/" + flight.arrivalAirport + ") @"
                + (row + 1) + seat + " " + passenger.lastName + ", " + passenger.firstName + cancelledStatus;
    }
}class Passenger {
    int skyId;
    String firstName;
    String lastName;
    int skyPoints;
    double cashBalance;

    Passenger(int skyId, String firstName, String lastName,
            int skyPoints, double cashBalance) {
        this.skyId = skyId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.skyPoints = skyPoints;
        this.cashBalance = cashBalance;
    }

    Ticket book(Flight flight, int row, char seat) {
        if (flight.isOccupied(row, seat)) {
            return null;
        }

        double skyPointsCost = flight.costInSkyPoints();
        boolean purchasedWithSkyPoints = false;

        if (this.skyPoints >= skyPointsCost) {
            this.skyPoints -= skyPointsCost;
            purchasedWithSkyPoints = true;
        } else if (this.cashBalance > flight.cost) {
            this.cashBalance -= flight.cost;
        } else {
            return null;
        }

        Ticket ticket = new Ticket(this, flight, purchasedWithSkyPoints, row, seat);

        // TODO: Add ticket to flight seatmap.
        flight.bookSeat(row, seat, ticket);

        return ticket;
    }
}import java.time.*;

class Flight {

    String flightNo; /* Flight Number. E.g. "UAL 2247" */
    String departureAirport; /* ICAO code, e.g. "KSAN", "EDDF" */
    String arrivalAirport; /* ICAO code, e.g. "KSAN", "EDDF" */
    String aircraftType; /* ICAO code, e.g. "B738", "A388" */
    double cost; /* cost of the ticket. */
    LocalDateTime departureTime;
    LocalDateTime arrivalTime;
    int capacity; /* seating capacity */

    // TODO: Create 2D array seatmap
    int numRows;
    int seatsPerRow;
    boolean[][] ticketsBooked;

    public Flight(String flightNo, String departureAirport,
            String arrivalAirport, String aircraftType,
            double cost, LocalDateTime departureTime,
            LocalDateTime arrivalTime, int numRows, int seatsPerRow) {
        this.flightNo = flightNo;
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        this.aircraftType = aircraftType;
        this.cost = cost;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;

        // TODO: Calculate capacity
        this.capacity = numRows * seatsPerRow;

        // TODO: Initialize 2D array seat map.
        this.ticketsBooked = new boolean[numRows][seatsPerRow];

    }

    boolean bookSeat(int row, char seat, Ticket ticket) {
        // Check if the seat is valid
        if (row >= 0 && row < numRows && seat >= 'A' && seat < 'A' + seatsPerRow) {
            // Book the seat
            int columnIndex = seat - 'A';
            if (!ticketsBooked[row][columnIndex]) {
                ticketsBooked[row][columnIndex] = true;
                return true; // Seat successfully booked
            }
        }
        return false; 
    }

    double costInSkyPoints() {
        return this.cost * 100;
    }

    boolean isOccupied(int row, char seat) {
        if (row < 0 || row >= numRows || seat < 'A' || seat >= 'A' + seatsPerRow) {
        }return false; // TODO
    }

    int getNumBooked() {
       int numBooked = 0;

        // Traverse the 2D array with a nested for-loop
        for (int row = 0; row < numRows; row++) {
            for (int column = 0; column < seatsPerRow; column++) {
                if (ticketsBooked[row][column] ) {
                    numBooked++;
                }
            }
        }

        return numBooked;
    } // TODO

    boolean isFull() {
        return getNumBooked() >= capacity;
    }

    /**
     * Get Flight Plan Info String
     * "<flightNo>: <departureAirport>/<arrivalAirport> <aircraftType>
     * <getNumBooked()>/<capacity>"
     */
    String getFlightPlanInfo() {
        return flightNo + ": " + departureAirport + "/" + arrivalAirport + " " +
               aircraftType + " " + getNumBooked() + "/" + capacity;
    }
}
import tester.*;

import java.time.LocalDateTime;

class FlightTests {
    /**************************************************************************
     * 
     * Flight class tests
     * 
     *************************************************************************/

    boolean testCostInSkyPoints(Tester t) {
        Flight f = new Flight(
                "CA 987",
                "ZBAA",
                "KLAX",
                "B77W",
                1000.0,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(13),
                30, 10);

        return t.checkExpect(f.costInSkyPoints(), 100000.0);
    }

    boolean testIsOccupied(Tester t) {
        Flight f = new Flight(
                "CA 987",
                "ZBAA",
                "KLAX",
                "B77W",
                1000.0,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(13),
                30, 10);
        Passenger p = new Passenger(1000, "Frodo", "Baggins", 1000, 10000.0);

        // set seat 4D to be occupied
        // because our system is zero-indexed, seat "4D" would be (3, 'D').
        f.ticketsBooked[3][3] = true;
        // f.ticketsBooked[3][3] = new Ticket(p, f, false, 3, 'D');

        return t.checkExpect(f.isOccupied(3, 'D'), true)
                && t.checkExpect(f.isOccupied(4, 'A'), false);

        
    }

    /**
     * Test that Flight.isOccupied returns `true` for all out of bound
     * seat indexes.
     */
       boolean testBookSeat(Tester t) {
        Flight f = new Flight(
                "CA 987",
                "ZBAA",
                "KLAX",
                "B77W",
                1000.0,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(13),
                30, 10);
        Passenger p = new Passenger(1000, "Frodo", "Baggins", 1000, 10000.0);

        // Book a seat
        Ticket ticket = new Ticket(p, f, false, 3, 'D');
        boolean result = f.bookSeat(3, 'D', ticket);

        return t.checkExpect(result, true);
    }

    boolean testIsOccupiedOutOfBounds(Tester t) {
        Flight f = new Flight(
                "CA 987",
                "ZBAA",
                "KLAX",
                "B77W",
                1000.0,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(13),
                30, 10);

        return t.checkExpect(f.isOccupied(100, 'A'), true)
                && t.checkExpect(f.isOccupied(3, 'Z'), true)
                && t.checkExpect(f.isOccupied(-2, 'a'), true)
                && t.checkExpect(f.isOccupied(30, 'A'), true) // valid = 0-29
                && t.checkExpect(f.isOccupied(20, 'K'), true); // valid = 'A'-'J'
    }

    /**
     * Flight.getNumBooked()
     */
    boolean testGetNumBooked(Tester t) {
        Flight f = new Flight(
            "TestFlight",
            "DEP",
            "ARR",
            "TEST",
            100.0,
            LocalDateTime.now(),
            LocalDateTime.now().plusHours(4),
            3,
            4);

        f.ticketsBooked[0][0] = true;
        f.ticketsBooked[1][1] = true;
        f.ticketsBooked[2][2] = true;

        return t.checkExpect(f.getNumBooked(), 3);
    } // TODo

    /**
     * Flight.isFull()
     */
    boolean testIsFull(Tester t) {
        Flight f = new Flight(
            "TestFlight",
            "DEP",
            "ARR",
            "TEST",
            100.0,
            LocalDateTime.now(),
            LocalDateTime.now().plusHours(4),
            2,
            2);

    // Assume all seats are booked
    for (int row = 0; row < f.numRows; row++) {
        for (int column = 0; column < f.seatsPerRow; column++) {
            f.ticketsBooked[row][column] = true;
        }
    }
    return t.checkExpect(f.isFull(), true);
}// TODO


    /**
     * Flight.getFlightPlanInfo()
     */
    boolean testGetFlightPlanInfo(Tester t) {
        Flight f = new Flight(
            "TestFlight",
            "DEP",
            "ARR",
            "TEST",
            100.0,
            LocalDateTime.now(),
            LocalDateTime.now().plusHours(4),
            3,
            4);

    // Assume some seats are booked
    f.ticketsBooked[0][0] = true;
    f.ticketsBooked[1][1] = true;

    // Expect the flight plan info to be formatted correctly
    return t.checkExpect(
            f.getFlightPlanInfo(),
            "TestFlight: DEP/ARR TEST 2/12"
    );// TODO
    }

    /**************************************************************************
     * 
     * Passenger class tests
     * 
     **************************************************************************/

    /**
     * Passenger.book(Flight flight, int row, char seat)
     * when the seat in the parameters is occupied.
     */
    boolean testBookWhenOccupied(Tester t) {
        Flight f = new Flight(
            "TestFlight",
            "DEP",
            "ARR",
            "TEST",
            100.0,
            LocalDateTime.now(),
            LocalDateTime.now().plusHours(4),
            2,
            2);

    // Book a seat in the flight
    Passenger p = new Passenger(1, "John", "Doe", 100, 500.0);
    f.ticketsBooked[0][0] = true;
    Ticket bookedTicket = p.book(f, 0, 'A');

    // Expect the booking to be unsuccessful (seat is already occupied)
    return t.checkExpect(bookedTicket, null); // TODO
    }

    /**
     * Passenger.book(Flight flight, int row, char seat)
     * when the booking is successful.
     */
    boolean testBookSuccess(Tester t) {
        Flight f = new Flight(
            "TestFlight",
            "DEP",
            "ARR",
            "TEST",
            100.0,
            LocalDateTime.now(),
            LocalDateTime.now().plusHours(4),
            2,
            2);

    // Book a seat in the flight
        Passenger p = new Passenger(1, "John", "Doe", 100, 500.0);
        Ticket bookedTicket = p.book(f, 0, 'A');

    // Expect the booking to be successful
        return t.checkExpect(bookedTicket != null && bookedTicket.cancelled == false, true); // TODO
    }

    /**************************************************************************
     * 
     * Ticket class tests
     * 
     **************************************************************************/

    /*
     * Ticket.getTicketInfo()
     */
    boolean testGetTicketInfo(Tester t) {
          Flight f = new Flight(
            "TestFlight",
            "DEP",
            "ARR",
            "TEST",
            100.0,
            LocalDateTime.now(),
            LocalDateTime.now().plusHours(4),
            2,
            2);

    Passenger p = new Passenger(1, "John", "Doe", 100, 500.0);
    Ticket bookedTicket = p.book(f, 0, 'A');

    String expectedInfo = "TestFlight (DEP/ARR) @1A Doe, John";
    return t.checkExpect(bookedTicket.getTicketInfo(), expectedInfo); // TODO
    }

    /**
     * Ticket.cancel()
     */
    boolean testCancel(Tester t) {
        Flight f = new Flight(
            "TestFlight",
            "DEP",
            "ARR",
            "TEST",
            100.0,
            LocalDateTime.now(),
            LocalDateTime.now().plusHours(4),
            2,
            2);

    Passenger p = new Passenger(1, "John", "Doe", 100, 500.0);
    Ticket bookedTicket = p.book(f, 0, 'A');

    boolean isCancelled = bookedTicket.cancel();

    return t.checkExpect(isCancelled, true);// TODO
    }
}