package com.salespage.salespageservice.app.controllers.SellerControllers;

import com.salespage.salespageservice.app.controllers.BaseController;
import com.salespage.salespageservice.app.dtos.ProductCategories.ProductCategoryDto;
import com.salespage.salespageservice.domains.services.ProductCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "Seller product category", description = "Quản lý danh mục sản phẩn")
@RequestMapping("api/v1/seller/product-category")
@RestController
@CrossOrigin
@SecurityRequirement(name = "bearerAuth")
public class SellerProductCategoryController extends BaseController {

  @Autowired
  private ProductCategoryService productCategoryService;

  @GetMapping("")
  @Operation(summary = "Lấy danh sách danh mục sản phẩm", description = "Lấy danh sách danh mục sản phẩm của người dùng hiện tại")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Thành công"),
      @ApiResponse(responseCode = "401", description = "Không được phép"),
      @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ")
  })
  private ResponseEntity<?> getProductCategory(Authentication authentication) {
    try {
      return successApi(productCategoryService.getProductCategory(getUsername(authentication)));
    } catch (Exception ex) {
      return errorApi(ex);
    }
  }

  @GetMapping("{id}")
  @Operation(summary = "Lấy chi tiết danh mục sản phẩm", description = "Lấy chi tiết danh mục sản phẩm theo ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Thành công"),
      @ApiResponse(responseCode = "401", description = "Không được phép"),
      @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ")
  })
  private ResponseEntity<?> getDetailProductCategory(Authentication authentication, @PathVariable String id) {
    try {
      return successApi(productCategoryService.getDetailProductCategory(getUsername(authentication), id));
    } catch (Exception ex) {
      return errorApi(ex);
    }
  }

  @PostMapping("")
  @Operation(summary = "Tạo danh mục sản phẩm", description = "Tạo mới một danh mục sản phẩm")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Thành công"),
      @ApiResponse(responseCode = "401", description = "Không được phép"),
      @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ")
  })
  private ResponseEntity<?> createProductCategory(Authentication authentication, @RequestBody ProductCategoryDto dto) {
    try {
      productCategoryService.createProductCategory(getUsername(authentication), dto);
      return successApi("Tạo danh mục sản phẩm thành công");
    } catch (Exception ex) {
      return errorApi(ex);
    }
  }

  @PutMapping("{id}")
  @Operation(summary = "Cập nhật danh mục sản phẩm", description = "Cập nhật một danh mục sản phẩm")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Thành công"),
      @ApiResponse(responseCode = "401", description = "Không được phép"),
      @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ")
  })
  private ResponseEntity<?> updateProductCategory(Authentication authentication, @PathVariable String id, @RequestBody @Valid ProductCategoryDto dto) {
    try {
      productCategoryService.updateProductCategory(getUsername(authentication), id, dto);
      return successApi("Cập nhật danh mục sản phẩm thành công");
    } catch (Exception ex) {
      return errorApi(ex);
    }
  }

  @DeleteMapping("{id}")
  @Operation(summary = "Xóa danh mục sản phẩm", description = "Xóa một danh mục sản phẩm theo ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Thành công"),
      @ApiResponse(responseCode = "401", description = "Không được phép"),
      @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ")
  })
  private ResponseEntity<?> deleteProductCategory(Authentication authentication, @PathVariable String id) {
    try {
      productCategoryService.deleteProductCategory(getUsername(authentication), id);
      return successApi("Xóa danh mục sản phẩm thành công");
    } catch (Exception ex) {
      return errorApi(ex);
    }
  }
}
