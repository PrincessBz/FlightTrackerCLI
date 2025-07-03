FLIGHT TRACKER CLI CLIENT

This repository contains a standalone Java Command Line Interface (CLI) application
designed to interact with the Flight Tracker REST API. It serves as the frontend client to query
and display data related to cities, airports, passengers, and aircrafts.
Table of Contents
●
Project Purpose
●
Requirements
●
Local Setup and Installation
●
Usage
●
API Dependency
●
Testing
●
GitHub Actions (CI)
●
Project Structure

- Project Purpose

The flighttracker-cli is a console-based application that acts as a client for the Flight Tracker
REST API. Its primary functions include:
●
Retrieving lists of all cities, airports, passengers, and aircrafts.
●
Answering specific assignment questions by querying the API for:
1. Airports in a given city.
2. Aircrafts a specific passenger has flown on.
3. Airports an aircraft takes off from and lands at.
4. Airports a specific passenger has used.
This CLI demonstrates the consumption of a RESTful API from a separate Java application.

- Requirements

To build and run this CLI application, you will need:
●
Java Development Kit (JDK) 17 or higher (matching the pom.xml configuration).
●
Apache Maven 3.6.0 or higher.
●
The Flight Tracker REST API running locally (typically on http://localhost:8080). This
CLI is a client and requires the API backend to be operational.

- Local Setup and Installation
  
Follow these steps to get the CLI client running on your local machine:
1. Clone the Repository:
git clone https://github.com/PrincessBz/FlightTrackerCLI.git
cd flighttracker-cli
2. Build the Project:
Navigate to the root directory of the cloned flighttracker-cli project and build it using
Maven. This will compile the code, run unit tests, and package the application into an
executable JAR.
mvn clean install

- Usage
  
This CLI application is designed to be run from your terminal, taking the API's base URL as a
command-line argument.
1. Start the Flight Tracker REST API:
Before running the CLI, ensure your Spring Boot API backend is running. Open a
separate terminal, navigate to your API project's root directory, and start it:
cd /path/to/your/flighttracker-api-project
mvn spring-boot:run
Keep this terminal open and the API running.
2. Run the CLI Application:
Open a new terminal, navigate to the flighttracker-cli project's root directory, and
execute the generated JAR file:
java -jar target/flighttracker-cli-1.0-SNAPSHOT-jar-with-dependencies.jar
http://localhost:8080/api
○
Note: Adjust 1.0-SNAPSHOT if your pom.xml version differs.
○
Note: Ensure http://localhost:8080/api is the correct base URL for your API's
endpoints. If your API endpoints are directly under the root (e.g., /cities), use
http://localhost:8080.
3. Interact with the CLI:
Once launched, the CLI will display a menu. Enter the corresponding number for the
action you wish to perform and press Enter. For queries requiring an ID, you will be
prompted to enter it.
=========================================
Welcome to Flight Tracker CLI Client!
=========================================
Connecting to API at: http://localhost:8080/api
-----------------------------------------
--- Main Menu ---
1. Get All Cities
2. Get All Airports
3. Get All Passengers
4. Get All Aircrafts
--- Assignment Questions ---
5. Q1: Get Airports in a City
6. Q2: Get Aircrafts flown by a Passenger
7. Q3: Get Airports used by an Aircraft
8. Q4: Get Airports used by a Passenger
0. Exit
Enter your choice:

- API Dependency

This CLI client is entirely dependent on the Flight Tracker REST API. It makes HTTP GET
requests to the API endpoints to retrieve data. Ensure the API is running and accessible at the
specified URL (http://localhost:8080/api by default).

- Testing
  
The project includes comprehensive unit tests for the RESTClient class using JUnit 5 and
Mockito. These tests ensure the client can correctly handle API responses (success, errors)
and network issues without making actual network calls.
To run the tests:
mvn test
Or, as part of the full build:
mvn clean install

- GitHub Actions (CI)
  
This repository is configured with a GitHub Actions workflow (.github/workflows/maven.yml) to
enable Continuous Integration (CI).
●
Any push to the main branch or pull
request targeting main will automatically trigger a
build and run all unit tests.
●
You can view the status of these automated checks under the "Actions" tab of this
GitHub repository.

- Project Structure
  
flighttracker-cli/
├── pom.xml ├── src/
│ ├── main/
│ │ └── java/
│ │ └── com/
│ │ └── flighttracker/
# Maven Project Object Model
│ │ └── cli/
│ │ ├── model/ # POJOs for API data (City, Airport, etc.)
│ │ ├── http/ # HTTP-related client code
│ │ │ └── client/
│ │ │ └── RESTClient.java # Handles API requests and JSON parsing
│ │ └── HTTPRestCLIApplication.java # Main CLI application class
│ └── test/
│ └── java/
│ └── com/
│ └── flighttracker/
│ └── cli/
│ └── http/
│ └── client/
│ └── RESTClientTest.java # Unit tests for RESTClient
└──
.github/
└── workflows/
└── maven.yml # GitHub Action CI workflow

By Princess.
