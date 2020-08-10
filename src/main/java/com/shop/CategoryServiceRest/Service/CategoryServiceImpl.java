package com.shop.CategoryServiceRest.Service;

import com.shop.CategoryServiceRest.Model.Category;
import com.shop.CategoryServiceRest.Model.Item;
import com.shop.CategoryServiceRest.Repository.CategoryRepo;
import com.shop.CategoryServiceRest.Repository.ItemRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    private CategoryRepo categoryRepo;
    private ItemRepo itemRepo;

    @Autowired
    public void setCategoryRepo(CategoryRepo categoryRepo) {
        logger.debug("Setting categoryRepo");
        this.categoryRepo = categoryRepo;
    }

    @Autowired
    public void setItemRepo(ItemRepo itemRepo) {
        this.itemRepo = itemRepo;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "categories", key = "#parent")
    public List<Category> findByParent(Category parent) {
        logger.info("findByParent method called");
        return categoryRepo.findByParent(parent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> findByParentIsNull() {
        logger.info("findByParentIsNull method called");
        return categoryRepo.findByParentIsNull();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "categories")
    public Category findById(Long id) {
        logger.info("findById method called for category with id = " + id);
        return categoryRepo.findById(id).orElseThrow(NoSuchElementException::new);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "categories")
    public Category findByName(String name) {
        logger.info("findByName method called");
        return categoryRepo.findByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> getAllNamesOfRootCategories() {
        logger.info("getAllNamesOfCategories method called");
        return categoryRepo.findByParentIsNull().stream()
                .map(Category::getName)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> getAllNamesOfChildren() {
        logger.info("getAllNamesOfChildren method called");
        return categoryRepo.findByParentIsNull().parallelStream()
                .map(categoryRepo::findByParent)
                .flatMap(Collection::stream)
                .map(Category::getName)
                .collect(Collectors.toSet());
    }

    @Override
    public Page<Item> getAllItemsByCategory(Category category, Pageable pageable) {
        logger.info("Called getAllItemsByCategory method");

        return itemRepo.findAllByCategory(category, pageable);
    }

    @Override
    public void save(Category category) {
        logger.info("Saving category to database");
        categoryRepo.save(category);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "categories", key = "#category"),
            @CacheEvict(value = "categories", key = "#category.id"),
            @CacheEvict(value = "categories", key = "#category.name")
    })
    public void delete(Category category) {
        logger.info("Deleting category with id = " + category.getId() + " from database");
        categoryRepo.delete(category);
    }
}
