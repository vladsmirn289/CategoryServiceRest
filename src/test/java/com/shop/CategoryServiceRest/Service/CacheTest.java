package com.shop.CategoryServiceRest.Service;

import com.shop.CategoryServiceRest.Model.Category;
import com.shop.CategoryServiceRest.Model.Item;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@PropertySource(value = "classpath:application.properties")
@Sql(value = {
        "classpath:db/PostgreSQL/after-test.sql",
        "classpath:db/PostgreSQL/category-test.sql",
        "classpath:db/PostgreSQL/item-test.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class CacheTest {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CacheManager cacheManager;

    @Test
    public void findByParentCacheTest() {
        Category parent1 = categoryService.findById(1L);
        Category parent2 = categoryService.findById(4L);

        categoryService.findByParent(parent1);
        categoryService.findByParent(parent2);

        Cache cache = cacheManager.getCache("categories");
        assertThat(cache).isNotNull();

        List<Category> categories1 = cache.get(parent1, ArrayList.class);
        List<Category> categories2 = cache.get(parent2, ArrayList.class);

        assertThat(categories1).isNotNull();
        assertThat(categories2).isNotNull();
        assertThat(categories1.size()).isEqualTo(2);
        assertThat(categories2.size()).isEqualTo(1);
    }

    @Test
    public void findByIdCacheTest() {
        categoryService.findById(2L);
        categoryService.findById(3L);

        Cache cache = cacheManager.getCache("categories");
        assertThat(cache).isNotNull();

        Category category1 = cache.get(2L, Category.class);
        Category category2 = cache.get(3L, Category.class);

        assertThat(category1).isNotNull();
        assertThat(category2).isNotNull();
    }

    @Test
    public void findByNameCacheTest() {
        categoryService.findByName("Научная литература");
        categoryService.findByName("Программирование");

        Cache cache = cacheManager.getCache("categories");
        assertThat(cache).isNotNull();

        Category category1 = cache.get("Научная литература", Category.class);
        Category category2 = cache.get("Программирование", Category.class);

        assertThat(category1).isNotNull();
        assertThat(category2).isNotNull();
    }

    @Test
    @Transactional
    public void getAllItemsByCategoryCacheTest() {
        Category category1 = categoryService.findById(2L);
        Pageable pageable = PageRequest.of(0, 5, Sort.by("name"));

        categoryService.getAllItemsByCategory(category1, pageable);

        Cache cache = cacheManager.getCache("pagination");
        assertThat(cache).isNotNull();

        Set<Item> items1 = cache.get(pageable, Set.class);

        assertThat(items1).isNotNull();
        assertThat(items1.size()).isEqualTo(2);
    }

    @Test
    @Transactional
    public void deleteCachingTest() {
        Cache cache = cacheManager.getCache("categories");
        assertThat(cache).isNotNull();

        categoryService.findByName("Программирование");
        Category toDelete = categoryService.findById(3L);
        assertThat(cache.get("Программирование", Category.class)).isNotNull();
        assertThat(cache.get(3L, Category.class)).isNotNull();

        categoryService.delete(toDelete);

        assertThat(cache.get("Программирование", Category.class)).isNull();
        assertThat(cache.get(3L, Category.class)).isNull();

        ///////////////////////////////////////////////

        Category toDelete2 = categoryService.findById(2L);
        Pageable pageable = PageRequest.of(0, 5, Sort.by("name"));
        categoryService.getAllItemsByCategory(toDelete2, pageable);
        assertThat(cache.get(toDelete2, Set.class)).isNotNull();

        categoryService.delete(toDelete2);

        assertThat(cache.get(toDelete2, Set.class)).isNull();
    }
}
