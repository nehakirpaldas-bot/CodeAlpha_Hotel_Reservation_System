import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;
import java.util.Random;
import java.util.UUID;

class FileManager {

    private static final String ROOMS_FILE = "rooms.csv";
    private static final String RESERVATIONS_FILE = "reservations.csv";


    public static List<Room> loadRooms() {
        List<Room> rooms = new ArrayList<>();
        File file = new File(ROOMS_FILE);
        if (!file.exists()) {
            return rooms;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(",");
                int roomNumber = Integer.parseInt(p[0]);
                RoomCategory category = RoomCategory.valueOf(p[1]);
                double price = Double.parseDouble(p[2]);
                boolean maintenance = Boolean.parseBoolean(p[3]);
                rooms.add(new Room(roomNumber, category, price, maintenance));
            }
        } catch (IOException e) {
            System.out.println("Warning: could not load rooms.csv (" + e.getMessage() + ")");
        }
        return rooms;
    }

    public static void saveRooms(List<Room> rooms) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ROOMS_FILE))) {
            for (Room r : rooms) {
                bw.write(r.getRoomNumber() + "," + r.getCategory() + "," +
                        r.getPricePerNight() + "," + r.isUnderMaintenance());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Warning: could not save rooms.csv (" + e.getMessage() + ")");
        }
    }


    public static List<Reservation> loadReservations(List<Room> rooms) {
        List<Reservation> reservations = new ArrayList<>();
        File file = new File(RESERVATIONS_FILE);
        if (!file.exists()) {
            return reservations;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(",", -1);
                String reservationId = p[0];
                int roomNumber = Integer.parseInt(p[1]);
                String guestId = p[2];
                String guestName = p[3];
                String guestEmail = p[4];
                String guestPhone = p[5];
                LocalDate checkIn = LocalDate.parse(p[6]);
                LocalDate checkOut = LocalDate.parse(p[7]);
                double amount = Double.parseDouble(p[8]);
                ReservationStatus status = ReservationStatus.valueOf(p[9]);
                boolean paid = Boolean.parseBoolean(p[10]);
                String transactionId = p[11].isEmpty() ? null : p[11];

                Room room = findRoom(rooms, roomNumber);
                if (room == null) continue; // skip orphaned rows

                Guest guest = new Guest(guestId, guestName, guestEmail, guestPhone);
                Reservation reservation = new Reservation(reservationId, room, guest, checkIn, checkOut, amount);
                reservation.setStatus(status);
                if (paid) {
                    reservation.markPaid(transactionId);
                }
                reservations.add(reservation);
            }
        } catch (IOException e) {
            System.out.println("Warning: could not load reservations.csv (" + e.getMessage() + ")");
        }
        return reservations;
    }

    public static void saveReservations(List<Reservation> reservations) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RESERVATIONS_FILE))) {
            for (Reservation r : reservations) {
                String row = String.join(",",
                        r.getReservationId(),
                        String.valueOf(r.getRoom().getRoomNumber()),
                        r.getGuest().getGuestId(),
                        r.getGuest().getName(),
                        r.getGuest().getEmail(),
                        r.getGuest().getPhone(),
                        r.getCheckIn().toString(),
                        r.getCheckOut().toString(),
                        String.valueOf(r.getTotalAmount()),
                        r.getStatus().toString(),
                        String.valueOf(r.isPaid()),
                        r.getTransactionId() == null ? "" : r.getTransactionId()
                );
                bw.write(row);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Warning: could not save reservations.csv (" + e.getMessage() + ")");
        }
    }

    private static Room findRoom(List<Room> rooms, int roomNumber) {
        for (Room r : rooms) {
            if (r.getRoomNumber() == roomNumber) {
                return r;
            }
        }
        return null;
    }
}


 class Guest implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String guestId;
    private final String name;
    private final String email;
    private final String phone;

    public Guest(String guestId, String name, String email, String phone) {
        this.guestId = guestId;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String getGuestId() {
        return guestId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public String toString() {
        return name + " (ID: " + guestId + ", " + email + ", " + phone + ")";
    }
}

 class HotelService {

    private List<Room> rooms;
    private List<Reservation> reservations;
    private int reservationCounter;

    public HotelService() {
        rooms = FileManager.loadRooms();
        if (rooms.isEmpty()) {
            seedDefaultRooms();
            FileManager.saveRooms(rooms);
        }
        reservations = FileManager.loadReservations(rooms);
        reservationCounter = nextCounterFrom(reservations);
    }

    private void seedDefaultRooms() {
        rooms = new ArrayList<>();
        int roomNo = 101;
        for (int i = 0; i < 5; i++) rooms.add(new Room(roomNo++, RoomCategory.STANDARD));
        for (int i = 0; i < 3; i++) rooms.add(new Room(roomNo++, RoomCategory.DELUXE));
        for (int i = 0; i < 2; i++) rooms.add(new Room(roomNo++, RoomCategory.SUITE));
    }

    private int nextCounterFrom(List<Reservation> existing) {
        int max = 0;
        for (Reservation r : existing) {
            try {
                int n = Integer.parseInt(r.getReservationId().replace("RES-", ""));
                if (n > max) max = n;
            } catch (NumberFormatException ignored) {
                // non-standard id, skip
            }
        }
        return max + 1;
    }

    public List<Room> getAllRooms() {
        return rooms;
    }


    public List<Room> searchAvailableRooms(RoomCategory category, LocalDate checkIn, LocalDate checkOut) {
        validateDateRange(checkIn, checkOut);
        List<Room> available = new ArrayList<>();
        for (Room room : rooms) {
            if (room.isUnderMaintenance()) continue;
            if (category != null && room.getCategory() != category) continue;
            if (isRoomAvailable(room, checkIn, checkOut)) {
                available.add(room);
            }
        }
        return available;
    }

    private boolean isRoomAvailable(Room room, LocalDate checkIn, LocalDate checkOut) {
        for (Reservation res : reservations) {
            if (res.getStatus() != ReservationStatus.CONFIRMED) continue;
            if (res.getRoom().getRoomNumber() != room.getRoomNumber()) continue;
            // classic interval overlap test: [checkIn, checkOut) vs [resIn, resOut)
            if (checkIn.isBefore(res.getCheckOut()) && res.getCheckIn().isBefore(checkOut)) {
                return false;
            }
        }
        return true;
    }

    private void validateDateRange(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Check-in and check-out dates are required");
        }
        if (!checkIn.isBefore(checkOut)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }
    }

    public Reservation bookRoom(int roomNumber, Guest guest, LocalDate checkIn, LocalDate checkOut) {
        validateDateRange(checkIn, checkOut);
        Room room = findRoomByNumber(roomNumber);
        if (room == null) {
            throw new IllegalArgumentException("Room #" + roomNumber + " does not exist");
        }
        if (room.isUnderMaintenance()) {
            throw new IllegalStateException("Room #" + roomNumber + " is under maintenance");
        }
        if (!isRoomAvailable(room, checkIn, checkOut)) {
            throw new IllegalStateException("Room #" + roomNumber + " is already booked for those dates");
        }

        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        double totalAmount = nights * room.getPricePerNight();
        String reservationId = String.format("RES-%04d", reservationCounter++);

        Reservation reservation = new Reservation(reservationId, room, guest, checkIn, checkOut, totalAmount);
        reservations.add(reservation);
        FileManager.saveReservations(reservations);
        return reservation;
    }

    public boolean processPaymentForReservation(String reservationId, String cardLast4) {
        Reservation reservation = findReservation(reservationId);
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation " + reservationId + " not found");
        }
        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new IllegalStateException("Reservation is " + reservation.getStatus() + ", cannot pay");
        }
        if (reservation.isPaid()) {
            System.out.println("Reservation is already paid.");
            return true;
        }

        PaymentService.PaymentResult result =
                PaymentService.processPayment(reservation.getTotalAmount(), cardLast4);
        System.out.println(result.message);
        if (result.success) {
            reservation.markPaid(result.transactionId);
            FileManager.saveReservations(reservations);
        }
        return result.success;
    }

    public boolean cancelReservation(String reservationId) {
        Reservation reservation = findReservation(reservationId);
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation " + reservationId + " not found");
        }
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            System.out.println("Reservation is already cancelled.");
            return false;
        }
        reservation.setStatus(ReservationStatus.CANCELLED);
        FileManager.saveReservations(reservations);
        return true;
    }

    public Reservation findReservation(String reservationId) {
        for (Reservation r : reservations) {
            if (r.getReservationId().equalsIgnoreCase(reservationId)) {
                return r;
            }
        }
        return null;
    }

    public List<Reservation> getAllReservations() {
        return reservations;
    }

    private Room findRoomByNumber(int roomNumber) {
        for (Room r : rooms) {
            if (r.getRoomNumber() == roomNumber) {
                return r;
            }
        }
        return null;
    }
}

