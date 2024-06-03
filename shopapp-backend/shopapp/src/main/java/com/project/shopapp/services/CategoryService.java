package com.project.shopapp.services;

import com.project.shopapp.dtos.CategoryDTO;
import com.project.shopapp.model.Category;

import java.util.List;

public interface CategoryService {
    Category createCategory(CategoryDTO categoryDTO);
    Category getCategoryById(long id);
    List<Category> getAllCategory();
    Category updateCategory(CategoryDTO categoryDTO, long productId);
    void deleteCategory(long id);



}
