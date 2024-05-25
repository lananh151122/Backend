package com.salespage.salespageservice.domains.services;

import com.salespage.salespageservice.ModelMapper;
import com.salespage.salespageservice.domains.entities.types.LogType;
import com.salespage.salespageservice.domains.entities.types.UserRole;
import com.salespage.salespageservice.domains.exceptions.BadRequestException;
import com.salespage.salespageservice.domains.factories.NotificationFactory;
import com.salespage.salespageservice.domains.info.AddressResult;
import com.salespage.salespageservice.domains.info.DistanceMatrixResult;
import com.salespage.salespageservice.domains.info.OpenStreetMapResponse;
import com.salespage.salespageservice.domains.producer.Producer;
import com.salespage.salespageservice.domains.storages.*;
import com.salespage.salespageservice.domains.utils.GoogleDriver;
import com.salespage.salespageservice.domains.utils.JwtUtils;
import com.salespage.salespageservice.domains.utils.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpMethod;

import java.util.List;
import java.util.Objects;

public class BaseService {

  @Autowired
  @Lazy
  protected NotificationFactory notificationFactory;
  @Autowired
  protected GoogleDriver driver;
  @Autowired
  protected ModelMapper modelMapper;
  @Autowired
  protected AccountStorage accountStorage;
  @Autowired
  protected UserStorage userStorage;
  @Autowired
  protected ProductStorage productStorage;
  @Autowired
  protected ProductTransactionStorage productTransactionStorage;
  @Autowired
  protected VoucherStoreStorage voucherStoreStorage;
  @Autowired
  protected VoucherCodeStorage voucherCodeStorage;
  @Autowired
  protected VoucherCodeLimitStorage voucherCodeLimitStorage;
  @Autowired
  protected SellerStoreStorage sellerStoreStorage;
  @Autowired
  protected ProductTypeStorage productTypeStorage;
  @Autowired
  protected UserFavoriteStorage userFavoriteStorage;
  @Autowired
  protected SystemLogStorage systemLogStorage;
  @Autowired
  protected JwtUtils jwtUtils;
  @Autowired
  protected GoogleDriver googleDriver;
  @Autowired
  protected BankTransactionStorage bankTransactionStorage;
  @Autowired
  protected PaymentTransactionStorage paymentTransactionStorage;
  @Autowired
  protected NotificationStorage notificationStorage;
  @Autowired
  protected BankAccountStorage bankAccountStorage;
  @Autowired
  protected TransactionStatisticStorage transactionStatisticStorage;
  @Autowired
  protected CheckInDailyStorage checkInDailyStorage;
  @Autowired
  protected CheckInDailyStatisticStorage checkInDailyStatisticStorage;
  @Autowired
  protected ProductCategoryStorage productCategoryStorage;
  @Autowired
  protected TpBankTransactionStorage tpBankTransactionStorage;
  @Autowired
  protected StatisticCheckpointStorage statisticCheckpointStorage;
  @Autowired
  protected ProductStatisticStorage productStatisticStorage;
  @Autowired
  protected SearchHistoryStorage searchHistoryStorage;
  @Autowired
  protected ConfigStorage configStorage;
  @Autowired
  protected ShipperStorage shipperStorage;
  @Autowired
  protected RatingStorage ratingStorage;
  @Autowired
  protected Producer producer;
  @Autowired
  protected OtpStorage otpStorage;
  @Autowired
  protected CartStorage cartStorage;
  @Autowired
  protected ProductDetailStorage productDetailStorage;
  @Autowired
  protected ProductComboStorage productComboStorage;
  @Autowired
  protected ProductComboDetailStorage productComboDetailStorage;
  @Autowired
  protected ProductTransactionDetailStorage productTransactionDetailStorage;
  @Autowired
  protected MongoTemplate mongoTemplate;
  @Value("${goong.url}")
  private String goongUrl;
  @Value("${goong.api-key}")
  private String goongApiKey;
  @Autowired
  @Lazy
  private SystemLogService systemLogService;

  protected void writeLog(String message, String trace, LogType logType, String username) {
    systemLogService.createSystemLog(username, message, trace, logType);
  }

  protected boolean hasUserRole(List<UserRole> roles, UserRole role) {
    return roles.contains(role);
  }

  public OpenStreetMapResponse getOpenStreetMap(String lat, String lon, String address) {
    StringBuilder url = new StringBuilder("https://nominatim.openstreetmap.org/search.php");
    if (Objects.nonNull(lat) && Objects.nonNull(lon)) {
      url.append("q=").append(lat).append(',').append(lon);
    } else if (Objects.nonNull(address)) {
      url.append("q=").append(address);
    }
    url.append("?polygon_geojson").append("=1")
        .append("&format").append("=json");
    return RequestUtil.request(HttpMethod.GET, url.toString(), OpenStreetMapResponse.class, null, null);
  }

  public DistanceMatrixResult.Distance getDistance(String shipperLocation, String userLocation, String vehicle) {
    String url = goongUrl + "/DistanceMatrix" +
        "?origins=" + shipperLocation +
        "&destinations=" + userLocation +
        "&vehicle=" + vehicle +
        "&api_key=" + goongApiKey;
    DistanceMatrixResult response = RequestUtil.request(HttpMethod.GET, url, DistanceMatrixResult.class, null, null);
    if (Objects.isNull(response)) {
      throw new BadRequestException("Không thể xác định vị trí");
    }
    return response.getRows().get(0).getElements().get(0).getDistance();
  }

  public AddressResult suggestAddressByAddress(String address) {
    String url = goongUrl + "/Geocode" +
        "?address=" + address +
        "&api_key=" + goongApiKey;
    AddressResult response = RequestUtil.request(HttpMethod.GET, url, AddressResult.class, null, null);
    if (Objects.isNull(response)) {
      throw new BadRequestException("Không thể xác định vị trí");
    }
    return response;
  }
}