class PaymentService {

    private static final Random RANDOM = new Random();

    public static class PaymentResult {
        public final boolean success;
        public final String transactionId;
        public final String message;

        public PaymentResult(boolean success, String transactionId, String message) {
            this.success = success;
            this.transactionId = transactionId;
            this.message = message;
        }
    }

    public static PaymentResult processPayment(double amount, String cardLast4) {
        System.out.printf("Contacting payment gateway to charge Rs. %.2f to card ending %s...%n",
                amount, cardLast4);
        try {
            Thread.sleep(400); // simulate network latency
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        boolean success = RANDOM.nextInt(100) < 95;
        if (success) {
            String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            return new PaymentResult(true, transactionId, "Payment approved.");
        } else {
            return new PaymentResult(false, null, "Payment declined by issuing bank. Please try again.");
        }
    }
}


 class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String reservationId;
    private final Room room;
    private final Guest guest;
    private final LocalDate checkIn;
    private final LocalDate checkOut;
    private final double totalAmount;
    private ReservationStatus status;
    private boolean paid;
    private String transactionId;

    public Reservation(String reservationId, Room room, Guest guest,
                       LocalDate checkIn, LocalDate checkOut, double totalAmount) {
        this.reservationId = reservationId;
        this.room = room;
        this.guest = guest;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.totalAmount = totalAmount;
        this.status = ReservationStatus.CONFIRMED;
        this.paid = false;
        this.transactionId = null;
    }

