package com.shop.CategoryServiceRest.Service;

import com.shop.CategoryServiceRest.Model.Category;
import com.shop.CategoryServiceRest.Model.Item;

import java.util.List;
import java.util.Set;

public interface CategoryService {
    List<Category> findByParent(Category parent);
    List<Category> findByParentIsNull();
    Category findById(Long id);
    Category findByName(String name);
    Set<String> getAllNamesOfRootCategories();
    Set<String> getAllNamesOfChildren();
    Set<Item> getAllItemsByCategory(Category category);

    void save(Category category);

    void delete(Category category);
}
