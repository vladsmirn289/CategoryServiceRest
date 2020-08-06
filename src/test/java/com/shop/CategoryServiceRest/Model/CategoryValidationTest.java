package com.shop.CategoryServiceRest.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class CategoryValidationTest {
    private Validator validator;
    private Item item;
    private Category parent;
    private Category category;

    @BeforeEach
    void init() {
        LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
        localValidatorFactoryBean.afterPropertiesSet();
        this.validator = localValidatorFactoryBean;

        this.parent = new Category("Books");
        Category category = new Category("Book", parent);

        this.item = new Item("Spring 5", 50L, 1.592D, 3300D, "1234567");
        item.setDescription("..........");
        item.setCharacteristics("....");
        item.setCategory(category);

        category.setId(1L);
        category.setItems(Collections.singleton(item));
        category.setParent(parent);
        this.category = category;
    }

    @Test
    void shouldValidateWithCorrectName() {
        Set<ConstraintViolation<Category>> constraintViolations = validator.validate(category);
        assertThat(constraintViolations).hasSize(0);
    }

    @Test
    void shouldNotValidateWithBlankName() {
        category.setName("   ");

        Set<ConstraintViolation<Category>> constraintViolations = validator.validate(category);
        assertThat(constraintViolations).hasSize(1);

        ConstraintViolation<Category> violation = constraintViolations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("name");
        assertThat(violation.getMessage()).isEqualTo("Имя категории не может быть пустым");
    }

    @Test
    public void getterTests() {
        assertThat(category.getId()).isEqualTo(1L);
        assertThat(category.getParent()).isEqualTo(parent);
        assertThat(category.getName()).isEqualTo("Book");
        assertThat(category.getItems()).isEqualTo(Collections.singleton(item));
    }
}
