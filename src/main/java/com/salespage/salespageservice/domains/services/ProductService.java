package com.salespage.salespageservice.domains.services;

import com.salespage.salespageservice.app.dtos.productDtos.ProductDto;
import com.salespage.salespageservice.app.dtos.productDtos.ProductTypeDetailDto;
import com.salespage.salespageservice.app.dtos.productDtos.ProductTypeDto;
import com.salespage.salespageservice.app.dtos.productDtos.UpdateTypeDetailStatusDto;
import com.salespage.salespageservice.app.responses.AiDataResponse;
import com.salespage.salespageservice.app.responses.PageResponse;
import com.salespage.salespageservice.app.responses.ProductResponse.*;
import com.salespage.salespageservice.app.responses.UploadImageData;
import com.salespage.salespageservice.app.responses.storeResponse.SellerStoreResponse;
import com.salespage.salespageservice.domains.entities.*;
import com.salespage.salespageservice.domains.entities.infor.Rate;
import com.salespage.salespageservice.domains.entities.status.ProductTypeStatus;
import com.salespage.salespageservice.domains.entities.types.FavoriteType;
import com.salespage.salespageservice.domains.entities.types.RatingType;
import com.salespage.salespageservice.domains.entities.types.SearchType;
import com.salespage.salespageservice.domains.entities.types.UserRole;
import com.salespage.salespageservice.domains.exceptions.AuthorizationException;
import com.salespage.salespageservice.domains.exceptions.ResourceNotFoundException;
import com.salespage.salespageservice.domains.utils.DateUtils;
import com.salespage.salespageservice.domains.utils.Helper;
import com.salespage.salespageservice.domains.utils.RequestUtil;
import jodd.util.StringUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Log4j2
public class ProductService extends BaseService {

  @Autowired
  private ProductTransactionService productTransactionService;

  @Autowired
  private SellerStoreService sellerStoreService;

  @Autowired
  private StatisticService statisticService;


  public Product createProduct(String username, ProductDto dto) {
    List<SellerStore> sellerStores = sellerStoreStorage.findSellerStoreByIdIn(dto.getSellerStoreIds());
    if (sellerStores.isEmpty()) throw new ResourceNotFoundException("Không tồn tại cửa hàng này");
    if (sellerStores.stream().noneMatch(store -> store.getOwnerStoreName().equals(username))) {
      throw new AuthorizationException();
    }

    ProductCategory productCategory = productCategoryStorage.findByCreatedByAndId(username, dto.getCategoryId());
    if (Objects.isNull(productCategory)) throw new ResourceNotFoundException("Không tồn tại danh mục này");

    Product product = modelMapper.toProduct(dto);
    product.setCreatedBy(username);
    productStorage.save(product);
    return product;
  }

  public Product updateProduct(String username, String productId, ProductDto dto) {
    Product product = productStorage.findProductById(productId);
    if (Objects.isNull(product)) throw new ResourceNotFoundException("Không tồn tại sản phẩm này hoặc đã bị xóa");
    if (!Objects.equals(product.getCreatedBy(), username))
      throw new AuthorizationException("Bạn không có quyền cập nhật sản phẩm này");

    ProductCategory productCategory = productCategoryStorage.findByCreatedByAndId(username, dto.getCategoryId());
    if (Objects.isNull(productCategory)) throw new ResourceNotFoundException("Không tồn tại danh mục này");

    List<SellerStore> sellerStores = sellerStoreStorage.findSellerStoreByIdIn(dto.getSellerStoreIds());
    if (sellerStores.isEmpty()) throw new ResourceNotFoundException("Không tồn tại cửa hàng này");
    modelMapper.mapToProduct(dto, product);

    dto.setSellerStoreIds(sellerStores.stream().map(k -> k.getId().toHexString()).collect(Collectors.toList()));
    productStorage.save(product);
    return product;
  }

