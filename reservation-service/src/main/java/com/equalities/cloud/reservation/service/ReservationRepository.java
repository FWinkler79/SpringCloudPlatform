package com.equalities.cloud.reservation.service;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

/**
 * A declarative way to describe a repository of REST resources that 
 * will be served automatically as a REST endpoint.
 * 
 * The URL to access this repository is (by default) derived from the 
 * REST resource's name, in this case 'Reservation'. 
 * 
 * Hence the URL will be:                http://host:port/reservations
 * You can even sort and page like this: http://host:port/reservations?page=1&size=2&sort=reservationName,asc
 */
@RepositoryRestResource
interface ReservationRepository extends JpaRepository<Reservation, Long> {

  /**
   * Finds a set of {@link Reservation}s by name. 
   * 
   * The method will be exposed under reservations/search/by-name and take
   * a query parameter named "reservationName". As a result, calling...
   *  
   *   http://localhost:2222/reservations/search/by-name?reservationName=Carl
   *   
   * ... will return just the reservation whose name is "Carl".
   * 
   * Note: the actual database query is generated by Spring Data behind the scenes.
   * A convention is used for Spring Data to know what query to create.
   * For example, the "findBy" prefix tells Spring Data to make a "SELECT" query.
   * The "ReservationName" suffix tell Spring Data which property to use for selection, 
   * in this case the Reservation's reservationName property! 
   *  
   * @param name the name of the reservation to look for.
   * @return the set of reservations matching the given name.
   */
  @RestResource(path = "by-name")
  Collection<Reservation> findByReservationName(@Param("reservationName") String name);
}
