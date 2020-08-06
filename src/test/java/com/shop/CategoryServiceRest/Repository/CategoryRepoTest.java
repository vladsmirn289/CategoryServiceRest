package com.shop.CategoryServiceRest.Repository;

import com.shop.CategoryServiceRest.Model.Category;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CategoryRepoTest {
    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    public void init() {
        Category books = new Category("Books");
        Category laptops = new Category("Laptops");

        Category book1 = new Category("book1", books);
        Category book2 = new Category("book2", books);

        categoryRepo.save(books);
        categoryRepo.save(laptops);
        categoryRepo.save(book1);
        categoryRepo.save(book2);
    }

    @AfterEach
    public void resetSequence() {
        entityManager.getEntityManager()
                .createNativeQuery("alter sequence hibernate_sequence restart 1")
                .executeUpdate();
    }

    @Test
    public void shouldFindCategoryWithCorrectId() {
        Category category = categoryRepo.findById(1L).orElse(null);

        assertThat(category).isNotNull();
        assertThat(category.getName()).isEqualTo("Books");
        assertThat(category.getParent()).isNull();

        Category categoryWithParent = categoryRepo.findById(3L).orElse(null);

        assertThat(categoryWithParent).isNotNull();
        assertThat(categoryWithParent.getName()).isEqualTo("book1");
        assertThat(categoryWithParent.getParent()).isEqualTo(category);
    }

    @Test
    public void shouldNotFindCategoryWithIncorrectId() {
        Category category = categoryRepo.findById(10L).orElse(null);

        assertThat(category).isNull();
    }

    @Test
    public void shouldFindCategoriesByParentIsNull() {
        List<Category> categories = categoryRepo.findByParentIsNull();

        assertThat(categories).isNotEmpty();
        assertThat(categories.size()).isEqualTo(2);
        assertThat(categories.get(0).getName()).isEqualTo("Books");
        assertThat(categories.get(1).getName()).isEqualTo("Laptops");
    }

    @Test
    public void shouldFindCategoryByParent() {
        Category parentBooks = categoryRepo.findById(1L).orElse(null);
        Category parentLaptops = categoryRepo.findById(3L).orElse(null);

        List<Category> books = categoryRepo.findByParent(parentBooks);
        List<Category> laptops = categoryRepo.findByParent(parentLaptops);

        assertThat(books.size()).isEqualTo(2);
        assertThat(laptops.size()).isEqualTo(0);

        assertThat(books.get(0).getName()).isEqualTo("book1");
        assertThat(books.get(1).getName()).isEqualTo("book2");
    }

    @Test
    public void shouldFindCategoryByName() {
        Category category = categoryRepo.findByName("book1");

        assertThat(category).isNotNull();
        assertThat(category.getParent()).isEqualTo(categoryRepo.findById(1L).orElse(null));
    }

    @Test
    public void shouldSaveCategory() {
        Category category = new Category("category");
        categoryRepo.save(category);

        Category saved = categoryRepo.findById(5L).orElse(null);

        assertThat(saved).isNotNull();
        assertThat(saved.getName()).isEqualTo("category");
        assertThat(saved.getParent()).isNull();
    }

    @Test
    public void shouldDeleteCategory() {
        Category category = categoryRepo.findById(2L).orElse(null);
        assertThat(category).isNotNull();
        categoryRepo.delete(category);

        assertThat(categoryRepo.findAll().size()).isEqualTo(3);
    }
}