  public Page<Product> getAllProduct(String username, String productId, String productName, Double minPrice, Double maxPrice, String storeName, String sellerStoreUsername, String categoryName, Long lte, Long gte, String productInfoLabel, Boolean isHot, Pageable pageable) {
    Query query = new Query();
    if (StringUtil.isNotBlank(username)) {
      query.addCriteria(Criteria.where("seller_username").is(username));
    }
    if (StringUtil.isNotBlank(productId) && ObjectId.isValid(productId)) {
      query.addCriteria(Criteria.where("_id").is(new ObjectId(productId)));
    }
    if (StringUtil.isNotBlank(productName)) {
      Pattern pattern = Pattern.compile(".*" + productName + ".*", Pattern.CASE_INSENSITIVE);
      query.addCriteria(Criteria.where("product_name").regex(pattern));
    }

    if (minPrice != null && maxPrice != null) {
      List<ProductDetail> productDetails = productDetailStorage.findBySellPriceBetween(minPrice, maxPrice);
      query.addCriteria(Criteria.where("_id").in(productDetails.stream().map(k -> new ObjectId(k.getProductId())).collect(Collectors.toList())));
    } else if (maxPrice != null) {
      List<ProductDetail> productDetails = productDetailStorage.findBySellPriceLessThanEqual(maxPrice);
      query.addCriteria(Criteria.where("_id").in(productDetails.stream().map(k -> new ObjectId(k.getProductId())).collect(Collectors.toList())));
    } else if (minPrice != null) {
      List<ProductDetail> productDetails = productDetailStorage.findBySellPriceGreaterThanEqual(minPrice);
      query.addCriteria(Criteria.where("_id").in(productDetails.stream().map(k -> new ObjectId(k.getProductId())).collect(Collectors.toList())));
    }
    if (Objects.nonNull(lte) && Objects.nonNull(gte)) {
      query.addCriteria(Criteria.where("created_at").lte(gte).andOperator(Criteria.where("created_at").gte(lte)));
    }
    if (StringUtil.isNotBlank(storeName)) {
      List<SellerStore> sellerStores = sellerStoreService.findIdsByStoreName(storeName);
      List<String> storeNames = sellerStores.stream()
          .map(SellerStore::getStoreName)
          .collect(Collectors.toList());
      query.addCriteria(Criteria.where("store_name").in(storeNames));
    }
    if (StringUtils.isNotBlank(categoryName)) {
      List<ProductCategory> productCategories = productCategoryStorage.findByCategoryNameLike(categoryName);
      query.addCriteria(Criteria.where("category_id").in(productCategories.stream().map(k -> k.getId().toHexString()).collect(Collectors.toList())));
    }
    if (StringUtil.isNotBlank(sellerStoreUsername)) {
      List<SellerStore> sellerStores = sellerStoreService.findIdsByOwnerStoreName(sellerStoreUsername);
      List<String> ids = sellerStores.stream()
          .map(s -> s.getId().toHexString())
          .collect(Collectors.toList());
      query.addCriteria(Criteria.where("seller_store_id").in(ids));
    }

    if (StringUtil.isNotBlank(productInfoLabel)) {
      Pattern labelPattern = Pattern.compile(".*" + productInfoLabel + ".*", Pattern.CASE_INSENSITIVE);
      query.addCriteria(Criteria.where("productInfos.label").regex(labelPattern));
    }

    if (isHot != null) {
      query.addCriteria(Criteria.where("isHot").is(isHot));
    }

    return productStorage.findAll(query, pageable);
  }

  public PageResponse<SellerProductResponse> getAllProduct(String productId, String storeId, String storeName, String productName, Pageable pageable) {
    Query query = new Query();
    if (StringUtil.isNotBlank(productId) && ObjectId.isValid(productId)) {
      query.addCriteria(Criteria.where("_id").is(new ObjectId(productId)));
    }
    if (StringUtil.isNotBlank(storeId) && ObjectId.isValid(storeId)) {
      query.addCriteria(Criteria.where("seller_store_ids").in(List.of(storeId)));
    }
    if (StringUtil.isNotBlank(productName)) {
      query.addCriteria(Criteria.where("product_name").is(productName));
    }
    if (StringUtil.isNotBlank(storeName)) {
      List<SellerStore> sellerStores = sellerStoreService.findIdsByStoreName(storeName);
      List<String> storeIds = sellerStores.stream()
          .map(k -> k.getId().toHexString())
          .collect(Collectors.toList());
      query.addCriteria(Criteria.where("seller_store_ids").in(storeIds));

    }
    Page<Product> products = productStorage.findAll(query, pageable);
    List<SellerProductResponse> responses = modelMapper.toListSellerProductResponse(products.getContent());
    for (SellerProductResponse response : responses) {
      ProductCategory productCategory = productCategoryStorage.findById(response.getCategoryId());
      List<SellerStore> sellerStores = sellerStoreStorage.findSellerStoreByIdIn(response.getSellerStoreIds());
      List<SellerStoreResponse> storeResponses = modelMapper.toListSellerStoreResponse(sellerStores);
      response.setStores(storeResponses);
      response.setProductCategory(modelMapper.toProductCategoryResponse(productCategory));
    }
    Page<SellerProductResponse> itemResponses = new PageImpl<>(responses, pageable, products.getTotalElements());
    return PageResponse.createFrom(itemResponses);
  }

