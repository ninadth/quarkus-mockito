package com.quarkus;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;


@Path("/movies")
//@Produces(MediaType.APPLICATION_JSON)
//@Consumes(MediaType.APPLICATION_JSON)
public class MovieResource {

    @Inject
    MovieRepository movieRepository;

    @GET
    public Response getAllMovies(){
        List<Movie> movies = movieRepository.listAll();
        return Response.ok(movies).build();
    }

    @GET
    @Path("movie/{id}")
    public Response getById(
           @PathParam("id") Long id){
        return movieRepository.findByIdOptional(id)
                .map(movie -> Response.ok(movie).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("movies/country/{country}")
    public Response getByCountry(@PathParam("country") String country) {
        List<Movie> movies = movieRepository.list("SELECT m FROM Movie m WHERE m.country = ?1 ORDER BY m.id ASC", country);
        return Response.ok(movies).build();
    }

    @GET
    @Path("movies/{title}")
    public Response getByTitle(
            @PathParam("title") String title){
        return movieRepository.find("title",title)
                .singleResultOptional()
                .map(movie -> Response.ok(movie).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @Transactional
    public Response createMovie(Movie movie){
        movieRepository.persist(movie);
        if(movieRepository.isPersistent(movie)){
            return Response.created(URI.create("/movies/" +movie.getId())).build();
        }
        return Response.status(BAD_REQUEST).build();
    }

    @DELETE
    @Path("movie/{id}")
    @Transactional
    public Response deleteById(@PathParam("id") Long id){
        boolean delete = movieRepository.deleteById(id);
        if(delete){
            return Response.noContent().build();
        }
        return Response.status(BAD_REQUEST).build();
    }
}
