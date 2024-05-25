package com.salespage.salespageservice.domains.storages;

import com.salespage.salespageservice.domains.entities.ProductType;
import com.salespage.salespageservice.domains.entities.ProductTypeDetail;
import com.salespage.salespageservice.domains.entities.status.ProductTypeStatus;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductTypeStorage extends BaseStorage {

  public void save(ProductType productType) {
    productTypeRepository.save(productType);
  }

  public ProductType findByProductType(String productType) {
    return productTypeRepository.findByProductType(productType);
  }

  public ProductType findProductTypeById(String typeId) {
    return productTypeRepository.findById(new ObjectId(typeId)).orElse(null);
  }

  public List<ProductType> findTop20ByProductTypeNameLikeAndStatus(String typeName, ProductTypeStatus status) {
    return productTypeRepository.findTop20ByProductTypeNameLikeOrProductTypeLikeAndStatus(typeName, typeName, status);
  }

  public List<ProductType> findTop20ByProductTypeNameLike(String typeName) {
    return productTypeRepository.findTop20ByProductTypeNameLike(typeName);
  }


  public List<ProductType> findTop20() {
    return productTypeRepository.findTop20By();
  }

  public void save(ProductTypeDetail productTypeDetail) {
    productTypeDetailRepository.save(productTypeDetail);
  }

  public ProductTypeDetail findProductTypeDetailByTypeNameAndTypeDetailName(String typeName, String typeDetailName) {
    return productTypeDetailRepository.findProductTypeDetailByTypeNameAndTypeDetailName(typeName, typeDetailName);
  }

  public ProductTypeDetail findById(String id) {
    return productTypeDetailRepository.findProductTypeDetailById(new ObjectId(id));
  }

  public List<ProductTypeDetail> findByProductId(String productId) {
    return productTypeDetailRepository.findByProductId(productId);
  }

  public List<ProductType> findAll() {
    return productTypeRepository.findAll();
  }

  public List<ProductType> findByStatus(ProductTypeStatus status) {
    return productTypeRepository.findByStatus(status);
  }

  public List<ProductTypeDetail> getTop10SimilarProduct(List<String> listType) {
    return productTypeDetailRepository.findTop12ByTypeDetailNameInOrderByCreatedAtDesc(listType);
  }

}
