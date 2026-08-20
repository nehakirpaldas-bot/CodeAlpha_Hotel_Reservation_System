 Hotel Reservation System
 
A console-based Hotel Reservation System developed in Java using Object-Oriented Programming (OOP) and CSV File I/O.

The system allows users to search available rooms, make reservations, manage bookings, process simulated payments, and store data in CSV files without using a database.

📌 Project Overview

The Hotel Reservation System is designed to demonstrate practical Java programming concepts through a simple real-world application.



It provides basic hotel management and reservation functionality while maintaining data persistence through local CSV files.

✨ Key Features

🔎 Search available rooms

🏨 View room details and room types

📅 Make a hotel reservation

❌ Cancel reservations

📋 View reservation details

💳 Simulated payment processing

💾 Store data using CSV files

🔄 Update room availability

🧩 Object-Oriented program structure

⚠️ Basic input validation and error handling

🛠️ Technologies Used

Java

Object-Oriented Programming (OOP)

Java Collections

File I/O

CSV File Handling

Exception Handling

Console-Based Interface

🧱 OOP Concepts Demonstrated

This project applies the major principles of Object-Oriented Programming:

Encapsulation

Classes keep their data private and provide controlled access through methods.

Abstraction

Complex operations such as reservation and file handling are separated into dedicated classes.

Inheritance

Common functionality can be shared between related classes where appropriate.

Polymorphism

Methods can behave differently depending on the object or implementation being used.

📂 Project Structure

HotelReservationSystem/
│
├── src/
│   ├── Main.java
│   ├── Room.java
│   ├── Guest.java
│   ├── Reservation.java
│   ├── HotelService.java
│   ├── FileManager.java
│   └── PaymentService.java
│
├── data/
│   ├── rooms.csv
│   └── reservations.csv
│
└── README.md


🏨 Room Types

The system supports different room categories, such as:

Room TypeDescription

Standard

Basic and affordable accommodation

Deluxe

More spacious room with additional facilities

Suite

Premium accommodation with extra space and facilities

💾 Data Storage

This project does not use a database.



Instead, it uses CSV files for persistent storage.



Example:

rooms.csv

stores room-related information.

reservations.csv

stores reservation-related information.



This approach demonstrates how Java can perform File Input/Output (I/O) operations to save and retrieve application data.

🔄 How the System Works

Start Application
       ↓
Display Main Menu
       ↓
Search / View Rooms
       ↓
Select Available Room
       ↓
Enter Guest Information
       ↓
Create Reservation
       ↓
Process Simulated Payment
       ↓
Save Reservation
       ↓
Update Room Availability
       ↓
Reservation Confirmed

▶️ How to Run

. Open the Project

Open the project in a Java-compatible IDE such as:

IntelliJ IDEA
Eclipse
NetBeans
Visual Studio Code

3. Configure Java

Make sure Java/JDK is installed on your computer.
You can check your Java version using:
java -version

4. Run the Application

Run:

Main.java

The application will start in the console.

🖥️ Example Menu

=================================
     HOTEL RESERVATION SYSTEM
=================================

1. View Available Rooms
2. Search Rooms
3. Make Reservation
4. View Reservations
5. Cancel Reservation
6. Exit

Enter your choice:

🔐 Data & Security Note

This is an educational project intended to demonstrate Java programming, OOP, and file handling.



The payment functionality is simulated and does not process real financial transactions.



No sensitive payment information should be entered into the application.

🎯 Learning Objectives

This project helped demonstrate practical understanding of:



Java classes and objects

Encapsulation

Abstraction

Inheritance

Polymorphism

ArrayLists / Collections

File Input/Output

CSV data handling

Exception handling

Modular programming

Separation of responsibilities

Basic software design
🚀 Future Improvements
Possible improvements include:

Add a graphical user interface (GUI)

Add user authentication

Add an administrator dashboard

Add real database support using MySQL

Add date-based room availability

Add email confirmation

Add real payment gateway integration

Add more advanced reporting features

👩‍💻 Author

Neha Kumari



BSCS Student
The Shaikh Ayaz University, Shikarpur

📌 Project Purpose

This project was developed as a practical Java project to strengthen programming, Object-Oriented Programming, and software development skills.

⭐ If you find this project useful, consider giving the repository a star!
