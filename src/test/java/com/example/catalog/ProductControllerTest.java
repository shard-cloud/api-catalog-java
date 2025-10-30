package com.example.catalog;

import com.example.catalog.dto.CreateProductDto;
import com.example.catalog.entity.Product;
import com.example.catalog.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
public class ProductControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void testGetAllProducts() throws Exception {
        // Create test product
        Product product = new Product();
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(new BigDecimal("99.99"));
        product.setCategory("Test Category");
        product.setStock(10);
        productRepository.save(product);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Test Product"));
    }

    @Test
    void testGetProductById() throws Exception {
        // Create test product
        Product product = new Product();
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(new BigDecimal("99.99"));
        product.setCategory("Test Category");
        product.setStock(10);
        Product savedProduct = productRepository.save(product);

        mockMvc.perform(get("/api/products/" + savedProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.price").value(99.99));
    }

    @Test
    void testCreateProduct() throws Exception {
        CreateProductDto createProductDto = new CreateProductDto();
        createProductDto.setName("New Product");
        createProductDto.setDescription("New Description");
        createProductDto.setPrice(new BigDecimal("149.99"));
        createProductDto.setCategory("New Category");
        createProductDto.setStock(20);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createProductDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Product"))
                .andExpect(jsonPath("$.price").value(149.99));
    }

    @Test
    void testUpdateProduct() throws Exception {
        // Create test product
        Product product = new Product();
        product.setName("Original Product");
        product.setDescription("Original Description");
        product.setPrice(new BigDecimal("99.99"));
        product.setCategory("Original Category");
        product.setStock(10);
        Product savedProduct = productRepository.save(product);

        // Update product
        String updateJson = """
                {
                    "name": "Updated Product",
                    "price": 199.99
                }
                """;

        mockMvc.perform(put("/api/products/" + savedProduct.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Product"))
                .andExpect(jsonPath("$.price").value(199.99));
    }

    @Test
    void testDeleteProduct() throws Exception {
        // Create test product
        Product product = new Product();
        product.setName("Product to Delete");
        product.setDescription("Description");
        product.setPrice(new BigDecimal("99.99"));
        product.setCategory("Category");
        product.setStock(10);
        Product savedProduct = productRepository.save(product);

        mockMvc.perform(delete("/api/products/" + savedProduct.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void testSearchProducts() throws Exception {
        // Create test products
        Product product1 = new Product();
        product1.setName("Smartphone");
        product1.setDescription("Mobile phone");
        product1.setPrice(new BigDecimal("999.99"));
        product1.setCategory("Electronics");
        product1.setStock(10);
        productRepository.save(product1);

        Product product2 = new Product();
        product2.setName("Laptop");
        product2.setDescription("Computer laptop");
        product2.setPrice(new BigDecimal("1299.99"));
        product2.setCategory("Electronics");
        product2.setStock(5);
        productRepository.save(product2);

        mockMvc.perform(get("/api/products/search?q=phone"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Smartphone"));
    }

    @Test
    void testGetProductsByCategory() throws Exception {
        // Create test products
        Product product1 = new Product();
        product1.setName("Smartphone");
        product1.setDescription("Mobile phone");
        product1.setPrice(new BigDecimal("999.99"));
        product1.setCategory("Electronics");
        product1.setStock(10);
        productRepository.save(product1);

        Product product2 = new Product();
        product2.setName("T-Shirt");
        product2.setDescription("Cotton shirt");
        product2.setPrice(new BigDecimal("19.99"));
        product2.setCategory("Clothing");
        product2.setStock(50);
        productRepository.save(product2);

        mockMvc.perform(get("/api/products/category/Electronics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].category").value("Electronics"));
    }

    @Test
    void testGetAllCategories() throws Exception {
        // Create test products with different categories
        Product product1 = new Product();
        product1.setName("Product 1");
        product1.setDescription("Description 1");
        product1.setPrice(new BigDecimal("99.99"));
        product1.setCategory("Electronics");
        product1.setStock(10);
        productRepository.save(product1);

        Product product2 = new Product();
        product2.setName("Product 2");
        product2.setDescription("Description 2");
        product2.setPrice(new BigDecimal("49.99"));
        product2.setCategory("Clothing");
        product2.setStock(20);
        productRepository.save(product2);

        mockMvc.perform(get("/api/products/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").value("Clothing"))
                .andExpect(jsonPath("$[1]").value("Electronics"));
    }

    @Test
    void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"))
                .andExpect(jsonPath("$.message").value("Catalog API is healthy"));
    }
}
