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
   * Note: The name of the method will be used to derive the property 
   *       the generated finder code uses. In this case it will be 'reservationName'.
   * 
   * @param name the name of the reservation to look for.
   * @return the set of reservations matching the given name.
   */
  @RestResource(path = "by-name")
  Collection<Reservation> findReservationByReservationName(@Param("reservationName") String name);
}
