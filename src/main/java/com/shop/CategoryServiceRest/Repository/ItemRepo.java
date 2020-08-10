package com.shop.CategoryServiceRest.Repository;

import com.shop.CategoryServiceRest.Model.Category;
import com.shop.CategoryServiceRest.Model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepo extends JpaRepository<Item, Long> {
    Page<Item> findAllByCategory(Category category, Pageable pageable);
}
