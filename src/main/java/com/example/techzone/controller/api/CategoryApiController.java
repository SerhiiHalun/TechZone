package com.example.techzone.controller.api;


import com.example.techzone.model.Category;
import com.example.techzone.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryApiController {
    private final CategoryService categoryService;

    public CategoryApiController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<Category>> getAll() {
        List<Category> categories = categoryService.findAll();
        return categories.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(categories);
    }

    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<Category> getById(@PathVariable int id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<Category> create(@RequestBody Category category) {
        Category createdCategory = categoryService.createCategory(category);

        return createdCategory != null
                ? ResponseEntity.status(HttpStatus.CREATED).body(createdCategory)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Category> update(@PathVariable int id,@RequestBody Category category) {
        return ResponseEntity.ok(categoryService.updateCategory(id,category));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable int id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Category deleted");
    }
}