  public PageResponse<ProductDataResponse> findProduct(String productId, String productName,
                                                       Double minPrice, Double maxPrice, String storeName,
                                                       String username, String categoryName, String type,
                                                       Boolean isHot, Long lte, Long gte, Pageable pageable) {
    Page<Product> products = getAllProduct(null, productId, productName, minPrice, maxPrice, storeName, username, categoryName, lte, gte, type, isHot, pageable);

    List<ProductDataResponse> responses = toProductDataResponse(products.getContent());

    return PageResponse.createFrom(new PageImpl<>(responses, pageable, products.getTotalElements()));
  }

  private List<ProductDataResponse> toProductDataResponse(List<Product> products) {
    List<ProductDataResponse> responses = products.stream().map(Product::assignToProductDataResponse).collect(Collectors.toList());
    List<String> productIds = products.stream().map(k -> k.getId().toHexString()).collect(Collectors.toList());
    List<ProductStatistic> productStatistics = productStatisticStorage.findByProductIdIn(productIds);
    Map<String, List<ProductStatistic>> mapProductStatistic = productStatistics.stream()
        .collect(Collectors.groupingBy(ProductStatistic::getProductId));
    List<ProductDetail> productDetails = productDetailStorage.findByProductIdIn(productIds);
    Map<String, List<ProductDetail>> mapProductDetail = productDetails.stream()
        .collect(Collectors.groupingBy(ProductDetail::getProductId));
    responses.forEach(product -> {
      List<ProductStatistic> statistics = mapProductStatistic.get(product.getProductId());
      if (statistics == null) {
        statistics = new ArrayList<>();
      }
      List<ProductDetail> details = mapProductDetail.get(product.getProductId());
      if (details == null) {
        details = new ArrayList<>();
      }
      product.assignFromListDetail(details);
      Long totalBuy = statistics.stream()
          .mapToLong(ProductStatistic::getTotalBuy)
          .sum();
      Long totalView = statistics.stream()
          .mapToLong(ProductStatistic::getTotalView)
          .sum();
      product.setTotalSell(totalBuy);
      product.setTotalView(totalView);
    });
    return responses;
  }

  public SellerProductDetailResponse getSellerProductDetail(String productId) {
    Product product = productStorage.findProductById(productId);
    SellerProductDetailResponse response = modelMapper.toSellerProductDetailResponse(product);
    List<SellerStore> sellerStores = sellerStoreStorage.findSellerStoreByIdIn(product.getSellerStoreIds());

    ProductCategory productCategory = productCategoryStorage.findById(product.getCategoryId());
    if (Objects.isNull(productCategory)) throw new ResourceNotFoundException("Không tìm thấy danh mục sản phẩm");

    List<ProductTypeDetail> typeDetails = productTypeStorage.findByProductId(productId);

    response.setTypeDetails(modelMapper.toListTypeDetails(typeDetails));
    response.setStores(modelMapper.toListSellerStoreResponse(sellerStores));
    response.setProductCategory(modelMapper.toProductCategoryResponse(productCategory));

    return response;
  }

