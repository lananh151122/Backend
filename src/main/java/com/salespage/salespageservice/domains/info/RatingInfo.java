package com.salespage.salespageservice.domains.info;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingInfo {
  String username;

  String productId;

  Float point;

  String comment;
}
