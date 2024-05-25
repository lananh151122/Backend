package com.salespage.salespageservice.app.controllers.SellerControllers;

import com.salespage.salespageservice.app.controllers.BaseController;
import com.salespage.salespageservice.app.dtos.productDtos.ProductDetailDto;
import com.salespage.salespageservice.app.responses.BaseResponse;
import com.salespage.salespageservice.domains.services.ProductDetailService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Seller product detail", description = "Quản lý sản phẩm được bán")
@CrossOrigin
@RestController
@RequestMapping("api/v1/seller/product-detail")
@SecurityRequirement(name = "bearerAuth")
public class SellerProductDetail extends BaseController {

  @Autowired
  ProductDetailService productDetailService;

  @GetMapping("{productId}")
  public ResponseEntity<BaseResponse> getProductDetail(Authentication authentication, @PathVariable String productId) {
    try {
      return successApi(productDetailService.getProductDetail(getUsername(authentication), productId));
    } catch (Exception ex) {
      return errorApi(ex);
    }
  }

  @GetMapping("{productId}/upload")
  public ResponseEntity<BaseResponse> uploadImageForProductDetai(Authentication authentication, @PathVariable String productId, @RequestBody MultipartFile file) {
    try {
      return successApi(productDetailService.uploadImageUrl(getUsername(authentication), productId, file));
    } catch (Exception ex) {
      return errorApi(ex);
    }
  }

  @PostMapping("")
  public ResponseEntity<BaseResponse> createProductDetail(Authentication authentication, @RequestBody ProductDetailDto dto) {
    try {
      productDetailService.createProductDetail(getUsername(authentication), dto);
      return successApi("Tạo thành công");
    } catch (Exception ex) {
      return errorApi(ex);
    }
  }

  @PutMapping("{detailId}")
  public ResponseEntity<BaseResponse> updateProductDetail(Authentication authentication, @PathVariable String detailId, @RequestBody ProductDetailDto dto) {
    try {
      productDetailService.updateProductDetail(getUsername(authentication), detailId, dto);
      return successApi("Cập nhật thành công");
    } catch (Exception ex) {
      return errorApi(ex);
    }
  }

  @DeleteMapping("{detailId}")
  public ResponseEntity<BaseResponse> deleteProductDetail(Authentication authentication, @PathVariable String detailId) {
    try {
      productDetailService.deleteProductDetail(getUsername(authentication), detailId);
      return successApi("Xóa thành công");
    } catch (Exception ex) {
      return errorApi(ex);
    }
  }
}
