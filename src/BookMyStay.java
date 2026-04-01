import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

public class BookMyStay {
    public static void main(String[] args) {
        // UC1
        System.out.println("Welcome to the Hotel Booking Management System");
        System.out.println("System initialized successfully.");

        // UC2
        Room singleRoom = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suiteRoom = new SuiteRoom();

        int singleAvailable = 5;
        int doubleAvailable = 3;
        int suiteAvailable = 2;

        System.out.println("\nHotel Room Initialization");

        System.out.println("\nSingle Room:");
        singleRoom.displayRoomDetails();
        System.out.println("Available: " + singleAvailable);

        System.out.println("\nDouble Room:");
        doubleRoom.displayRoomDetails();
        System.out.println("Available: " + doubleAvailable);

        System.out.println("\nSuite Room:");
        suiteRoom.displayRoomDetails();
        System.out.println("Available: " + suiteAvailable);

        // UC3
        RoomInventory inventory = new RoomInventory();

        System.out.println("\nHotel Room Inventory Status");

        System.out.println("\nSingle Room:");
        singleRoom.displayRoomDetails();
        System.out.println("Available Rooms: " + inventory.getRoomAvailability().get("Single"));

        System.out.println("\nDouble Room:");
        doubleRoom.displayRoomDetails();
        System.out.println("Available Rooms: " + inventory.getRoomAvailability().get("Double"));

        System.out.println("\nSuite Room:");
        suiteRoom.displayRoomDetails();
        System.out.println("Available Rooms: " + inventory.getRoomAvailability().get("Suite"));

        // UC4
        RoomSearchService searchService = new RoomSearchService();

        System.out.println("\nRoom Search");
        searchService.searchAvailableRooms(inventory, singleRoom, doubleRoom, suiteRoom);

        // UC5
        System.out.println("\nBooking Request Queue");

        BookingRequestQueue bookingQueue = new BookingRequestQueue();

        bookingQueue.addRequest(new Reservation("Abhi", "Single"));
        bookingQueue.addRequest(new Reservation("Subha", "Double"));
        bookingQueue.addRequest(new Reservation("Vanmathi", "Suite"));

        while (bookingQueue.hasPendingRequests()) {
            Reservation r = bookingQueue.getNextRequest();
            System.out.println("Processing booking for Guest: " + r.getGuestName() + ", Room Type: " + r.getRoomType());
        }

        // UC6
        BookingRequestQueue bookingQueue2 = new BookingRequestQueue();
        bookingQueue2.addRequest(new Reservation("Abhi", "Single"));
        bookingQueue2.addRequest(new Reservation("Subha", "Single"));
        bookingQueue2.addRequest(new Reservation("Vanmathi", "Suite"));

        RoomAllocationService allocationService = new RoomAllocationService();

        System.out.println("\nRoom Allocation Processing");

        while (bookingQueue2.hasPendingRequests()) {
            Reservation r = bookingQueue2.getNextRequest();
            allocationService.allocateRoom(r, inventory);
        }

        // UC7
        System.out.println("\nAdd-On Service Selection");

        AddOnServiceManager serviceManager = new AddOnServiceManager();

        serviceManager.addService("Single-1", new Service("Breakfast", 500.0));
        serviceManager.addService("Single-1", new Service("Airport Pickup", 1000.0));

        System.out.println("Reservation ID: Single-1");
        System.out.println("Total Add-On Cost: " + serviceManager.calculateTotalServiceCost("Single-1"));

        // UC8
        BookingHistory bookingHistory = new BookingHistory();
        bookingHistory.addReservation(new Reservation("Abhi", "Single"));
        bookingHistory.addReservation(new Reservation("Subha", "Double"));
        bookingHistory.addReservation(new Reservation("Vanmathi", "Suite"));

        BookingReportService reportService = new BookingReportService();

        System.out.println("\nBooking History and Reporting");
        reportService.generateReport(bookingHistory);

        // UC9
        System.out.println("\nBooking Validation");

        Scanner scanner = new Scanner(System.in);
        RoomInventory inventory9 = new RoomInventory();
        ReservationValidator validator = new ReservationValidator();
        BookingRequestQueue bookingQueue9 = new BookingRequestQueue();

        try {
            System.out.print("Enter guest name: ");
            String guestName = scanner.nextLine();

            System.out.print("Enter room type (Single/Double/Suite): ");
            String roomType = scanner.nextLine();

            validator.validate(guestName, roomType, inventory9);
            bookingQueue9.addRequest(new Reservation(guestName, roomType));
            System.out.println("Booking request accepted for Guest: " + guestName + ", Room Type: " + roomType);

        } catch (InvalidBookingException e) {
            System.out.println("Booking failed: " + e.getMessage());
        } finally {
            scanner.close();
        }

        // UC10
        System.out.println("\nBooking Cancellation");

        CancellationService cancellationService = new CancellationService();
        cancellationService.registerBooking("Single-1", "Single");
        cancellationService.cancelBooking("Single-1", inventory);

        System.out.println();
        cancellationService.showRollbackHistory();

        System.out.println();
        System.out.println("Updated Single Room Availability: " + inventory.getRoomAvailability().get("Single"));
    }
}

