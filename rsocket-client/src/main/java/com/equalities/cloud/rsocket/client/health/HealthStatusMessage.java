package com.equalities.cloud.rsocket.client.health;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class HealthStatusMessage {
  @NonNull private String status;
}
