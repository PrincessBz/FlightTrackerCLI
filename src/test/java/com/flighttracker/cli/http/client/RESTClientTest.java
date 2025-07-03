package com.flighttracker.cli.http.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.flighttracker.cli.domain.Aircraft;
import com.flighttracker.cli.domain.Airport;
import com.flighttracker.cli.domain.City;
import com.flighttracker.cli.domain.Passenger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RESTClientTest {
    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockHttpResponse;

    private RESTClient restClient;
    private ObjectMapper objectMapper;

    private City city1;
    private Airport airport1;
    private Airport airport2;
    private Passenger passenger1;
    private Aircraft aircraft1;
    private Aircraft aircraft2;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        restClient = new RESTClient(mockHttpClient, objectMapper);
        restClient.setServerURL("http://localhost:8080");

        // Initialize sample data
        city1 = new City(1L, "New York", "NY", 8000000);
        airport1 = new Airport(101L, "JFK Airport", "JFK");
        airport2 = new Airport(102L, "LaGuardia Airport", "LGA");
        passenger1 = new Passenger(201L, "Alice", "Smith", "555-1234");
        aircraft1 = new Aircraft(301L, "Boeing 747", "United", 400);
        aircraft2 = new Aircraft(302L, "Airbus A320", "Delta", 150);
    }

    // --- Helper for Mocking HTTP Responses ---
    private void mockHttpResponse(int statusCode, String body) throws IOException, InterruptedException {
        when(mockHttpResponse.statusCode()).thenReturn(statusCode);
        when(mockHttpResponse.body()).thenReturn(body);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(mockHttpResponse);
    }

    // --- Test Cases for getAll methods ---

    @Test
    void testGetAllCities_Success() throws IOException, InterruptedException {
        String jsonResponse = objectMapper.writeValueAsString(Arrays.asList(city1, new City(2L, "Los Angeles", "CA", 4000000)));
        mockHttpResponse(200, jsonResponse);

        List<City> cities = restClient.getAllCities();

        assertNotNull(cities);
        assertFalse(cities.isEmpty());
        assertEquals(2, cities.size());
        assertEquals("New York", cities.get(0).getName());
        verify(mockHttpClient, times(1)).send(any(HttpRequest.class), any());
    }

    @Test
    void testGetAllCities_ApiError() throws IOException, InterruptedException {
        mockHttpResponse(500, "Internal Server Error");

        List<City> cities = restClient.getAllCities();

        assertNotNull(cities);
        assertTrue(cities.isEmpty()); // Should return empty list on error
        verify(mockHttpClient, times(1)).send(any(HttpRequest.class), any());
    }

    @Test
    void testGetAllCities_NetworkError() throws IOException, InterruptedException {
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenThrow(new IOException("Network down"));

        List<City> cities = restClient.getAllCities();

        assertNotNull(cities);
        assertTrue(cities.isEmpty());
        verify(mockHttpClient, times(1)).send(any(HttpRequest.class), any());
    }

    @Test
    void testGetAllAirports_Success() throws IOException, InterruptedException {
        String jsonResponse = objectMapper.writeValueAsString(Arrays.asList(airport1, airport2));
        mockHttpResponse(200, jsonResponse);

        List<Airport> airports = restClient.getAllAirports();

        assertNotNull(airports);
        assertFalse(airports.isEmpty());
        assertEquals(2, airports.size());
        assertEquals("JFK Airport", airports.get(0).getName());
        verify(mockHttpClient, times(1)).send(any(HttpRequest.class), any());
    }

    @Test
    void testGetAllPassengers_Success() throws IOException, InterruptedException {
        String jsonResponse = objectMapper.writeValueAsString(Arrays.asList(passenger1, new Passenger(202L, "Bob", "Johnson", "555-5678")));
        mockHttpResponse(200, jsonResponse);

        List<Passenger> passengers = restClient.getAllPassengers();

        assertNotNull(passengers);
        assertFalse(passengers.isEmpty());
        assertEquals(2, passengers.size());
        assertEquals("Alice", passengers.get(0).getFirstName());
        verify(mockHttpClient, times(1)).send(any(HttpRequest.class), any());
    }

    @Test
    void testGetAllAircrafts_Success() throws IOException, InterruptedException {
        String jsonResponse = objectMapper.writeValueAsString(Arrays.asList(aircraft1, aircraft2));
        mockHttpResponse(200, jsonResponse);

        List<Aircraft> aircrafts = restClient.getAllAircrafts();

        assertNotNull(aircrafts);
        assertFalse(aircrafts.isEmpty());
        assertEquals(2, aircrafts.size());
        assertEquals("Boeing 747", aircrafts.get(0).getType());
        verify(mockHttpClient, times(1)).send(any(HttpRequest.class), any());
    }

    // --- Test Cases for Assignment Questions ---

    @Test
    void testGetAirportsInCity_Success() throws IOException, InterruptedException {
        String jsonResponse = objectMapper.writeValueAsString(Set.of(airport1, airport2));
        mockHttpResponse(200, jsonResponse);

        Set<Airport> airports = restClient.getAirportsInCity(city1.getId());

        assertNotNull(airports);
        assertFalse(airports.isEmpty());
        assertEquals(2, airports.size());
        assertTrue(airports.contains(airport1));
        assertTrue(airports.contains(airport2));
        verify(mockHttpClient, times(1)).send(any(HttpRequest.class), any());
    }

    @Test
    void testGetAirportsInCity_NotFound() throws IOException, InterruptedException {
        mockHttpResponse(404, "City not found");

        Set<Airport> airports = restClient.getAirportsInCity(999L);

        assertNotNull(airports);
        assertTrue(airports.isEmpty());
        verify(mockHttpClient, times(1)).send(any(HttpRequest.class), any());
    }

    @Test
    void testGetAircraftsFlownByPassenger_Success() throws IOException, InterruptedException {
        String jsonResponse = objectMapper.writeValueAsString(Set.of(aircraft1, aircraft2));
        mockHttpResponse(200, jsonResponse);

        Set<Aircraft> aircrafts = restClient.getAircraftsFlownByPassenger(passenger1.getId());

        assertNotNull(aircrafts);
        assertFalse(aircrafts.isEmpty());
        assertEquals(2, aircrafts.size());
        assertTrue(aircrafts.contains(aircraft1));
        assertTrue(aircrafts.contains(aircraft2));
        verify(mockHttpClient, times(1)).send(any(HttpRequest.class), any());
    }

    @Test
    void testGetAircraftsFlownByPassenger_NotFound() throws IOException, InterruptedException {
        mockHttpResponse(404, "Passenger not found");

        Set<Aircraft> aircrafts = restClient.getAircraftsFlownByPassenger(999L);

        assertNotNull(aircrafts);
        assertTrue(aircrafts.isEmpty());
        verify(mockHttpClient, times(1)).send(any(HttpRequest.class), any());
    }

    @Test
    void testGetAirportsByAircraft_Success() throws IOException, InterruptedException {
        String jsonResponse = objectMapper.writeValueAsString(Set.of(airport1));
        mockHttpResponse(200, jsonResponse);

        Set<Airport> airports = restClient.getAirportsByAircraft(aircraft1.getId());

        assertNotNull(airports);
        assertFalse(airports.isEmpty());
        assertEquals(1, airports.size());
        assertTrue(airports.contains(airport1));
        verify(mockHttpClient, times(1)).send(any(HttpRequest.class), any());
    }

    @Test
    void testGetAirportsByAircraft_NotFound() throws IOException, InterruptedException {
        mockHttpResponse(404, "Aircraft not found");

        Set<Airport> airports = restClient.getAirportsByAircraft(999L);

        assertNotNull(airports);
        assertTrue(airports.isEmpty());
        verify(mockHttpClient, times(1)).send(any(HttpRequest.class), any());
    }

    @Test
    void testGetAirportsUsedByPassenger_Success() throws IOException, InterruptedException {
        String jsonResponse = objectMapper.writeValueAsString(Set.of(airport1, airport2));
        mockHttpResponse(200, jsonResponse);

        Set<Airport> airports = restClient.getAirportsUsedByPassenger(passenger1.getId());

        assertNotNull(airports);
        assertFalse(airports.isEmpty());
        assertEquals(2, airports.size());
        assertTrue(airports.contains(airport1));
        assertTrue(airports.contains(airport2));
        verify(mockHttpClient, times(1)).send(any(HttpRequest.class), any());
    }

    @Test
    void testGetAirportsUsedByPassenger_NotFound() throws IOException, InterruptedException {
        mockHttpResponse(404, "Passenger not found");

        Set<Airport> airports = restClient.getAirportsUsedByPassenger(999L);

        assertNotNull(airports);
        assertTrue(airports.isEmpty());
        verify(mockHttpClient, times(1)).send(any(HttpRequest.class), any());
    }

    @Test
    void testSetServerURL() {
        RESTClient client = new RESTClient(); // Use default constructor for this test
        assertNull(client.getServerURL());
        client.setServerURL("http://test.com");
        assertEquals("http://test.com", client.getServerURL());
    }

    @Test
    void testGetClient_LazyInitialization() {
        RESTClient client = new RESTClient(); // Use default constructor
        HttpClient initialClient = client.getClient();
        assertNotNull(initialClient);
        // Calling again should return the same instance
        assertEquals(initialClient, client.getClient());
    }
}