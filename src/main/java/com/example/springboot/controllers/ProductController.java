package com.example.springboot.controllers;

import com.example.springboot.dtos.ProductRecordDto;
import com.example.springboot.models.ProductModel;
import com.example.springboot.repositories.ProductRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {

  @Autowired ProductRepository productRepository;

  @PostMapping("/products")
  public ResponseEntity<ProductModel> saveProduct(
      @RequestBody @Valid ProductRecordDto productRecordDto) {
    var productModel = new ProductModel();
    BeanUtils.copyProperties(productRecordDto, productModel);
    return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(productModel));
  }

  @GetMapping("/products")
  public ResponseEntity<List<ProductModel>> getAllProduct() {
    return ResponseEntity.status(HttpStatus.OK).body(productRepository.findAll());
  }

  @GetMapping("/products/{id}")
  public ResponseEntity<Object> getOneProduct(@PathVariable(value = "id") UUID id) {
    Optional<ProductModel> product = productRepository.findById(id);

    return product
        .<ResponseEntity<Object>>map(
            productModel -> ResponseEntity.status(HttpStatus.OK).body(productModel))
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found."));
  }

  @PutMapping("/products/{id}")
  public ResponseEntity<Object> updateProduct(
      @PathVariable(value = "id") UUID id, @RequestBody @Valid ProductRecordDto productRecordDto) {
    ResponseEntity<Object> getProductResponse = getOneProduct(id);

    if (getProductResponse.getStatusCode() != HttpStatus.OK) return getProductResponse;

    ProductModel product = (ProductModel) getProductResponse.getBody();

    assert product != null;
    BeanUtils.copyProperties(productRecordDto, product);
    return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(product));
  }

  @DeleteMapping("/products/{id}")
  public ResponseEntity<Object> deleteProduct(@PathVariable(value = "id") UUID id) {
    ResponseEntity<Object> getProductResponse = getOneProduct(id);

    if (getProductResponse.getStatusCode() != HttpStatus.OK) return getProductResponse;

    ProductModel product = (ProductModel) getProductResponse.getBody();

    assert product != null;
    productRepository.delete(product);
    return ResponseEntity.status(HttpStatus.OK).body("Product deleted sucessfuly.");
  }
}
