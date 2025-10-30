package com.example.catalog.service;

import com.example.catalog.dto.CreateProductDto;
import com.example.catalog.dto.ProductDto;
import com.example.catalog.dto.UpdateProductDto;
import com.example.catalog.entity.Product;
import com.example.catalog.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    /**
     * Get all products with pagination
     */
    @Transactional(readOnly = true)
    public Page<ProductDto> getAllProducts(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> products = productRepository.findAll(pageable);
        
        return products.map(this::convertToDto);
    }

    /**
     * Get product by ID
     */
    @Transactional(readOnly = true)
    public Optional<ProductDto> getProductById(Long id) {
        return productRepository.findById(id)
                .map(this::convertToDto);
    }

    /**
     * Create a new product
     */
    public ProductDto createProduct(CreateProductDto createProductDto) {
        Product product = new Product();
        product.setName(createProductDto.getName());
        product.setDescription(createProductDto.getDescription());
        product.setPrice(createProductDto.getPrice());
        product.setCategory(createProductDto.getCategory());
        product.setStock(createProductDto.getStock());

        Product savedProduct = productRepository.save(product);
        return convertToDto(savedProduct);
    }

    /**
     * Update an existing product
     */
    public Optional<ProductDto> updateProduct(Long id, UpdateProductDto updateProductDto) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    if (updateProductDto.getName() != null) {
                        existingProduct.setName(updateProductDto.getName());
                    }
                    if (updateProductDto.getDescription() != null) {
                        existingProduct.setDescription(updateProductDto.getDescription());
                    }
                    if (updateProductDto.getPrice() != null) {
                        existingProduct.setPrice(updateProductDto.getPrice());
                    }
                    if (updateProductDto.getCategory() != null) {
                        existingProduct.setCategory(updateProductDto.getCategory());
                    }
                    if (updateProductDto.getStock() != null) {
                        existingProduct.setStock(updateProductDto.getStock());
                    }

                    Product updatedProduct = productRepository.save(existingProduct);
                    return convertToDto(updatedProduct);
                });
    }

    /**
     * Delete a product by ID
     */
    public boolean deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Search products by name or description
     */
    @Transactional(readOnly = true)
    public Page<ProductDto> searchProducts(String searchTerm, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Product> products = productRepository.searchProducts(searchTerm, pageable);
        
        return products.map(this::convertToDto);
    }

    /**
     * Get products by category
     */
    @Transactional(readOnly = true)
    public Page<ProductDto> getProductsByCategory(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Product> products = productRepository.findByCategoryIgnoreCase(category, pageable);
        
        return products.map(this::convertToDto);
    }

    /**
     * Get all distinct categories
     */
    @Transactional(readOnly = true)
    public List<String> getAllCategories() {
        return productRepository.findDistinctCategories();
    }

    /**
     * Get products with low stock
     */
    @Transactional(readOnly = true)
    public List<ProductDto> getProductsWithLowStock(Integer threshold) {
        List<Product> products = productRepository.findProductsWithLowStock(threshold);
        return products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Convert Product entity to ProductDto
     */
    private ProductDto convertToDto(Product product) {
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory(),
                product.getStock(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
