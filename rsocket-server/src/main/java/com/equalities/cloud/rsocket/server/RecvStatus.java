package com.equalities.cloud.rsocket.server;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class RecvStatus {
  @NonNull private String receivedMessage;
  @NonNull private Boolean receivedStatus;
}