    public String getReservationId() {
        return reservationId;
    }

    public Room getRoom() {
        return room;
    }

    public Guest getGuest() {
        return guest;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public boolean isPaid() {
        return paid;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void markPaid(String transactionId) {
        this.paid = true;
        this.transactionId = transactionId;
    }

    public long getNumberOfNights() {
        return ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    public void printDetails() {
        System.out.println("=========================================");
        System.out.println("Reservation ID : " + reservationId);
        System.out.println("Guest          : " + guest);
        System.out.println("Room           : #" + room.getRoomNumber() + " (" + room.getCategory() + ")");
        System.out.println("Check-in       : " + checkIn);
        System.out.println("Check-out      : " + checkOut);
        System.out.println("Nights         : " + getNumberOfNights());
        System.out.printf("Total Amount   : Rs. %.2f%n", totalAmount);
        System.out.println("Status         : " + status);
        System.out.println("Payment        : " + (paid ? "PAID (txn: " + transactionId + ")" : "NOT PAID"));
        System.out.println("=========================================");
    }
}

class Room implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int roomNumber;
    private final RoomCategory category;
    private double pricePerNight;
    private boolean underMaintenance;

    public Room(int roomNumber, RoomCategory category) {
        this(roomNumber, category, category.getBasePricePerNight(), false);
    }

    public Room(int roomNumber, RoomCategory category, double pricePerNight, boolean underMaintenance) {
        this.roomNumber = roomNumber;
        this.category = category;
        this.pricePerNight = pricePerNight;
        this.underMaintenance = underMaintenance;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public RoomCategory getCategory() {
        return category;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public boolean isUnderMaintenance() {
        return underMaintenance;
    }

    public void setUnderMaintenance(boolean underMaintenance) {
        this.underMaintenance = underMaintenance;
    }

    @Override
    public String toString() {
        return String.format("Room #%d | %-8s | Rs. %.2f/night%s",
                roomNumber, category, pricePerNight,
                underMaintenance ? "  [UNDER MAINTENANCE]" : "");
    }
}
 enum RoomCategory {
    STANDARD(2000.0),
    DELUXE(3500.0),
    SUITE(6000.0);

    private final double basePricePerNight;

    RoomCategory(double basePricePerNight) {
        this.basePricePerNight = basePricePerNight;
    }

    public double getBasePricePerNight() {
        return basePricePerNight;
    }
}

enum ReservationStatus {
    CONFIRMED,
    CANCELLED,
    COMPLETED
}

public class Main {

    private static final HotelService hotelService = new HotelService();
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== Welcome to the Hotel Reservation System ===");
        boolean running = true;
        while (running) {
            printMenu();
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1": searchRooms(); break;
                case "2": bookRoom(); break;
                case "3": cancelReservation(); break;
                case "4": viewBooking(); break;
                case "5": viewAllRooms(); break;
                case "6": viewAllReservations(); break;
                case "7": running = false; break;
                default: System.out.println("Invalid option, please choose 1-7.");
            }
        }
        System.out.println("Thank you for using the Hotel Reservation System. Goodbye!");
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("1. Search Available Rooms");
        System.out.println("2. Book a Room");
        System.out.println("3. Cancel Reservation");
        System.out.println("4. View Booking Details");
        System.out.println("5. View All Rooms");
        System.out.println("6. View All Reservations");
        System.out.println("7. Exit");
        System.out.print("Choose an option: ");
    }

    private static LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            try {
                return LocalDate.parse(s);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            }
        }
    }

