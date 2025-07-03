package com.flighttracker.cli.http.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flighttracker.cli.domain.Aircraft;
import com.flighttracker.cli.domain.Airport;
import com.flighttracker.cli.domain.City;
import com.flighttracker.cli.domain.Passenger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class RESTClient {
    private String serverURL;
    private HttpClient httpClient;
    private final ObjectMapper objectMapper;

    // Constructor for dependency injection (useful for testing)
    public RESTClient(HttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    // Default constructor for direct use in the main application
    public RESTClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public String getServerURL() {
        return serverURL;
    }

    public void setServerURL(String serverURL) {
        this.serverURL = serverURL;
    }

    public HttpClient getClient() {
        if (httpClient == null) {
            httpClient = HttpClient.newHttpClient();
        }
        return httpClient;
    }

    private <T> T sendGetRequest(String endpoint, TypeReference<T> typeRef) {
        if (serverURL == null || serverURL.isEmpty()) {
            System.err.println("Error: Server URL is not set in RESTClient.");
            return null;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverURL + endpoint))
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = getClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), typeRef);
            } else {
                System.out.println("Error fetching " + endpoint + ": HTTP Status " + response.statusCode());
                System.out.println("Response Body: " + response.body());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Network/IO Error fetching " + endpoint + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public List<City> getAllCities() {
        List<City> cities = sendGetRequest("/cities", new TypeReference<List<City>>() {});
        return cities != null ? cities : Collections.emptyList();
    }

    public List<Airport> getAllAirports() {
        List<Airport> airports = new ArrayList<>();
        if (serverURL == null || serverURL.isEmpty()) {
            System.err.println("Error: Server URL is not set in RESTClient.");
            return Collections.emptyList();
        }

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(serverURL + "/airports")).build();

        try {
            HttpResponse<String> response = getClient().send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                airports = buildAirportListFromResponse(response.body());
            } else {
                System.out.println("Error Status Code: " + response.statusCode());
                System.out.println("Response Body: " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return airports;
    }

    public List<Airport> buildAirportListFromResponse(String response) throws JsonProcessingException {
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return objectMapper.readValue(response, new TypeReference<List<Airport>>(){});
    }

    public List<Passenger> getAllPassengers() {
        List<Passenger> passengers = sendGetRequest("/passengers", new TypeReference<List<Passenger>>() {});
        return passengers != null ? passengers : Collections.emptyList();
    }

    public List<Aircraft> getAllAircrafts() {
        List<Aircraft> aircrafts = sendGetRequest("/aircrafts", new TypeReference<List<Aircraft>>() {});
        return aircrafts != null ? aircrafts : Collections.emptyList();
    }

    public Set<Airport> getAirportsInCity(Long cityId) {
        Set<Airport> airports = sendGetRequest("/cities/" + cityId + "/airports", new TypeReference<Set<Airport>>() {});
        return airports != null ? airports : Collections.emptySet();
    }

    public Set<Aircraft> getAircraftsFlownByPassenger(Long passengerId) {
        Set<Aircraft> aircrafts = sendGetRequest("/passengers/" + passengerId + "/aircrafts", new TypeReference<Set<Aircraft>>() {});
        return aircrafts != null ? aircrafts : Collections.emptySet();
    }

    public Set<Airport> getAirportsByAircraft(Long aircraftId) {
        Set<Airport> airports = sendGetRequest("/aircrafts/" + aircraftId + "/airports", new TypeReference<Set<Airport>>() {});
        return airports != null ? airports : Collections.emptySet();
    }

    public Set<Airport> getAirportsUsedByPassenger(Long passengerId) {
        Set<Airport> airports = sendGetRequest("/passengers/" + passengerId + "/airportsUsed", new TypeReference<Set<Airport>>() {});
        return airports != null ? airports : Collections.emptySet();
    }
}