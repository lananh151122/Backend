package com.salespage.salespageservice.app.responses.Statistic;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ChartDataResponse {
  List<String> labels = new ArrayList<>();
  List<DataSets> datasets = new ArrayList<>();
  private String productId;
  private String productName;
  private Long totalUser = 0L;
  private Long totalPurchase = 0L;
  private Long totalProduct = 0L;
  private Long totalView = 0L;
  private Long totalBuy = 0L;
  private Long totalShipCod = 0L;

  @Data
  public static class DataSets {
    List<DailyDataResponse> data = new ArrayList<>();
    String label;
    Boolean fill = true;
    Integer borderWidth = 1;

    String borderColor;
  }
}