abstract class Room {
    protected int numberOfBeds;
    protected int squareFeet;
    protected double pricePerNight;

    public Room(int numberOfBeds, int squareFeet, double pricePerNight) {
        this.numberOfBeds = numberOfBeds;
        this.squareFeet = squareFeet;
        this.pricePerNight = pricePerNight;
    }

    public void displayRoomDetails() {
        System.out.println("Beds: " + numberOfBeds);
        System.out.println("Size: " + squareFeet + " sqft");
        System.out.println("Price per night: " + pricePerNight);
    }
}

class SingleRoom extends Room {
    public SingleRoom() { super(1, 250, 1500.0); }
}

class DoubleRoom extends Room {
    public DoubleRoom() { super(2, 400, 2500.0); }
}

class SuiteRoom extends Room {
    public SuiteRoom() { super(3, 750, 5000.0); }
}

class RoomInventory {
    private Map<String, Integer> roomAvailability;

    public RoomInventory() {
        roomAvailability = new HashMap<>();
        initializeInventory();
    }

    private void initializeInventory() {
        roomAvailability.put("Single", 5);
        roomAvailability.put("Double", 3);
        roomAvailability.put("Suite", 2);
    }

    public Map<String, Integer> getRoomAvailability() { return roomAvailability; }

    public void updateAvailability(String roomType, int count) { roomAvailability.put(roomType, count); }
}

class RoomSearchService {
    public void searchAvailableRooms(
            RoomInventory inventory,
            Room singleRoom,
            Room doubleRoom,
            Room suiteRoom) {

        Map<String, Integer> availability = inventory.getRoomAvailability();

        if (availability.get("Single") > 0) {
            System.out.println("\nSingle Room:");
            singleRoom.displayRoomDetails();
            System.out.println("Available: " + availability.get("Single"));
        }

        if (availability.get("Double") > 0) {
            System.out.println("\nDouble Room:");
            doubleRoom.displayRoomDetails();
            System.out.println("Available: " + availability.get("Double"));
        }

        if (availability.get("Suite") > 0) {
            System.out.println("\nSuite Room:");
            suiteRoom.displayRoomDetails();
            System.out.println("Available: " + availability.get("Suite"));
        }
    }
}

class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
}

class BookingRequestQueue {
    private Queue<Reservation> requestQueue;

    public BookingRequestQueue() { requestQueue = new LinkedList<>(); }

    public void addRequest(Reservation reservation) { requestQueue.offer(reservation); }

    public Reservation getNextRequest() { return requestQueue.poll(); }

    public boolean hasPendingRequests() { return !requestQueue.isEmpty(); }
}

class RoomAllocationService {
    private Set<String> allocatedRoomIds;
    private Map<String, Set<String>> assignedRoomsByType;

    public RoomAllocationService() {
        allocatedRoomIds = new HashSet<>();
        assignedRoomsByType = new HashMap<>();
    }

