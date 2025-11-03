package com.example.techzone.service;

import com.example.techzone.model.Category;
import com.example.techzone.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category findById(int id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Category with id " + id + " not found"));
    }

    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    public void deleteById(int id) {
        if (!categoryRepository.existsById(id)) {
            throw new NoSuchElementException("Category with id " + id + " not found");
        }
        categoryRepository.deleteById(id);
    }
    public List<Category> getAllCategory() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(int id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
    }

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    public Category updateCategory(int id,Category category) {
        Category toUpdate = getCategoryById(id);
        if (category.getName() != null && !category.getName().isEmpty()) {
            toUpdate.setName(category.getName());
        }
        return categoryRepository.save(toUpdate);
    }

    public void deleteCategory(int id) {
        categoryRepository.deleteById(id);
    }

}
