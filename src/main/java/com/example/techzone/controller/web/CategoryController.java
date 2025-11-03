package com.example.techzone.controller.web;


import com.example.techzone.model.Category;
import com.example.techzone.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/category")
public class CategoryController {
    CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    @GetMapping("/create")
    public String showCategoryForm(Model model){
        model.addAttribute("category",new Category());
        return "category/create";
    }
    @PostMapping("/create")
    public String createCategory(@ModelAttribute("category") Category category){
        categoryService.createCategory(category);
        return "redirect:/product/home";
    }

}
