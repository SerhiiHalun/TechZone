package com.example.techzone.controller.web;


import com.example.techzone.dto.FeedbackCreateDto;
import com.example.techzone.dto.FeedbackResponseDto;
import com.example.techzone.model.Category;
import com.example.techzone.model.Product;
import com.example.techzone.service.CategoryService;
import com.example.techzone.service.FeedbackService;
import com.example.techzone.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final FeedbackService feedbackService;
    public ProductController(ProductService productService,
                             CategoryService categoriesService, FeedbackService feedbackService) {
        this.productService = productService;
        this.categoryService = categoriesService;
        this.feedbackService = feedbackService;
    }
    @GetMapping("/home")
    public String showHome(Model model) {
        model.addAttribute("specialProducts", productService.getProductsByDiscount(true, null));
        model.addAttribute("randomProducts", productService.getProductsByDiscount(false, 10));
        model.addAttribute("allCategories", categoryService.findAll());
        return "product/home";
    }
    @GetMapping
    public String listProducts(Model model) {
        return "redirect:/home";
    }

    @GetMapping("/search")
    public String searchProducts(
            @RequestParam(name = "query", required = false) String query,
            @RequestParam(name = "categoryId", required = false) Integer categoryId,
            Model model
    ) {
        List<Category> allCategories = categoryService.findAll();
        model.addAttribute("allCategories", allCategories);

        if (categoryId != null) {
            List<Product> products = productService.getProductsByCategoryId(categoryId);
            model.addAttribute("products", products);
            model.addAttribute("selectedCategoryId", categoryId);
            model.addAttribute("searchQuery", null);
        } else if (query != null && !query.trim().isEmpty()) {
            List<Product> products = productService.searchProductsByName(query.trim());
            model.addAttribute("products", products);
            model.addAttribute("searchQuery", query.trim());
            model.addAttribute("selectedCategoryId", null);
        } else {
            List<Product> products = productService.getAllProducts();
            model.addAttribute("products", products);
            model.addAttribute("searchQuery", "");
            model.addAttribute("selectedCategoryId", null);
        }
        return "product/search";
    }

    @GetMapping("/{id}")
    public String getProduct(@PathVariable int id, Model model) {
        Product product = productService.getProductById(id);
        List<FeedbackResponseDto> feedbacks = feedbackService.getFeedbacksByProductId(id);

        model.addAttribute("product", product);
        model.addAttribute("specialProducts", productService.getProductsByDiscount(true, null));
        model.addAttribute("feedbacks", feedbacks);
        model.addAttribute("review", new FeedbackCreateDto());

        return "product/view-product";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new Product());

        List<Category> categories = categoryService.findAll();
        model.addAttribute("categories", categories);

        return "product/create";
    }

    @PostMapping("/create")
    public String createProduct(@ModelAttribute("product") Product product,
                                @RequestParam(name = "files", required = false) List<MultipartFile> files,
                                @RequestParam(name = "mainIndex", defaultValue = "0") int mainIndex) {

        Product createdProduct = productService.createProductWithImages(product,
                (files != null ? files : List.of()), mainIndex);


        return "redirect:/product/" + createdProduct.getId();
    }


    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable int id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);

        List<Category> categories = categoryService.findAll();
        model.addAttribute("categories", categories);

        return "product/edit";
    }

    @PostMapping("/{id}/update")
    public String updateProduct(@PathVariable int id,
                                @ModelAttribute("product") Product updatedProduct,
                                @RequestParam(name = "files", required = false) List<MultipartFile> files,
                                @RequestParam(name = "mainIndex", defaultValue = "-1") int mainIndex,
                                @RequestParam(name = "deleteImageIds", required = false) List<Long> deleteImageIds) {

        productService.updateProductWithImages(id, updatedProduct, files, mainIndex, deleteImageIds);
        return "redirect:/product/{id}";
    }


    @GetMapping("/{id}/delete")
    public String deleteProduct(@PathVariable int id) {
        productService.deleteProduct(id);
        return "redirect:/product";
    }
}

