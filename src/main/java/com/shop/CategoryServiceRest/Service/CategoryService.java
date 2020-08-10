package com.shop.CategoryServiceRest.Service;

import com.shop.CategoryServiceRest.Model.Category;
import com.shop.CategoryServiceRest.Model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface CategoryService {
    List<Category> findByParent(Category parent);
    List<Category> findByParentIsNull();
    Category findById(Long id);
    Category findByName(String name);
    Set<String> getAllNamesOfRootCategories();
    Set<String> getAllNamesOfChildren();
    Page<Item> getAllItemsByCategory(Category category, Pageable pageable);

    void save(Category category);

    void delete(Category category);
}
