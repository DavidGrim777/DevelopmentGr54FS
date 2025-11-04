package de.ait.training.controller;

import de.ait.training.model.Car;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class RestApiCarControllerIT {


    @LocalServerPort
    private int port;

    @Autowired
    TestRestTemplate restTemplate;

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    @Test
    @DisplayName("price between 10000 and 30000, 3 cars were found, status OK")
    @Sql(scripts = {"classpath:sql/clear.sql.txt", "classpath:sql/seed_cars.sql.txt"})
    void testPriceBetween10000And30000() throws Exception {
        ResponseEntity<Car[]> response = restTemplate.getForEntity(url("/api/cars/price/between/10000/30000"), Car[].class);
        //assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Car> cars = Arrays.asList(response.getBody());
        assertThat(cars.size()).isEqualTo(3);
        assertThat(cars.get(0).getModel()).isEqualTo("BMW x5");

    }

    @Test
    @DisplayName("price under 16000, 1 car was found, status OK")
    @Sql(scripts = {"classpath:sql/clear.sql.txt", "classpath:sql/seed_cars.sql.txt"})
    void testPriceUnder16000Success() {
        ResponseEntity<Car[]> response = restTemplate.getForEntity(url("/api/cars/price/under/16000"), Car[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Car> cars = Arrays.asList(response.getBody());
        assertThat(cars.size()).isEqualTo(1);
        assertThat(cars.get(0).getModel()).isEqualTo("Audi A4");
    }

    @Test
    @DisplayName("wrong min and max price, 0 cars ware found, status BadRequest")
    void testMinMaxPricesWrongFail() {
        ResponseEntity<Car[]> response = restTemplate.getForEntity(url("/api/cars/price/between/30000/10000"), Car[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Car[] result = response.getBody();
        List<Car> cars = Arrays.asList(response.getBody());
        assertThat(cars.isEmpty()).isEqualTo(true);
    }

    // HOMEWORK 06

    // 1

    @Test
    @DisplayName("returns a list of all cars from the database, 4 cars in database, status OK")
    @Sql(scripts = {"classpath:sql/clear.sql.txt", "classpath:sql/seed_cars.sql.txt"})
    void testReturnsAllCarsFromDatabase() {
        ResponseEntity<Car[]> response = restTemplate.getForEntity(url("/api/cars"), Car[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Car> cars = Arrays.asList(response.getBody());
        assertThat(cars.size()).isEqualTo(4);
        assertThat(cars.get(0).getModel()).isNotNull();

    }

    @Test
    @DisplayName("returns a empty list from the database, 0 cars ware found, status OK")
    @Sql(scripts = {"classpath:sql/clear.sql.txt"})
    void testReturnsEmptyListWhenNoCarsFromDatabase() throws Exception {
        ResponseEntity<Car[]> response = restTemplate.getForEntity(url("/api/cars"), Car[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Car> cars = Arrays.asList(response.getBody());
        assertThat(cars.isEmpty()).isEqualTo(true);
    }

    // 2

    @Test
    @DisplayName("returns filtered cars by red color, case insensitive, 1 car was found, status OK ")
    @Sql(scripts = {"classpath:sql/clear.sql.txt", "classpath:sql/seed_cars.sql.txt"})
    void testReturnsFiltersCarsByRedColor() {
        ResponseEntity<Car[]> response = restTemplate.getForEntity(url("/api/cars/color/red"), Car[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Car> cars = Arrays.asList(response.getBody());
        assertThat(cars.size()).isEqualTo(1);
        assertThat(cars.get(0).getColor()).isEqualTo("red");
        assertThat(cars.get(0).getModel()).isEqualTo("Ferrari");
    }

    @Test
    @DisplayName("returns a empty list from the database, 0 cars ware found by purple color, status NOT_FOUND")
    @Sql(scripts = {"classpath:sql/clear.sql.txt", "classpath:sql/seed_cars.sql.txt"})
    void testReturnsEmptyListWhenColorNotFound() {
        ResponseEntity<Car[]> response = restTemplate.getForEntity(url("/api/cars/color/purple"), Car[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        List<Car> cars = Arrays.asList(response.getBody());
        assertThat(cars.isEmpty()).isEqualTo(true);
    }

    // 3

    @Test
    @DisplayName("returns all cars where the price is between 10,000 and 30,000, 3 cars was found, status OK ")
    @Sql(scripts = {"classpath:sql/clear.sql.txt", "classpath:sql/seed_cars.sql.txt"})
    void testReturnsCarsWhenPriceIsInGivenRange() {
        ResponseEntity<Car[]> response = restTemplate.getForEntity(url("/api/cars/price/between/10000/30000"), Car[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Car> cars = Arrays.asList(response.getBody());
        assertThat(cars.size()).isEqualTo(3);
        assertThat(cars.get(0).getModel()).isEqualTo("BMW x5");
    }

    @Test
    @DisplayName("returns a empty list from the database, no cars between 100/500, status NOT_FOUND")
    @Sql(scripts = {"classpath:sql/clear.sql.txt", "classpath:sql/seed_cars.sql.txt"})
    void testReturnsEmptyListWhenNoCarsInRange() {
        ResponseEntity<Car[]> response = restTemplate.getForEntity(url("/api/cars/price/between/100/500"), Car[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        List<Car> cars = Arrays.asList(response.getBody());
        assertThat(cars.isEmpty()).isEqualTo(true);
    }

    @Test
    @DisplayName("returns a empty list from the database, wrong min and max price, 0 cars found, status BAD_REQUEST")
    void testReturnsMinMaxInvalidRange() {
        ResponseEntity<Car[]> response = restTemplate.getForEntity(url("/api/cars/price/between/30000/10000"), Car[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        List<Car> cars = Arrays.asList(response.getBody());
        assertThat(cars.isEmpty()).isEqualTo(true);
    }

    @Test
    @DisplayName("returns cars including prices equal to min and max, 3 cars found, status OK")
    @Sql(scripts = {"classpath:sql/clear.sql.txt", "classpath:sql/seed_cars.sql.txt"})
    void testPriceBetweenBoundaryIncluded() {
        ResponseEntity<Car[]> response = restTemplate.getForEntity(url("/api/cars/price/between/15000/25000"), Car[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Car> cars = Arrays.asList(response.getBody());
        assertThat(cars.size()).isEqualTo(3);
    }

    // 4

    @Test
    @DisplayName("returns cars with price less or equal to 25000, 3 cars found, status OK")
    @Sql(scripts = {"classpath:sql/clear.sql.txt", "classpath:sql/seed_cars.sql.txt"})
    void testPriceUnderSuccess() {
        ResponseEntity<Car[]> response = restTemplate.getForEntity(url("/api/cars/price/under/25000"), Car[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Car> cars = Arrays.asList(response.getBody());
        assertThat(cars.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("returns empty list when no cars under price, status NOT_FOUND")
    @Sql(scripts = {"classpath:sql/clear.sql.txt", "classpath:sql/seed_cars.sql.txt"})
    void testPriceUnderNoCars() {
        ResponseEntity<Car[]> response = restTemplate.getForEntity(url("/api/cars/price/under/1000"), Car[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        List<Car> cars = Arrays.asList(response.getBody());
        assertThat(cars.isEmpty()).isTrue();
    }

    // 5

    @Test
    @DisplayName("price over 20000, 2 cars found, status OK")
    @Sql(scripts = {"classpath:sql/clear.sql.txt", "classpath:sql/seed_cars.sql.txt"})
    void shouldReturnCarsOverMinPrice() {
        ResponseEntity<Car[]> response = restTemplate.getForEntity(url("/api/cars/price/over/20000"), Car[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Car> cars = Arrays.asList(response.getBody());
        assertThat(cars.size()).isEqualTo(2);
        assertThat(cars.get(0).getColor()).isNotNull();
    }

    @Test
    @DisplayName("price over 1000000, no cars found, status NotFound")
    @Sql(scripts = {"classpath:sql/clear.sql.txt", "classpath:sql/seed_cars.sql.txt"})
    void shouldReturn404WhenNoCarsOverPrice() {
        ResponseEntity<Car[]> response = restTemplate.getForEntity(url("/api/cars/price/over/1000000"), Car[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        List<Car> cars = Arrays.asList(response.getBody());
        assertThat(cars.isEmpty()).isEqualTo(true);
    }

}
