package com.salespage.salespageservice.domains.services;

import com.salespage.salespageservice.domains.entities.types.StatisticType;
import com.salespage.salespageservice.domains.utils.Helper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TransactionStatisticService extends BaseService {

  public void statisticToday() {
    LocalDate today = LocalDate.now();
    String date = today.getYear() + "-" + today.getMonthValue() + "-" + today.getDayOfMonth();
    statisticUpdate(date, Helper.getStartTimeOfDay(today), Helper.getEndTimeOfDay(today), StatisticType.DAY);
  }

  public void statisticPeriodDate() {
    LocalDate today = LocalDate.now();
    LocalDate periodDay = today.minusDays(1);
    String date = periodDay.getYear() + "-" + periodDay.getMonthValue() + "-" + periodDay.getDayOfMonth();
    statisticUpdate(date, Helper.getStartTimeOfDay(periodDay), Helper.getEndTimeOfDay(periodDay), StatisticType.DAY);
  }

  public void statisticWeek() {
    LocalDate today = LocalDate.now();
    String date = today.getYear() + "-" + today.getMonthValue() + "-" + today.getDayOfMonth();
    statisticUpdate(date, Helper.getStartTimeOfWeek(today), Helper.getEndTimeOfWeek(today), StatisticType.WEEK);
  }

  public void statisticPeriodWeek() {
    LocalDate today = LocalDate.now();
    LocalDate periodWeek = today.minusDays(7);
    String date = periodWeek.getYear() + "-" + periodWeek.getMonthValue() + "-" + periodWeek.getDayOfMonth();
    statisticUpdate(date, Helper.getStartTimeOfWeek(periodWeek), Helper.getEndTimeOfWeek(periodWeek), StatisticType.WEEK);
  }

  public void statisticMonth() {
    LocalDate today = LocalDate.now();
    String date = today.getYear() + "-" + today.getMonthValue();
    statisticUpdate(date, Helper.getStartTimeOfMonth(today), Helper.getEndTimeOfMonth(today), StatisticType.MONTH);
  }

  public void statisticPeriodMonth() {
    LocalDate today = LocalDate.now();
    LocalDate periodMonth = today.minusDays(today.getMonthValue());
    String date = periodMonth.getYear() + "-" + periodMonth.getMonthValue();
    statisticUpdate(date, Helper.getStartTimeOfMonth(periodMonth), Helper.getEndTimeOfMonth(periodMonth), StatisticType.MONTH);
  }

  public void statisticYear() {
    LocalDate today = LocalDate.now();
    String date = String.valueOf(today.getYear());
    statisticUpdate(date, Helper.getStartTimeOfYear(today), Helper.getEndTimeOfYear(today), StatisticType.YEAR);
  }

  public void statisticUpdate(String date, Long startAt, Long endAt, StatisticType statisticType) {
//    List<ProductTransaction> listProductTransactions = productTransactionStorage.findByCreatedAtBetween(startAt, endAt);
//    for (ProductTransaction transaction : listProductTransactions) {
//      TransactionStatistic transactionStatistic = transactionStatisticStorage.findByDateAndProductIdAndStatisticType(date, transaction.getProductDetailId(), statisticType);
//      if (Objects.isNull(transactionStatistic)) transactionStatistic = new TransactionStatistic();
//
//      TotalStatisticResponse total = productTransactionStorage.countByProductId(transaction.getProductDetailId(), startAt, endAt);
//      transactionStatistic.setStatisticType(statisticType);
//      transactionStatistic.setDate(date);
//      transactionStatistic.setUsername(transaction.getSellerUsername());
//      transactionStatistic.setProductId(transaction.getProduct().getId().toHexString());
//      transactionStatistic.setProductDetailId(transaction.getProductDetailId());
//      transactionStatistic.setTotalPrice(total.getTotalPrice());
//      transactionStatistic.setTotalProduct(total.getQuantity());
//      transactionStatistic.setTotalUser(productTransactionStorage.countUserBuy(transaction.getBuyerUsername(), transaction.getProductDetailId()));
//      transactionStatisticStorage.save(transactionStatistic);
//    }
  }


}
