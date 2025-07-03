package com.flighttracker.cli;

import com.flighttracker.cli.domain.Aircraft;
import com.flighttracker.cli.domain.Airport;
import com.flighttracker.cli.domain.City;
import com.flighttracker.cli.domain.Passenger;
import com.flighttracker.cli.http.client.RESTClient;

import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class HTTPRestCLIApplication {

    private RESTClient restClient;
    private final Scanner scanner;

    // Constructor for dependency injection (useful for testing)
    public HTTPRestCLIApplication(RESTClient restClient) {
        this.restClient = restClient;
        this.scanner = new Scanner(System.in);
    }

    // Getter for RESTClient (matching example's pattern)
    public RESTClient getRestClient() {
        if (restClient == null) {
            restClient = new RESTClient(); // Lazily initialize if not set (e.g., in main method)
        }
        return restClient;
    }

    // Setter for RESTClient (matching example's pattern, useful for tests)
    public void setRestClient(RESTClient restClient) {
        this.restClient = restClient;
    }

    public static void main(String[] args) {
        // IMPORTANT: Ensure your Spring Boot API is running before starting this CLI!
        // You need to run your Spring Boot app in its own terminal/IDE first:
        // Navigate to your API project directory and run: mvn spring-boot:run

        if (args.length < 1) {
            System.err.println("Usage: java -jar flighttracker-cli-1.0-SNAPSHOT-jar-with-dependencies.jar <API_BASE_URL>");
            System.err.println("Example: java -jar flighttracker-cli-1.0-SNAPSHOT-jar-with-dependencies.jar http://localhost:8080");
            System.exit(1);
        }

        String apiBaseUrl = args[0]; // Get API base URL from command line argument

        HTTPRestCLIApplication cliApp = new HTTPRestCLIApplication(new RESTClient());
        cliApp.getRestClient().setServerURL(apiBaseUrl);

        System.out.println("=========================================");
        System.out.println("  Welcome to Flight Tracker CLI Client!  ");
        System.out.println("=========================================");
        System.out.println("Connecting to API at: " + apiBaseUrl);
        System.out.println("-----------------------------------------");

        cliApp.runInteractiveMenu(); // Start the interactive menu
    }

    /**
     * Runs the main CLI loop, displaying the menu and handling user input.
     */
    public void runInteractiveMenu() {
        int choice;
        do {
            displayMenu();
            System.out.print("Enter your choice: ");
            choice = getUserChoice();

            switch (choice) {
                case 1: getAllCities(); break;
                case 2: getAllAirports(); break;
                case 3: getAllPassengers(); break;
                case 4: getAllAircrafts(); break;
                case 5: getAirportsInCity(); break; // Q1
                case 6: getAircraftsFlownByPassenger(); break; // Q2
                case 7: getAirportsByAircraft(); break; // Q3
                case 8: getAirportsUsedByPassenger(); break; // Q4
                case 0: System.out.println("Exiting Flight Tracker CLI. Goodbye!"); break;
                default: System.out.println("Invalid choice. Please try again.");
            }
            System.out.println("\n-----------------------------------------");
        } while (choice != 0);

        scanner.close(); // Close the scanner when done
    }

    /**
     * Displays the main menu options to the user.
     */
    private void displayMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("1. Get All Cities");
        System.out.println("2. Get All Airports");
        System.out.println("3. Get All Passengers");
        System.out.println("4. Get All Aircrafts");
        System.out.println("--- Assignment Questions ---");
        System.out.println("5. Q1: Get Airports in a City");
        System.out.println("6. Q2: Get Aircrafts flown by a Passenger");
        System.out.println("7. Q3: Get Airports used by an Aircraft");
        System.out.println("8. Q4: Get Airports used by a Passenger");
        System.out.println("0. Exit");
    }

    /**
     * Prompts the user for input and validates it as an integer.
     * @return The user's integer choice.
     */
    private int getUserChoice() {
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.next(); // Consume the invalid input
            System.out.print("Enter your choice: ");
        }
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume the rest of the line (newline character)
        return choice;
    }

    /**
     * Prompts the user for an ID and validates it as a Long.
     * @param prompt The message to display to the user.
     * @return The parsed Long ID, or null if input is invalid.
     */
    private Long getUserIdInput(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        try {
            return Long.parseLong(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID. Please enter a valid number.");
            return null;
        }
    }

    // --- CLI Actions (Calling RESTClient methods) ---

    private void getAllCities() {
        System.out.println("Fetching all cities...");
        List<City> cities = getRestClient().getAllCities();
        if (!cities.isEmpty()) {
            cities.forEach(System.out::println);
        } else {
            System.out.println("No cities found or API error occurred.");
        }
    }

    private void getAllAirports() {
        System.out.println("Fetching all airports...");
        List<Airport> airports = getRestClient().getAllAirports();
        if (!airports.isEmpty()) {
            airports.forEach(System.out::println);
        } else {
            System.out.println("No airports found or API error occurred.");
        }
    }

    private void getAllPassengers() {
        System.out.println("Fetching all passengers...");
        List<Passenger> passengers = getRestClient().getAllPassengers();
        if (!passengers.isEmpty()) {
            passengers.forEach(System.out::println);
        } else {
            System.out.println("No passengers found or API error occurred.");
        }
    }

    private void getAllAircrafts() {
        System.out.println("Fetching all aircrafts...");
        List<Aircraft> aircrafts = getRestClient().getAllAircrafts();
        if (!aircrafts.isEmpty()) {
            aircrafts.forEach(System.out::println);
        } else {
            System.out.println("No aircrafts found or API error occurred.");
        }
    }

    private void getAirportsInCity() {
        Long cityId = getUserIdInput("Enter City ID: ");
        if (cityId != null) {
            System.out.println("Fetching airports in city " + cityId + "...");
            Set<Airport> airports = getRestClient().getAirportsInCity(cityId);
            if (!airports.isEmpty()) {
                airports.forEach(System.out::println);
            } else {
                System.out.println("No airports found for city ID " + cityId + " or API error occurred.");
            }
        }
    }

    private void getAircraftsFlownByPassenger() {
        Long passengerId = getUserIdInput("Enter Passenger ID: ");
        if (passengerId != null) {
            System.out.println("Fetching aircrafts flown by passenger " + passengerId + "...");
            Set<Aircraft> aircrafts = getRestClient().getAircraftsFlownByPassenger(passengerId);
            if (!aircrafts.isEmpty()) {
                aircrafts.forEach(System.out::println);
            } else {
                System.out.println("No aircrafts found for passenger ID " + passengerId + " or API error occurred.");
            }
        }
    }

    private void getAirportsByAircraft() {
        Long aircraftId = getUserIdInput("Enter Aircraft ID: ");
        if (aircraftId != null) {
            System.out.println("Fetching airports used by aircraft " + aircraftId + "...");
            Set<Airport> airports = getRestClient().getAirportsByAircraft(aircraftId);
            if (!airports.isEmpty()) {
                airports.forEach(System.out::println);
            } else {
                System.out.println("No airports found for aircraft ID " + aircraftId + " or API error occurred.");
            }
        }
    }

    private void getAirportsUsedByPassenger() {
        Long passengerId = getUserIdInput("Enter Passenger ID: ");
        if (passengerId != null) {
            System.out.println("Fetching airports used by passenger " + passengerId + "...");
            Set<Airport> airports = getRestClient().getAirportsUsedByPassenger(passengerId);
            if (!airports.isEmpty()) {
                airports.forEach(System.out::println);
            } else {
                System.out.println("No airports found for passenger ID " + passengerId + " or API error occurred.");
            }
        }
    }
}


