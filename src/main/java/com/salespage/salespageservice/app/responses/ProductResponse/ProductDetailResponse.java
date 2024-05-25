package com.salespage.salespageservice.app.responses.ProductResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.salespage.salespageservice.app.responses.UploadImageData;
import com.salespage.salespageservice.app.responses.storeResponse.SellerStoreResponse;
import com.salespage.salespageservice.domains.entities.Product;
import com.salespage.salespageservice.domains.entities.ProductCategory;
import com.salespage.salespageservice.domains.entities.infor.Rate;
import com.salespage.salespageservice.domains.info.ProductInfo;
import com.salespage.salespageservice.domains.utils.Helper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ProductDetailResponse {
  @Schema(description = "Số sản phẩm bán đươc")
  protected Long totalSell;
  @Schema(description = "Số lượt xem")
  protected Long totalView;
  @Schema(description = "id sản phẩm")
  String productId;
  @Schema(description = "tên sản phẩm")
  String productName;
  @Schema(description = "giá sản phẩm")
  Double productPrice;
  @Schema(description = "giá bán sản phẩm")
  Double sellProductPrice;
  @Schema(description = "phần trăm giảm giá sản phẩm")
  Double discountPercent;
  @Schema(description = "Danh sách URL ảnh sản phẩm")
  List<UploadImageData> imageUrls = new ArrayList<>();
  @Schema(description = "Mô tả sản phẩm")
  String description;
  @Schema(description = "Trạng thái thích sản phẩm")
  @JsonProperty("isLike")
  Boolean isLike = false;
  @Schema(description = "Đánh giá sản phẩm")
  Rate rate = new Rate();
  @Schema(description = "Đánh giá sản phẩm")
  Float yourRate = 0F;
  @Schema(description = "Nội dung đánh giá sản phẩm")
  String yourComment;
  @Schema(description = "Danh sách các cửa hàng bán")
  List<SellerStoreResponse> stores;
  @Schema(description = "ID danh mục sản phẩm")
  String categoryId;
  @Schema(description = "Tên danh mục sản phẩm")
  String categoryName;
  List<ProductInfo> productInfos = new ArrayList<>();

  @JsonProperty("is_hot")
  Boolean isHot;

  List<ProductDetailInfoResponse> productDetails = new ArrayList<>();

  public void assignFromProduct(Product product) {
    productId = product.getId().toHexString();
    productName = product.getProductName();
    imageUrls = product.getImageUrls().stream()
        .map(image -> new UploadImageData(Helper.generateRandomString(), Helper.generateRandomString() + ".png", "done", image, image))
        .collect(Collectors.toList());
    rate = product.getRate();
    productInfos = product.getProductInfos();
    categoryId = product.getCategoryId();
    isHot = product.getIsHot();
    description = product.getDescription();
  }

  public void assignFromCategory(ProductCategory productCategory) {
    categoryId = productCategory.getId().toHexString();
    categoryName = productCategory.getCategoryName();
  }
}
