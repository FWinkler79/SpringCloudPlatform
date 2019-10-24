package com.equalities.cloud.reservation.service;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * A reservation entity to store in a database and
 * to expose as a REST resource.
 */
@Entity
public class Reservation {
  @Id
  @GeneratedValue
  private Long id;
  private String reservationName;

  public Reservation() {
  }

  public Reservation(String reservationName) {
    this.reservationName = reservationName;
  }

  public Long getId() {
    return id;
  }

  public String getReservationName() {
    return reservationName;
  }

  @Override
  public String toString() {
    return "Reservation [id=" + id + ", reservationName=" + reservationName + "]";
  }
}
