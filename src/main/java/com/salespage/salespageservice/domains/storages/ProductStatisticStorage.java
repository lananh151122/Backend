package com.salespage.salespageservice.domains.storages;

import com.salespage.salespageservice.domains.entities.ProductStatistic;
import com.salespage.salespageservice.domains.utils.DateUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductStatisticStorage extends BaseStorage {
  public void save(ProductStatistic ProductStatistic) {
    productStatisticRepository.save(ProductStatistic);
  }

  public void update(ProductStatistic ProductStatistic) {
    productStatisticRepository.save(ProductStatistic);
  }

  public List<ProductStatistic> findByDailyBetween(LocalDate startDate, LocalDate endDate) {
    return productStatisticRepository.findByDailyBetween(startDate, endDate);
  }

  public List<ProductStatistic> findByDailyBetweenOrderByTotalViewDesc(LocalDate startDate, LocalDate endDate) {
    return productStatisticRepository.findByDailyBetweenOrderByTotalViewDesc(startDate, endDate);
  }

  public ProductStatistic findByDailyAndProductId(LocalDate daily, String productId) {
    return productStatisticRepository.findByDailyAndProductId(daily, productId);
  }

  public List<ProductStatistic> findByProductDetailIdAndDailyBetween(String productId, LocalDate startDate, LocalDate endDate) {
    return productStatisticRepository.findByProductDetailIdAndDailyBetween(productId, startDate, endDate);

  }

  public ProductStatistic findFirstByProductIdAndDailyOrderByTotalViewAsc(String productId) {
    LocalDateTime now = DateUtils.now().toLocalDate().atStartOfDay();
    return productStatisticRepository.findFirstByProductIdAndDailyOrderByTotalViewAsc(productId, now);
  }

  public ProductStatistic findFirstByProductIdAndDailyOrderByTotalViewDesc(String productId) {
    LocalDateTime now = DateUtils.now().toLocalDate().atStartOfDay();
    return productStatisticRepository.findFirstByProductIdAndDailyOrderByTotalViewDesc(productId, now);
  }

  public List<ProductStatistic> findTop12ByOrderByTotalBuyDesc() {
    return productStatisticRepository.findTop12ByOrderByTotalBuyDesc();
  }

  public List<ProductStatistic> findTop12ByProductIdInOrderByTotalBuyDesc(List<String> productIds) {
    return productStatisticRepository.findTop12ByProductIdInOrderByTotalBuyDesc(productIds);

  }

  public List<ProductStatistic> findTopNByOrderByTotalBuyDesc(int size) {
    return productStatisticRepository.findTopNByOrderByTotalBuyDesc(size);

  }

  public List<ProductStatistic> findTop12ByProductIdInOrderByTotalViewDesc(List<String> productIds) {
    return productStatisticRepository.findTop12ByProductIdInOrderByTotalViewDesc(productIds);

  }

  public HashSet<String> findDistinctTop10ProductIdByOrderByTotalViewDesc() {
    return productStatisticRepository.findDistinctTop10ProductIdByOrderByTotalViewDesc().stream().map(ProductStatistic::getProductId).collect(Collectors.toCollection(HashSet::new));

  }

  public List<ProductStatistic> findTop12ByOrderByTotalViewDesc() {
    return productStatisticRepository.findTop12ByOrderByTotalViewDesc();
  }

  public void saveAll(List<ProductStatistic> statistics) {
    productStatisticRepository.saveAll(statistics);
  }

  public List<ProductStatistic> findByProductIdIn(List<String> productIds) {
    return productStatisticRepository.findByProductIdIn(productIds);
  }

  public ProductStatistic findByDailyAndProductDetailId(LocalDate current, String id) {
    return productStatisticRepository.findByDailyAndProductDetailId(current, id);
  }

  public List<ProductStatistic> findByProductIdAndDailyBetweenOrderByTotalViewAsc(String id, LocalDate startDate, LocalDate endDate) {
    return productStatisticRepository.findByProductIdAndDailyBetweenOrderByTotalViewAsc(id, startDate.minusDays(1), endDate.plusDays(1));
  }
}
