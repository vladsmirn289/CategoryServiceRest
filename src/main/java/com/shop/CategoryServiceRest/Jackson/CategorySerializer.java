package com.shop.CategoryServiceRest.Jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.shop.CategoryServiceRest.Model.Category;
import com.shop.CategoryServiceRest.Model.Item;
import com.shop.CategoryServiceRest.Service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
public class CategorySerializer extends StdSerializer<Category> {
    private CategoryService categoryService;

    @Autowired
    public void setCategoryService(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    public CategorySerializer() {
        this(null);
    }

    protected CategorySerializer(Class<Category> t) {
        super(t);
    }

    @Override
    public void serialize(Category category, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();

        if (category.getId() == null) {
            jsonGenerator.writeNullField("id");
        } else {
            jsonGenerator.writeNumberField("id", category.getId());
        }

        if (category.getParent() != null) {
            jsonGenerator.writeObjectField("parent", category.getParent());
        } else {
            jsonGenerator.writeNullField("parent");
        }
        jsonGenerator.writeStringField("name", category.getName());

        jsonGenerator.writeArrayFieldStart("items");

        Set<Item> items = categoryService.getAllItemsByCategory(category);
        for (Item i : items) {
            jsonGenerator.writeObject(i);
        }

        jsonGenerator.writeEndArray();

        jsonGenerator.writeEndObject();
    }
}
