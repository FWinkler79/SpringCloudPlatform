package com.equalities.cloud.reservation.service;



import static com.equalities.cloud.reservation.service.rsocket.ReservationConfirmation.Status.BOOKED;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.equalities.cloud.reservation.service.rsocket.ReservationConfirmation;

import brave.Span;
import brave.Tracer;

@Controller
public class ReservationServiceEndpoint {
  
  @Autowired
  private Tracer tracer;

  @Autowired
  private TransactionTemplate transactionTemplate;
  
  @Autowired
  private ReservationRepository reservationRepository;
  
  @ResponseBody
  @PostMapping("/reservation/create/{reservationName}")
  public ReservationConfirmation makeReservation(@PathVariable String reservationName) {
    return transactionTemplate.execute(new ReservationTransactionCallback(reservationName));
  }
  
  private class ReservationTransactionCallback implements TransactionCallback<ReservationConfirmation> {

    private String reservationName;
    
    public ReservationTransactionCallback(String reservationName) {
      this.reservationName = reservationName;
    }
    
    @Override
    public ReservationConfirmation doInTransaction(TransactionStatus status) {
      Span span = tracer.nextSpan().name("saving-reservation").start();
      reservationRepository.save(new Reservation(reservationName));
      span.finish();
      return new ReservationConfirmation(reservationName, BOOKED);
    }
  }
}
