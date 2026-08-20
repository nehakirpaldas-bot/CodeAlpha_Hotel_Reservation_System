 Hotel Reservation System

A console-based Hotel Reservation System built in Java, demonstrating clean OOP design with file-based persistence 
(no external database required). Search rooms, make and cancel bookings, simulate payments, and view reservation details
— all from a simple text menu.

Features
🔍 Search available rooms by category and date range
🛏️ Room categorization — Standard, Deluxe, Suite (each with its own base price)
📅 Book & cancel reservations, with automatic date-overlap checking
💳 Simulated payment gateway — fake transaction IDs, ~95% approval rate to mimic real-world declines
📋 View booking details and full reservation history
💾 File I/O persistence — bookings and room data are saved to CSV files and survive restarts
Tech Stack
Java (11+) — no external libraries or frameworks
File I/O via java.io — CSV-based storage (rooms.csv, reservations.csv)
java.time for date handling
Project Structure
HotelReservationSystem/
├── Main.java              # Console menu / entry point
├── HotelService.java      # Core business logic (search, book, pay, cancel)
├── FileManager.java       # File I/O — reads/writes CSV data
├── PaymentService.java    # Simulated payment gateway
├── Room.java               # Room model
├── RoomCategory.java       # Enum: STANDARD, DELUXE, SUITE
├── Reservation.java        # Reservation model
├── ReservationStatus.java  # Enum: CONFIRMED, CANCELLED, COMPLETED
├── Guest.java               # Guest model
└── README.md
Getting Started
Prerequisites
JDK 11 or later installed (java -version to check)
Run it
bash
git clone https://github.com/<nehakirpaldas-bot>/hotel-reservation-system.git
cd hotel-reservation-system
javac *.java
java Main

On first run, the app seeds 10 default rooms (5 Standard, 3 Deluxe, 2 Suite) into rooms.csv. reservations.csv is created the first time you make a booking. Both files persist between runs.

Usage
1. Search Available Rooms
2. Book a Room
3. Cancel Reservation
4. View Booking Details
5. View All Rooms
6. View All Reservations
7. Exit
Choose an option: 2
Room number to book: 101
Guest ID: G1
Guest name: Neha Kumari
Guest email: n@example.com
Guest phone: 03001234567
Check-in date (YYYY-MM-DD): 2026-08-10
Check-out date (YYYY-MM-DD): 2026-08-12
Booking created. Reservation ID: RES-0001
Total amount due: Rs. 4000.00
Pay now? (y/n): y
Enter last 4 digits of card: 1234
Contacting payment gateway to charge Rs. 4000.00 to card ending 1234...
Payment approved.
Payment successful. Booking confirmed and paid.
Design Notes
Availability is computed, not stored. A Room has no "is available" flag — HotelService checks a requested date range against every CONFIRMED reservation for that room to detect overlaps. This lets the same physical room be booked for different, non-overlapping stays.
Layered architecture:
Main — UI only (console I/O)
HotelService — business rules (availability, booking, cancellation, payment orchestration)
FileManager — persistence, isolated so it can be swapped for a real database (e.g. JDBC/SQLite) without touching business logic
CSV persistence is intentionally simple for readability. Guest names/emails are assumed comma-free; swap in a CSV library or a database for production use.
Possible Extensions
 Swap FileManager for JDBC + SQLite/MySQL
 Add an admin role to add/remove rooms or flag maintenance
 Add refund logic on cancellation of a paid reservation
 Add unit tests (JUnit) for the availability/overlap logic
 Add a simple web or GUI front end (Spring Boot / JavaFX)
