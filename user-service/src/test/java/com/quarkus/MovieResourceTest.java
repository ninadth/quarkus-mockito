package com.quarkus;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.ValidatableResponse;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;


@QuarkusTest
@Tag("integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MovieResourceTest {

    @Test
    @Order(1)
    void getAllMovies() {

        given()
                .when()
                .get("/movies")
                .then()
                .body("movie.size()", equalTo("2"))
                .body("movie.id", hasItems(1, 2))
                .body("movie.title", hasItems("FirstMovie", "SecondMovie"))
                .body("movie.description", hasItems("MY-FirstMovie", "MY-SecondMovie"))
                .body("movie.director", hasItems("ME", "ME"))
                .body("movie.country", hasItems("PLANET", "PLANET"))
                .statusCode(Response.Status.OK.getStatusCode());
    }

//    @Test
//    @Order(1)
//    void getById() {
//    }
//
//    @Test
//    @Order(1)
//    void getByCountry() {
//    }
//
//    @Test
//    @Order(1)
//    void getByTitle() {
//    }
//
//    @Test
//    @Order(2)
//    void createMovie() {
//    }
//
//    @Test
//    @Order(3)
//    void deleteById() {
//    }
}