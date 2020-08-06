package com.shop.CategoryServiceRest.Controller;

import com.shop.CategoryServiceRest.Model.Category;
import com.shop.CategoryServiceRest.Model.Item;
import com.shop.CategoryServiceRest.Service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@PropertySource(value = "classpath:application.properties")
@Sql(value = {
        "classpath:db/PostgreSQL/after-test.sql",
        "classpath:db/PostgreSQL/category-test.sql",
        "classpath:db/PostgreSQL/item-test.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class CategoryRestTest {
    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    public void init() {
        cacheManager.getCache("categories").clear();
    }

    @Test
    public void shouldShowParentCategories() {
        Category first = categoryService.findById(1L);
        Category second = categoryService.findById(4L);

        ResponseEntity<List<Category>> responseCategories =
                restTemplate.exchange(
                        "http://localhost:9004/api/categories/parents",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<Category>>(){});

        assertThat(responseCategories.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseCategories.getBody()).isNotNull();

        List<Category> categories = responseCategories.getBody();
        assertThat(categories).contains(first, second);
    }

    @Test
    public void shouldShowCategoriesByParent() {
        Category first = categoryService.findById(2L);
        Category second = categoryService.findById(3L);

        ResponseEntity<List<Category>> responseCategories =
                restTemplate.exchange(
                        "http://localhost:9004/api/categories/parents/1",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<Category>>(){});

        assertThat(responseCategories.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseCategories.getBody()).isNotNull();

        List<Category> categories = responseCategories.getBody();
        assertThat(categories).contains(first, second);
    }

    @Test
    public void shouldShowCategoryById() {
        ResponseEntity<Category> responseCategories =
                restTemplate.exchange(
                        "http://localhost:9004/api/categories/3",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<Category>(){});

        assertThat(responseCategories.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseCategories.getBody()).isNotNull();

        Category category = responseCategories.getBody();
        assertThat(category.getId()).isEqualTo(3L);
        assertThat(category.getName()).isEqualTo("Программирование");
    }

    @Test
    public void shouldNotFoundWhenTryToFindCategoryByInvalidId() {
        ResponseEntity<Category> responseCategories =
                restTemplate.exchange(
                        "http://localhost:9004/api/categories/100",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<Category>(){});

        assertThat(responseCategories.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseCategories.getBody()).isNull();
    }

    @Test
    public void shouldShowCategoryByName() {
        ResponseEntity<Category> responseCategories =
                restTemplate.exchange(
                        "http://localhost:9004/api/categories?name=Программирование",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<Category>(){});

        assertThat(responseCategories.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseCategories.getBody()).isNotNull();

        Category category = responseCategories.getBody();
        assertThat(category.getId()).isEqualTo(3L);
        assertThat(category.getName()).isEqualTo("Программирование");
    }

    @Test
    public void shouldNotFoundWhenTryToFindCategoryByInvalidName() {
        ResponseEntity<Category> responseCategories =
                restTemplate.exchange(
                        "http://localhost:9004/api/categories?name=hello+world",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<Category>(){});

        assertThat(responseCategories.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseCategories.getBody()).isNull();
    }

    @Test
    public void shouldShowAllRootNames() {
        ResponseEntity<List<String>> responseNames =
                restTemplate.exchange(
                        "http://localhost:9004/api/categories/allRootNames",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<String>>(){});

        assertThat(responseNames.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseNames.getBody()).isNotNull();

        List<String> names = responseNames.getBody();
        assertThat(names.size()).isEqualTo(2);
        assertThat(names).contains("Книги", "Электроника");
    }

    @Test
    public void shouldShowAllChildNames() {
        ResponseEntity<List<String>> responseNames =
                restTemplate.exchange(
                        "http://localhost:9004/api/categories/allChildNames",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<String>>(){});

        assertThat(responseNames.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseNames.getBody()).isNotNull();

        List<String> names = responseNames.getBody();
        assertThat(names.size()).isEqualTo(3);
        assertThat(names).contains("Научная литература", "Программирование", "Компьютеры");
    }

    @Test
    public void shouldShowAllItemByCategory() {
        ResponseEntity<List<Item>> responseItems =
                restTemplate.exchange(
                        "http://localhost:9004/api/categories/3/items",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<Item>>(){});

        assertThat(responseItems.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseItems.getBody()).isNotNull();

        List<Item> items = responseItems.getBody();
        assertThat(items.size()).isEqualTo(2);
    }

    @Test
    public void shouldSuccessfulUpdateCategory() {
        Category category = categoryService.findById(3L);
        category.setName("Hello world");

        ResponseEntity<Category> responseCategories =
                restTemplate.exchange(
                        "http://localhost:9004/api/categories/3",
                        HttpMethod.PUT,
                        new HttpEntity<>(category),
                        Category.class);

        assertThat(responseCategories.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseCategories.getBody()).isNotNull();

        Category first = responseCategories.getBody();
        assertThat(first.getId()).isEqualTo(3);
        assertThat(first.getName()).isEqualTo("Hello world");
    }

    @Test
    public void shouldBadRequestWhenTryToUpdateCategoryWithInvalidData() {
        Category category = categoryService.findById(3L);
        category.setName("");

        ResponseEntity<Category> responseCategories =
                restTemplate.exchange(
                        "http://localhost:9004/api/categories/3",
                        HttpMethod.PUT,
                        new HttpEntity<>(category),
                        Category.class);

        assertThat(responseCategories.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseCategories.getBody()).isNotNull();

        Category first = responseCategories.getBody();
        assertThat(first.getId()).isEqualTo(3);
        assertThat(first.getName()).isEqualTo("");
    }

    @Test
    public void shouldSuccessCreateNewCategory() {
        Category category = new Category("New category");
        Category parent = categoryService.findById(1L);
        category.setParent(parent);

        ResponseEntity<Category> responseCategories =
                restTemplate.exchange(
                        "http://localhost:9004/api/categories",
                        HttpMethod.POST,
                        new HttpEntity<>(category),
                        Category.class);

        assertThat(responseCategories.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseCategories.getBody()).isNotNull();

        Category first = responseCategories.getBody();
        assertThat(first.getId()).isEqualTo(100);
        assertThat(first.getName()).isEqualTo("New category");
        assertThat(first.getParent()).isEqualTo(parent);
    }

    @Test
    public void shouldBadRequestWhenTryToCreateNewCategoryWithInvalidData() {
        Category category = new Category("");
        category.setParent(null);

        ResponseEntity<Category> responseCategories =
                restTemplate.exchange(
                        "http://localhost:9004/api/categories",
                        HttpMethod.POST,
                        new HttpEntity<>(category),
                        Category.class);

        assertThat(responseCategories.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseCategories.getBody()).isNotNull();

        Category first = responseCategories.getBody();
        assertThat(first.getId()).isNull();
        assertThat(first.getName()).isEqualTo("");
        assertThat(first.getParent()).isNull();
    }

    @Test
    public void shouldSuccessfulDeleteCategory() {
        restTemplate.exchange(
                "http://localhost:9004/api/categories/3",
                HttpMethod.DELETE,
                null,
                Object.class);

        assertThrows(NoSuchElementException.class, () -> categoryService.findById(3L));
    }
}