  public List<ProductDataResponse> findHotProduct(String username) {
    List<Product> products;
    if (Objects.isNull(username)) {
      products = productStorage.findTop12ByIsHotOrderByUpdatedAtDesc();
    } else {
      List<SearchHistory> searchHistories = searchHistoryStorage.findTop12ByUsernameOrderByCreatedAtDesc(username);
      List<SearchHistory> searchProductHistories = searchHistories.stream().filter(k -> k.getSearchType().equals(SearchType.PRODUCT_NAME)).collect(Collectors.toList());
      List<SearchHistory> searchStoreHistories = searchHistories.stream().filter(k -> k.getSearchType().equals(SearchType.STORE_NAME)).collect(Collectors.toList());
      List<SearchHistory> searchSellerHistories = searchHistories.stream().filter(k -> k.getSearchType().equals(SearchType.SELLER_USERNAME)).collect(Collectors.toList());
      List<ProductStatistic> productStatistics;
      if (searchProductHistories.isEmpty()) {
        products = productStorage.findTop12ByIsHotOrderByUpdatedAtDesc();
      } else {
        List<String> productIds = searchProductHistories.stream().map(SearchHistory::getSearchData).collect(Collectors.toList());
        products = productStorage.findTop12ByIdInAndIsHotOrderByUpdatedAt(productIds);
      }
      if (products.size() < 10) {
        products = productStorage.findTop12ByIsHotOrderByUpdatedAtDesc();
      }
    }

    return toProductDataResponse(products);
  }

