package com.salespage.salespageservice.app.controllers;

import com.salespage.salespageservice.app.dtos.productTransactionDto.ProductTransactionDto;
import com.salespage.salespageservice.app.dtos.productTransactionDto.ProductTransactionInfoDto;
import com.salespage.salespageservice.app.responses.BaseResponse;
import com.salespage.salespageservice.domains.exceptions.TransactionException;
import com.salespage.salespageservice.domains.exceptions.info.ErrorCode;
import com.salespage.salespageservice.domains.services.ProductTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("api/v1/product-transaction")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Product transaction", description = "Quản lý các giao dịch mua bán của người dùng")
public class ProductTransactionController extends BaseController {

  @Autowired
  private ProductTransactionService productTransactionService;

  @PostMapping("")
  @Operation(summary = "Tạo giao dịch sản phẩm", description = "Tạo một giao dịch sản phẩm mới với thông tin đã cho")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Giao dịch sản phẩm đã được tạo"),
      @ApiResponse(responseCode = "400", description = "Đầu vào không hợp lệ"),
      @ApiResponse(responseCode = "401", description = "Không được phép"),
      @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ")
  })
  public ResponseEntity<BaseResponse> createProductTransaction(
      Authentication authentication,
      @RequestBody @Valid List<ProductTransactionDto> dtos) {
    try {
//      productTransactionService.createProductTransaction(getUsername(authentication), dtos);
      return successApi("Tạo mới giao dịch thành công");
    } catch (TransactionException ex) {
      return errorApi(ErrorCode.NOT_ENOUGH_MONEY);
    } catch (Exception ex) {
      return errorApi(ex);
    }
  }

  @PutMapping("")
  @Operation(summary = "Cập nhật giao dịch sản phẩm", description = "Cập nhật giao dịch sản phẩm với thông tin đã cho")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Cập nhật giao dịch sản phẩm thành công"),
      @ApiResponse(responseCode = "400", description = "Đầu vào không hợp lệ"),
      @ApiResponse(responseCode = "401", description = "Không được phép"),
      @ApiResponse(responseCode = "404", description = "Không tìm thấy giao dịch sản phẩm"),
      @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ")
  })
  public ResponseEntity<BaseResponse> updateProductTransaction(Authentication authentication, @RequestParam String transactionId, @RequestBody ProductTransactionInfoDto dto) {
    try {
//      productTransactionService.updateProductTransaction(getUsername(authentication), dto, transactionId);
      return successApi("Cập nhật giao dịch thành công");
    } catch (Exception ex) {
      return errorApi(ex);
    }
  }

  @PutMapping("cancel")
  @Operation(summary = "Hủy bỏ giao dịch sản phẩm", description = "Hủy bỏ giao dịch sản phẩm với mã giao dịch tương ứng")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Giao dịch sản phẩm đã được hủy bỏ"),
      @ApiResponse(responseCode = "400", description = "Đầu vào không hợp lệ"),
      @ApiResponse(responseCode = "401", description = "Không được phép"),
      @ApiResponse(responseCode = "404", description = "Không tìm thấy giao dịch sản phẩm"),
      @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ")
  })
  public ResponseEntity<BaseResponse> cancelProductTransaction(Authentication authentication, @RequestParam String transactionId) {
    try {
//      productTransactionService.cancelProductTransaction(getUsername(authentication), transactionId);
      return successApi("Hủy bỏ giao dịch thành công");
    } catch (Exception ex) {
      return errorApi(ex);
    }
  }

  @GetMapping("")
  @Operation(summary = "Tìm kiếm lịch sử giao dịch", description = "Tìm kiếm lịch sử giao dịch")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Tìm kiếm giao dịch thành công"),
      @ApiResponse(responseCode = "400", description = "Đầu vào không hợp lệ"),
      @ApiResponse(responseCode = "401", description = "Không được phép"),
      @ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ")
  })
  public ResponseEntity<BaseResponse> getAllProductTransaction(Authentication authentication,
                                                               @RequestParam(required = false) String sellerUsername,
                                                               @RequestParam(required = false) String storeName,
                                                               @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") @Schema(type = "string", format = "date") Date startDate,
                                                               @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") @Schema(type = "string", format = "date") Date endDate,
                                                               Pageable pageable
  ) {
    try {
      return successApi(productTransactionService.getAllTransaction(getUsername(authentication), sellerUsername, storeName, startDate, endDate, pageable));
    } catch (Exception ex) {
      return errorApi(ex);
    }
  }

}
