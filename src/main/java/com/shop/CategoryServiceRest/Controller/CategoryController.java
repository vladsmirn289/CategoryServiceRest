package com.shop.CategoryServiceRest.Controller;

import com.shop.CategoryServiceRest.Model.Category;
import com.shop.CategoryServiceRest.Model.Item;
import com.shop.CategoryServiceRest.Service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    private final static Logger logger = LoggerFactory.getLogger(CategoryController.class);

    private CategoryService categoryService;

    @Autowired
    public void setCategoryService(CategoryService categoryService) {
        logger.info("Setting categoryService");
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<Category>> showCategoriesByParent(@RequestBody Category parent) {
        logger.info("Called showCategoriesByParent method");
        List<Category> categories = categoryService.findByParent(parent);

        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @GetMapping("/top")
    public ResponseEntity<List<Category>> showTopCategories() {
        logger.info("Called showTopCategories method");
        List<Category> categories = categoryService.findByParentIsNull();

        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> showCategoryById(@PathVariable("id") Long id) {
        logger.info("Called showCategoryById method");

        try{
            Category category = categoryService.findById(id);
            return new ResponseEntity<>(category, HttpStatus.OK);
        } catch (NoSuchElementException ex) {
            logger.warn("Category with id - " + id + " not found");
            logger.error(ex.toString());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(params = {"name"})
    public ResponseEntity<Category> showCategoryByName(@RequestParam("name") String name) {
        logger.info("Called showCategoryByName method");
        Category category = categoryService.findByName(name);

        if (category != null) {
            return new ResponseEntity<>(category, HttpStatus.OK);
        } else {
            logger.warn("Category with name - " + name + " not found");
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/allRootNames")
    public ResponseEntity<List<String>> showAllRootNames() {
        logger.info("Called showAllRootNames method");
        List<String> names = new ArrayList<>(categoryService.getAllNamesOfRootCategories());

        return new ResponseEntity<>(names, HttpStatus.OK);
    }

    @GetMapping("/allChildNames")
    public ResponseEntity<List<String>> showAllChildNames() {
        logger.info("Called showAllChildNames method");
        List<String> names = new ArrayList<>(categoryService.getAllNamesOfChildren());

        return new ResponseEntity<>(names, HttpStatus.OK);
    }

    @GetMapping("/{id}/items")
    public ResponseEntity<List<Item>> showAllItemByCategory(@PathVariable("id") Long id) {
        logger.info("Called showAllItemsByCategory method");
        Category category = showCategoryById(id).getBody();
        List<Item> items = new ArrayList<>(categoryService.getAllItemsByCategory(category));

        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable("id") Long id,
                                                   @RequestBody @Valid Category category,
                                                   BindingResult bindingResult) {
        logger.info("Called updateCategory method");

        if (bindingResult.hasErrors()) {
            logger.info("Bad request on update category information");
            return new ResponseEntity<>(category, HttpStatus.BAD_REQUEST);
        }

        try {
            Category persistentCategory = categoryService.findById(id);

            BeanUtils.copyProperties(category, persistentCategory, "id");
            categoryService.save(persistentCategory);
            return new ResponseEntity<>(persistentCategory, HttpStatus.OK);
        } catch (NoSuchElementException ex) {
            logger.warn("Category with id - " + id + " not found");
            logger.error(ex.toString());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<Category> createNewCategory(@RequestBody @Valid Category category,
                                                      BindingResult bindingResult) {
        logger.info("Called createNewCategory method");

        if (bindingResult.hasErrors()) {
            logger.info("Bad request on create category information");
            return new ResponseEntity<>(category, HttpStatus.BAD_REQUEST);
        }

        categoryService.save(category);
        return new ResponseEntity<>(category, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable("id") Long id) {
        logger.info("Called deleteCategory method");
        Category category = showCategoryById(id).getBody();

        try {
            categoryService.delete(category);
        } catch (NoSuchElementException ex) {
            logger.warn("Category with id - " + id + " not found");
            logger.error(ex.toString());
        }
    }
}