    private static void searchRooms() {
        System.out.print("Category (STANDARD / DELUXE / SUITE, or leave blank for any): ");
        String catStr = sc.nextLine().trim().toUpperCase();
        RoomCategory category = null;
        if (!catStr.isEmpty()) {
            try {
                category = RoomCategory.valueOf(catStr);
            } catch (IllegalArgumentException e) {
                System.out.println("Unknown category, searching all categories instead.");
            }
        }

        try {
            LocalDate checkIn = readDate("Check-in date (YYYY-MM-DD): ");
            LocalDate checkOut = readDate("Check-out date (YYYY-MM-DD): ");

            List<Room> available = hotelService.searchAvailableRooms(category, checkIn, checkOut);
            if (available.isEmpty()) {
                System.out.println("No rooms available for those dates/category.");
            } else {
                System.out.println("Available rooms:");
                for (Room r : available) {
                    System.out.println("  " + r);
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void bookRoom() {
        try {
            System.out.print("Room number to book: ");
            int roomNumber = Integer.parseInt(sc.nextLine().trim());

            System.out.print("Guest ID: ");
            String guestId = sc.nextLine().trim();
            System.out.print("Guest name: ");
            String name = sc.nextLine().trim();
            System.out.print("Guest email: ");
            String email = sc.nextLine().trim();
            System.out.print("Guest phone: ");
            String phone = sc.nextLine().trim();

            LocalDate checkIn = readDate("Check-in date (YYYY-MM-DD): ");
            LocalDate checkOut = readDate("Check-out date (YYYY-MM-DD): ");

            Guest guest = new Guest(guestId, name, email, phone);
            Reservation reservation = hotelService.bookRoom(roomNumber, guest, checkIn, checkOut);

            System.out.println("Booking created. Reservation ID: " + reservation.getReservationId());
            System.out.printf("Total amount due: Rs. %.2f%n", reservation.getTotalAmount());

            System.out.print("Pay now? (y/n): ");
            if (sc.nextLine().trim().equalsIgnoreCase("y")) {
                System.out.print("Enter last 4 digits of card: ");
                String cardLast4 = sc.nextLine().trim();
                boolean success = hotelService.processPaymentForReservation(reservation.getReservationId(), cardLast4);
                if (success) {
                    System.out.println("Payment successful. Booking confirmed and paid.");
                } else {
                    System.out.println("Payment failed. The booking is still held; retry payment later using the reservation ID.");
                }
            } else {
                System.out.println("Booking held unpaid. Pay later using reservation ID " + reservation.getReservationId());
            }
        } catch (NumberFormatException e) {
            System.out.println("Room number must be a number.");
        } catch (Exception e) {
            System.out.println("Could not complete booking: " + e.getMessage());
        }
    }

    private static void cancelReservation() {
        System.out.print("Reservation ID to cancel: ");
        String id = sc.nextLine().trim();
        try {
            if (hotelService.cancelReservation(id)) {
                System.out.println("Reservation " + id + " cancelled.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewBooking() {
        System.out.print("Reservation ID: ");
        String id = sc.nextLine().trim();
        Reservation reservation = hotelService.findReservation(id);
        if (reservation == null) {
            System.out.println("No reservation found with ID " + id);
        } else {
            reservation.printDetails();
        }
    }

    private static void viewAllRooms() {
        for (Room r : hotelService.getAllRooms()) {
            System.out.println(r);
        }
    }

    private static void viewAllReservations() {
        List<Reservation> all = hotelService.getAllReservations();
        if (all.isEmpty()) {
            System.out.println("No reservations yet.");
            return;
        }
        for (Reservation r : all) {
            System.out.printf("%s | Room #%d | %s | %s to %s | %s | Paid: %s%n",
                    r.getReservationId(), r.getRoom().getRoomNumber(), r.getGuest().getName(),
                    r.getCheckIn(), r.getCheckOut(), r.getStatus(), r.isPaid());
        }
    }
}


