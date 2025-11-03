package com.example.techzone.service;


import com.example.techzone.model.Image;
import com.example.techzone.model.Product;
import com.example.techzone.repository.CategoryRepository;
import com.example.techzone.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ImageService imageService;
    @Autowired
    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository, ImageService imageService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.imageService = imageService;
    }
    @Transactional
    public Product createProductWithImages(Product product, List<MultipartFile> files, int mainIndex) {

        product.setImages(new ArrayList<>());
        Product savedProduct = productRepository.save(product);


        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            if (!file.isEmpty()) {
                boolean isMain = (i == mainIndex);

                Image image = imageService.addImageToProduct(file, savedProduct, isMain);

                savedProduct.getImages().add(image);
            }
        }

        return savedProduct;
    }
    @Transactional(readOnly = true)
    @Cacheable("product")
    public Product getProductById(int id){
        return productRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Product with id " + id + " not found"));
    };
    @Transactional(readOnly = true)
    @Cacheable("allProducts")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    public List<Product> getProductsByDiscount(boolean withDiscount, Integer limit) {
        List<Product> products = withDiscount
                ? productRepository.findByDiscountGreaterThan(0)
                : productRepository.findByDiscount(0);

        Collections.shuffle(products);
        if (limit != null) {
            return products.stream().limit(limit).toList();
        }
        return products;
    }
    @Transactional(readOnly = true)
    public List<Product> getProductsByCategoryId(int categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    @Transactional(readOnly = true)
    public List<Product> searchProductsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return productRepository.findAll();
        }
        return productRepository.findByNameContainingIgnoreCase(name.trim());
    }
    @Transactional
    public Product updateProductWithImages(int id,
                                           Product updatedProduct,
                                           List<MultipartFile> newFiles,
                                           int mainIndex,
                                           List<Long> deleteImageIds) {


        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product with id " + id + " not found"));


        product.setName(updatedProduct.getName());
        product.setDescription(updatedProduct.getDescription());
        product.setPrice(updatedProduct.getPrice());
        product.setAvailAmount(updatedProduct.getAvailAmount());
        product.setDiscount(updatedProduct.getDiscount());
        product.setCreatedDate(updatedProduct.getCreatedDate());
        product.setCategory(updatedProduct.getCategory());

        productRepository.save(product);


        if (deleteImageIds != null) {
            for (Long imageId : deleteImageIds) {
                imageService.deleteImage(imageId);
            }
        }

        if (newFiles != null && !newFiles.isEmpty()) {
            for (int i = 0; i < newFiles.size(); i++) {
                MultipartFile file = newFiles.get(i);
                if (!file.isEmpty()) {
                    boolean isMain = (i == mainIndex);
                    Image image = imageService.addImageToProduct(file, product, isMain);
                }
            }
        }

        return product;
    }

    @Transactional
    public void deleteProduct(int id) {
        if (!productRepository.existsById(id)) {
            throw new NoSuchElementException("Product with id " + id + " not found");
        }
        productRepository.deleteById(id);
    }
}
