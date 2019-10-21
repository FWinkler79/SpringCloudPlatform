package com.equalities.reservation.service.client;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateReservationRequest {
  private String reservationName;
}
