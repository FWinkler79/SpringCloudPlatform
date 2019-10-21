package com.equalities.reservation.service.client;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReservationConfirmation {
  
  enum Status {
    BOOKED,
    CANCELLED,
    REJECTED;
  }
  
  private String reservationName;
  private Status status;
}
