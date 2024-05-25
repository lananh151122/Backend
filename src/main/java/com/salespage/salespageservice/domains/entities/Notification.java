package com.salespage.salespageservice.domains.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.salespage.salespageservice.app.responses.notificationResponse.NotificationDetailResponse;
import com.salespage.salespageservice.app.responses.notificationResponse.NotificationResponse;
import com.salespage.salespageservice.domains.config.ObjectIdDeserializer;
import com.salespage.salespageservice.domains.config.ObjectIdSerializer;
import com.salespage.salespageservice.domains.entities.status.NotificationStatus;
import com.salespage.salespageservice.domains.entities.types.NotificationType;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Id;

@Data
@Document("notification")
public class Notification extends BaseEntity {
  @Id
  @JsonSerialize(using = ObjectIdSerializer.class)
  @JsonDeserialize(using = ObjectIdDeserializer.class)
  private ObjectId id;

  @Field("username")
  private String username;

  @Field("title")
  private String title;

  @Field("img_url")
  private String imgUrl;

  @Field("content")
  private String content;

  @Field("notification_status")
  private NotificationStatus notificationStatus;

  @Field("notification_type")
  private NotificationType notificationType;

  @Field("ref_id")
  private String refId;

  public NotificationResponse partnerToNotificationResponse() {
    NotificationResponse response = new NotificationDetailResponse();
    response.setId(id.toHexString());
    response.setTitle(title);
    response.setImgUrl(imgUrl);
    response.setCreatedAt(createdAt);
    response.setStatus(notificationStatus);
    response.setType(notificationType);
    return response;
  }

  public NotificationDetailResponse partnerToNotificationDetailResponse() {
    NotificationDetailResponse response = new NotificationDetailResponse();
    response.setId(id.toHexString());
    response.setTitle(title);
    response.setCreatedAt(createdAt);
    response.setImgUrl(imgUrl);
    response.setStatus(notificationStatus);
    response.setContent(content);
    response.setType(notificationType);
    return response;
  }
}
