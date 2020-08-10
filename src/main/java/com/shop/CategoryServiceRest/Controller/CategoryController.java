package com.shop.CategoryServiceRest.Controller;

import com.shop.CategoryServiceRest.Model.Category;
import com.shop.CategoryServiceRest.Model.Item;
import com.shop.CategoryServiceRest.Service.CategoryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final static Logger logger = LoggerFactory.getLogger(CategoryController.class);

    private CategoryService categoryService;

    @Autowired
    public void setCategoryService(CategoryService categoryService) {
        logger.info("Setting categoryService");
        this.categoryService = categoryService;
    }

    @ApiOperation(value = "Find parent categories")
    @GetMapping("/parents")
    public ResponseEntity<List<Category>> showParentCategories() {
        logger.info("Called showTopCategories method");
        List<Category> categories = categoryService.findByParentIsNull();

        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @ApiOperation(value = "Find all children by parent id")
    @GetMapping("/parents/{id}")
    public ResponseEntity<List<Category>> showCategoriesByParent(@PathVariable("id") Long id) {
        logger.info("Called showCategoriesByParent method");
        Category parent = showCategoryById(id).getBody();
        List<Category> categories = categoryService.findByParent(parent);

        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @ApiOperation(value = "Find category by id")
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

    @ApiOperation(value = "Find category by name")
    @GetMapping("/byName/{name}")
    public ResponseEntity<Category> showCategoryByName(@PathVariable("name") String name) {
        logger.info("Called showCategoryByName method");
        Category category = categoryService.findByName(name);

        if (category != null) {
            return new ResponseEntity<>(category, HttpStatus.OK);
        } else {
            logger.warn("Category with name - " + name + " not found");
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation(value = "Find all parent names")
    @GetMapping("/allRootNames")
    public ResponseEntity<List<String>> showAllRootNames() {
        logger.info("Called showAllRootNames method");
        List<String> names = new ArrayList<>(categoryService.getAllNamesOfRootCategories());

        return new ResponseEntity<>(names, HttpStatus.OK);
    }

    @ApiOperation(value = "Find all children names")
    @GetMapping("/allChildNames")
    public ResponseEntity<List<String>> showAllChildNames() {
        logger.info("Called showAllChildNames method");
        List<String> names = new ArrayList<>(categoryService.getAllNamesOfChildren());

        return new ResponseEntity<>(names, HttpStatus.OK);
    }

    @ApiOperation(value = "Find all items of certain category")
    @GetMapping(value = "/{id}/items", params = {"page", "size"})
    public ResponseEntity<List<Item>> showAllItemByCategory(@PathVariable("id") Long id,
                                                            @RequestParam("page") int page,
                                                            @RequestParam("size") int size) {
        logger.info("Called showAllItemsByCategory method");
        Category category = showCategoryById(id).getBody();
        Pageable pageable = PageRequest.of(page, size, Sort.by("name"));
        Page<Item> items = categoryService.getAllItemsByCategory(category, pageable);

        return new ResponseEntity<>(items.getContent(), HttpStatus.OK);
    }

    @ApiOperation(value = "Update exists category")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad request (invalid category information)")
    })
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

    @ApiOperation(value = "Create new category")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad request (invalid category information)")
    })
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

    @ApiOperation(value = "Delete category", notes = "Don't recommended to use")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Conflict (This category can contain items)")
    })
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