    public void allocateRoom(Reservation reservation, RoomInventory inventory) {
        String roomType = reservation.getRoomType();
        Map<String, Integer> availability = inventory.getRoomAvailability();

        if (availability.getOrDefault(roomType, 0) > 0) {
            String roomId = generateRoomId(roomType);
            allocatedRoomIds.add(roomId);
            assignedRoomsByType.computeIfAbsent(roomType, k -> new HashSet<>()).add(roomId);
            inventory.updateAvailability(roomType, availability.get(roomType) - 1);
            System.out.println("Booking confirmed for Guest: " + reservation.getGuestName() + ", Room ID: " + roomId);
        } else {
            System.out.println("Booking failed for Guest: " + reservation.getGuestName() + ", Room Type: " + roomType + " not available.");
        }
    }

    private String generateRoomId(String roomType) {
        int count = assignedRoomsByType.getOrDefault(roomType, new HashSet<>()).size() + 1;
        return roomType + "-" + count;
    }
}

class Service {
    private String serviceName;
    private double cost;

    public Service(String serviceName, double cost) {
        this.serviceName = serviceName;
        this.cost = cost;
    }

    public String getServiceName() { return serviceName; }
    public double getCost() { return cost; }
}

class AddOnServiceManager {
    private Map<String, List<Service>> servicesByReservation;

    public AddOnServiceManager() { servicesByReservation = new HashMap<>(); }

    public void addService(String reservationId, Service service) {
        servicesByReservation.computeIfAbsent(reservationId, k -> new ArrayList<>()).add(service);
    }

    public double calculateTotalServiceCost(String reservationId) {
        List<Service> services = servicesByReservation.getOrDefault(reservationId, new ArrayList<>());
        double total = 0;
        for (Service s : services) total += s.getCost();
        return total;
    }
}

class BookingHistory {
    private List<Reservation> confirmedReservations;

    public BookingHistory() { confirmedReservations = new ArrayList<>(); }

    public void addReservation(Reservation reservation) { confirmedReservations.add(reservation); }

    public List<Reservation> getConfirmedReservations() { return confirmedReservations; }
}

class BookingReportService {
    public void generateReport(BookingHistory history) {
        System.out.println("\nBooking History Report");
        for (Reservation r : history.getConfirmedReservations()) {
            System.out.println("Guest: " + r.getGuestName() + ", Room Type: " + r.getRoomType());
        }
    }
}

class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) { super(message); }
}

class ReservationValidator {
    public void validate(String guestName, String roomType, RoomInventory inventory) throws InvalidBookingException {
        if (guestName == null || guestName.trim().isEmpty()) {
            throw new InvalidBookingException("Guest name cannot be empty.");
        }

        if (!roomType.equals("Single") && !roomType.equals("Double") && !roomType.equals("Suite")) {
            throw new InvalidBookingException("Invalid room type selected.");
        }

        if (inventory.getRoomAvailability().getOrDefault(roomType, 0) <= 0) {
            throw new InvalidBookingException("No rooms available for type: " + roomType);
        }
    }
}

class CancellationService {
    private Stack<String> releasedRoomIds;
    private Map<String, String> reservationRoomTypeMap;

    public CancellationService() {
        releasedRoomIds = new Stack<>();
        reservationRoomTypeMap = new HashMap<>();
    }

    public void registerBooking(String reservationId, String roomType) {
        reservationRoomTypeMap.put(reservationId, roomType);
    }

    public void cancelBooking(String reservationId, RoomInventory inventory) {
        if (!reservationRoomTypeMap.containsKey(reservationId)) {
            System.out.println("Cancellation failed: Reservation ID " + reservationId + " not found.");
            return;
        }

        String roomType = reservationRoomTypeMap.get(reservationId);
        releasedRoomIds.push(reservationId);
        reservationRoomTypeMap.remove(reservationId);

        int current = inventory.getRoomAvailability().getOrDefault(roomType, 0);
        inventory.updateAvailability(roomType, current + 1);

        System.out.println("Booking cancelled successfully. Inventory restored for room type: " + roomType);
    }

    public void showRollbackHistory() {
        System.out.println("Rollback History (Most Recent First):");
        Stack<String> temp = new Stack<>();
        temp.addAll(releasedRoomIds);
        while (!temp.isEmpty()) {
            System.out.println("Released Reservation ID: " + temp.pop());
        }
    }
}