  public ProductDetailResponse getProductDetail(String username, String productId) throws Exception {
    Product product = productStorage.findProductById(productId);
    if (Objects.isNull(product)) throw new ResourceNotFoundException("Không tìm thấy sản phẩm");

    ProductDetailResponse response = product.assignToProductDetailResponse();
    List<SellerStore> sellerStores = sellerStoreStorage.findSellerStoreByIdIn(product.getSellerStoreIds());

    response.setStores(sellerStores.stream().map(k -> modelMapper.toSellerStoreResponse(k)).collect(Collectors.toList()));

    //assign from category
    ProductCategory productCategory = productCategoryStorage.findById(product.getCategoryId());
    if (Objects.isNull(productCategory)) throw new ResourceNotFoundException("Không tìm thấy danh mục sản phẩm");
    response.assignFromCategory(productCategory);

    ProductStatistic productStatistic = productStatisticStorage.findFirstByProductIdAndDailyOrderByTotalViewDesc(productId);
    if(productStatistic != null){
      response.setTotalView(productStatistic.getTotalView());
      response.setTotalSell(productStatistic.getTotalPurchase());
    }

    List<ProductDetail> productDetails = productDetailStorage.findByProductId(productId);
    List<ProductDetailInfoResponse> productDetailResponses = modelMapper.toListProductDetailInfo(productDetails);
    response.setProductDetails(productDetailResponses);
    return response;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public Boolean deleteProduct(String username, String productId) throws IOException {

    Product product = productStorage.findProductById(productId);

    if (!username.equals(product.getCreatedBy()))
      throw new ResourceNotFoundException("Bạn không có sản phẩm này");

//    productTransactionService.productTransactionCancel(productId);
    productStorage.delete(productId);
    googleDriver.deleteFolderByName(productId);
    return true;
  }

  public UploadImageData uploadProductImage(String username, String productId, MultipartFile file) throws IOException {
    List<UploadImageData> imageUrls = new ArrayList<>();
    Product product = productStorage.findProductById(productId);
    if (product == null) throw new ResourceNotFoundException("Không tòn tại sản phẩm này hoặc đã bị xóa");
    if (!product.getCreatedBy().equals(username))
      throw new AuthorizationException("Không được phép");

    String imageUrl = googleDriver.uploadPublicImageNotDelete("Product-" + productId, file.getName() + System.currentTimeMillis(), Helper.convertMultiPartToFile(file));
    product.getImageUrls().add(imageUrl);
    imageUrls.add(new UploadImageData(Helper.generateRandomString(), Helper.generateRandomString() + ".png", "done", imageUrl, imageUrl));

    product.setDefaultImageUrl(imageUrl);
    productStorage.save(product);
    return imageUrls.get(0);
  }

  public void updateDefaultImage(String username, String productId, String imageUrl) {
    Product product = productStorage.findProductById(productId);
    if (product == null) throw new ResourceNotFoundException("Không tòn tại sản phẩm này hoặc đã bị xóa");
    if (!product.getCreatedBy().equals(username))
      throw new AuthorizationException("Không được phép");

    product.setDefaultImageUrl(imageUrl);
    productStorage.save(product);
  }

  public List<String> deleteProductImages(String username, String productId, String images) {
    Product product = productStorage.findProductById(productId);
    if (!product.getCreatedBy().equals(username))
      throw new AuthorizationException("Bạn không thể xóa ảnh của sản phẩm này");
    String[] listImages = images.split(",");
    List<String> imageUrls = new ArrayList<>();
    for (String imageUrl : listImages) {
      String fileId = Helper.extractFileIdFromUrl(imageUrl);
      googleDriver.deleteFile(fileId);
      imageUrls.add(fileId);
      product.getImageUrls().remove(imageUrl);
    }
    productStorage.save(product);
    return imageUrls;
  }

  public void createProductType(String username, ProductTypeDto dto, List<UserRole> roles) {
    if (!hasUserRole(roles, UserRole.ADMIN) && !hasUserRole(roles, UserRole.OPERATOR))
      throw new AuthorizationException("Bạn không có quyền tạo mới");
    ProductType productType = new ProductType();
    productType.partnerFromDto(dto);
    productType.setCreatedBy(username);
    productType.setUpdatedBy(username);
    productTypeStorage.save(productType);
  }

  public void updateProductType(String username, ProductTypeDto dto, List<UserRole> roles) {
    if (!hasUserRole(roles, UserRole.ADMIN) && !hasUserRole(roles, UserRole.OPERATOR))
      throw new AuthorizationException("Bạn không có quyền tạo mới");
    ProductType productType = productTypeStorage.findByProductType(dto.getProductType());
    if (Objects.isNull(productType)) throw new ResourceNotFoundException("Không tồn tại loại sản phẩm này");
    productType.partnerFromDto(dto);
    productType.setCreatedBy(username);
    productType.setUpdatedBy(username);
    productTypeStorage.save(productType);
  }

  public void createProductTypeDetail(ProductTypeDetailDto dto, String username) {
    ProductType productType = productTypeStorage.findByProductType(dto.getTypeName());
    if (Objects.isNull(productType)) throw new ResourceNotFoundException("Không tồn tại loại sản phẩm này");
    ProductTypeDetail productTypeDetail = new ProductTypeDetail();
    productTypeDetail.partnerFromDto(dto);
    productTypeDetail.setCreatedBy(username);
    productTypeDetail.setUpdatedBy(username);
    productTypeStorage.save(productTypeDetail);
  }

  public void updateProductTypeDetail(ProductTypeDetailDto dto, String productTypeId, String username) {
    ProductType productType = productTypeStorage.findByProductType(dto.getTypeName());
    if (Objects.isNull(productType)) throw new ResourceNotFoundException("Không tồn tại loại sản phẩm này");
    ProductTypeDetail typeDetail = productTypeStorage.findById(productTypeId);
    if (Objects.isNull(typeDetail)) throw new ResourceNotFoundException("Không tồn tại chi tiết loại sản phẩm này");
    if (!Objects.equals(typeDetail.getCreatedBy(), username))
      throw new AuthorizationException("Bạn không có quyền sửa");
    ProductTypeDetail productTypeDetail = new ProductTypeDetail();
    productTypeDetail.partnerFromDto(dto);
    productTypeDetail.setCreatedBy(username);
    productTypeDetail.setUpdatedBy(username);
    productTypeStorage.save(productTypeDetail);
  }

  public void updateStatusTypeDetail(UpdateTypeDetailStatusDto dto, String username, List<UserRole> roles) {
    if (!hasUserRole(roles, UserRole.ADMIN) && !hasUserRole(roles, UserRole.OPERATOR))
      throw new AuthorizationException("Bạn không có quyền tạo mới");
    ProductTypeDetail productTypeDetail = productTypeStorage.findById(dto.getId());
    if (Objects.isNull(productTypeDetail))
      throw new ResourceNotFoundException("Không tồn tại chi tiết loại sản phẩm này");

    productTypeDetail.setStatus(dto.getStatus());
    productTypeDetail.setUpdatedBy(username);
    productTypeDetail.setUpdatedAt(DateUtils.nowInMillis());

    productTypeStorage.save(productTypeDetail);
  }

  public List<ProductType> getAllProductType(List<UserRole> roles, String typeName) {
    if (!hasUserRole(roles, UserRole.ADMIN) && !hasUserRole(roles, UserRole.OPERATOR))
      throw new AuthorizationException("Bạn không có quyền xem danh sách này");
    if (StringUtils.isNotBlank(typeName)) {
      return productTypeStorage.findTop20ByProductTypeNameLike(typeName);
    } else {
      return productTypeStorage.findAll();
    }
  }

  public List<ProductTypeResponse> getAllActiveProductType(String productTypeName) {
    List<ProductType> productTypes;
    if (StringUtils.isNotBlank(productTypeName)) {
      productTypes = productTypeStorage.findTop20ByProductTypeNameLikeAndStatus(productTypeName, ProductTypeStatus.ACTIVE);
    } else {
      productTypes = productTypeStorage.findByStatus(ProductTypeStatus.ACTIVE);
    }
    return productTypes.stream().map(ProductType::partnerToProductTypeResponse).collect(Collectors.toList());
  }


  @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
  public Rate updateRating(String username, String productId, Float point, String comment) {
    User user = userStorage.findByUsername(username);
    if (Objects.isNull(user)) throw new ResourceNotFoundException("Không tồn tại người dùng này");

    Product product = productStorage.findProductById(productId);
    if (Objects.isNull(product)) throw new ResourceNotFoundException("Không tồn tại sản phẩm này");

    if(point == 0F && StringUtils.isBlank(comment)){
      try{
        AiDataResponse data = RequestUtil.request(HttpMethod.GET, "https://ai--service-mztju.appengine.bfcplatform.vn/api/v1/ai/language/status?text=" + comment, AiDataResponse.class, null, new HashMap<>());
        point = data.getStatus().getRate().floatValue();
      }catch (Exception ex){
        log.error(ex);
      }
    }
    Rating rating = ratingStorage.findByUsernameAndRefIdAndRatingType(username, productId, RatingType.PRODUCT);
    Rate rate = product.getRate();
    if (Objects.isNull(rating)) {
      rating = new Rating(new ObjectId(), username, productId, RatingType.PRODUCT, point, comment);
      rate.processAddRatePoint(point);
    } else {
      rate.processUpdateRatePoint(rating.getPoint(), point);
      rating.setPoint(point);
      rating.setComment(comment);
      rating.setUpdatedAt(DateUtils.nowInMillis());
    }

    product.setRate(rate);
    productStorage.save(product);
    ratingStorage.save(rating);
    return rate;
  }

  public List<UploadImageData> getImage(String username, String productId) {
    Product product = productStorage.findProductById(productId);
    if (Objects.isNull(product)) throw new ResourceNotFoundException("Không có sản phẩm");
    return product.getImageUrls().stream().map(k -> new UploadImageData(Helper.generateRandomString(), Helper.generateRandomString() + ".png", "done", k, k)).collect(Collectors.toList());
  }

  public void getRating(String username, ProductDetailResponse productDetail) throws Exception {
    if (Objects.nonNull(username)) {
      UserFavorite userFavorite = userFavoriteStorage.findByUsernameAndRefIdAndFavoriteType(username, productDetail.getProductId(), FavoriteType.PRODUCT);
      Rating rating = ratingStorage.findByUsernameAndRefIdAndRatingType(username, productDetail.getProductId(), RatingType.PRODUCT);
      if (Objects.isNull(rating)) rating = new Rating();
      productDetail.setIsLike(!Objects.isNull(userFavorite) && userFavorite.getLike());
      productDetail.setYourRate(rating.getPoint());
      productDetail.setYourComment(rating.getComment());
    }
  }


  public List<ProductDataResponse> getSuggestProduct(String productId) {
    Product product = productStorage.findProductById(productId);
    if (product == null) throw new ResourceNotFoundException("Sản phẩm không tồn tại");

    List<Product> suggestProduct = productStorage.findByCategoryId(product.getCategoryId());

    if (suggestProduct.size() < 10) {
      List<String> productIds = new ArrayList<>(productStatisticStorage.findDistinctTop10ProductIdByOrderByTotalViewDesc());
      List<Product> anotherProduct = productStorage.findTop12ByIdIn(productIds);
      suggestProduct.addAll(anotherProduct);
    }

    return new ArrayList<>(new HashSet<>(suggestProduct.stream()
        .map(Product::assignToProductDataResponse)
        .collect(Collectors.toList())));
  }

}