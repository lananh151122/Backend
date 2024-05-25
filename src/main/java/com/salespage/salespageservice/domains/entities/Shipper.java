package com.salespage.salespageservice.domains.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.salespage.salespageservice.domains.config.ObjectIdDeserializer;
import com.salespage.salespageservice.domains.config.ObjectIdSerializer;
import com.salespage.salespageservice.domains.entities.status.ShipperStatus;
import com.salespage.salespageservice.domains.entities.types.VehicleType;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Id;

@Data
@Document("shipper")

public class Shipper extends BaseEntity {

  @Id
  @JsonSerialize(using = ObjectIdSerializer.class)
  @JsonDeserialize(using = ObjectIdDeserializer.class)
  private ObjectId id;

  @Field("username")
  private String username;

  @Field("ship_mode")
  private Boolean shipMode;

  @Field("is_accept_transaction")
  private Boolean acceptTransaction;

  @Field("longitude")
  private String longitude;

  @Field("latitude")
  private String latitude;

  @Field("vehicle")
  private VehicleType vehicleType;

  @Field("status")
  private ShipperStatus status;
}
