package com.quarkus;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class MovieResourceTestIT {

    @InjectMock
    MovieRepository movieRepository;

    @Inject
    MovieResource movieResource;
    private Movie movie;

    @BeforeEach
    void setUp() {
        this.movie = new Movie();

        this.movie.setId(1L);
        this.movie.setTitle("FirstMovie");
        this.movie.setDescription("My-FirstMovie");
        this.movie.setDirector("ME");
        this.movie.setCountry("planet");
    }

    @Test
    void getAllMoviesOK() {
        List<Movie> movies=new ArrayList<>();
        movies.add(this.movie);
        Mockito.when(movieRepository.listAll()).thenReturn(movies);
        Response response = movieResource.getAllMovies();
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
        assertNotNull(response.getEntity());
        List<Movie> entity=(List<Movie>) response.getEntity();
        assertFalse(entity.isEmpty());
        assertEquals("FirstMovie",entity.get(0).getTitle());
        assertEquals(1L,entity.get(0).getId());
        assertEquals("My-FirstMovie",entity.get(0).getDescription());
        assertEquals("ME",entity.get(0).getDirector());
        assertEquals("planet",entity.get(0).getCountry());
    }

    @Test
    void getByIdOK() {

        Mockito.when(movieRepository.findByIdOptional(1L)).
                thenReturn(Optional.of(this.movie));
        Response response = movieResource.getById(1L);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
        assertNotNull(response.getEntity());
        Movie entity=(Movie)response.getEntity();
        assertEquals("FirstMovie",entity.getTitle());
        assertEquals(1L,entity.getId());
        assertEquals("My-FirstMovie",entity.getDescription());
        assertEquals("ME",entity.getDirector());
        assertEquals("planet",entity.getCountry());
    }

    @Test
    void getByIdKO() {
        Mockito.when(movieRepository.findByIdOptional(1L)).
                thenReturn(Optional.empty());
        Response response = movieResource.getById(1L);
        assertNotNull(response);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(),response.getStatus());
        assertNull(response.getEntity());
    }

    @Test
    void getByTitleOK() {

        PanacheQuery<Movie> query = Mockito.mock(PanacheQuery.class);
        Mockito.when(query.page(Mockito.any())).thenReturn(query);
        Mockito.when(query.singleResultOptional()).thenReturn(Optional.of(movie));

        Mockito.when(movieRepository.find("title","FirstMovie")).
                thenReturn(query);

        Response response = movieResource.getByTitle("FirstMovie");
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
        assertNotNull(response.getEntity());
        Movie entity=(Movie)response.getEntity();
        assertEquals("FirstMovie",entity.getTitle());
        assertEquals(1L,entity.getId());
        assertEquals("My-FirstMovie",entity.getDescription());
        assertEquals("ME",entity.getDirector());
        assertEquals("planet",entity.getCountry());
    }

    @Test
    void getByTitleKO() {

        PanacheQuery<Movie> query = Mockito.mock(PanacheQuery.class);
        Mockito.when(query.page(Mockito.any())).thenReturn(query);
        Mockito.when(query.singleResultOptional()).thenReturn(Optional.empty());

        Mockito.when(movieRepository.find("title","FirstMovie")).
                thenReturn(query);

        Response response = movieResource.getByTitle("FirstMovie");
        assertNotNull(response);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(),response.getStatus());
        assertNull(response.getEntity());
    }

    @Test
    void createMovieOK() {

        Mockito.doNothing().when(movieRepository).persist(
                ArgumentMatchers.any(Movie.class)
        );

        Mockito.when(movieRepository.isPersistent(
                ArgumentMatchers.any(Movie.class)
                )).thenReturn(true);

        Movie newMovie = new Movie();
        newMovie.setTitle("SecondMovie");
        newMovie.setDescription("My-SecondMovie");
        newMovie.setDirector("ME");
        newMovie.setCountry("planet");

        Response response = movieResource.createMovie(newMovie);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(),
                response.getStatus());


    }

    @Test
    void createMovieKO() {

        Mockito.doNothing().when(movieRepository).persist(
                ArgumentMatchers.any(Movie.class)
        );
        Mockito.when(movieRepository.isPersistent(
                ArgumentMatchers.any(Movie.class)
        )).thenReturn(false);

        Movie newMovie = new Movie();
        newMovie.setId(1L);
        newMovie.setTitle("SecondMovie");
        newMovie.setDescription("My-SecondMovie");
        newMovie.setDirector("ME");
        newMovie.setCountry("planet");

        Response response = movieResource.createMovie(newMovie);
        assertNotNull(response);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
                response.getStatus());
        assertNull(response.getEntity());
        assertNull(response.getLocation());
    }

    @Test
    void deleteByIdOK() {

        Mockito.when(movieRepository.deleteById(1L)).thenReturn(true);
        Response response = movieResource.deleteById(1L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(),
                response.getStatus());
        assertNull(response.getEntity());
    }

    @Test
    void deleteByIdKO() {

        Mockito.when(movieRepository.deleteById(1L)).thenReturn(false);
        Response response = movieResource.deleteById(1L);
        assertNotNull(response);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
                response.getStatus());
        assertNull(response.getEntity());
    }
}