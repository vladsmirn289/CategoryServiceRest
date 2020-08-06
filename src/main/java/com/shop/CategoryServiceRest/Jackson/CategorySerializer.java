package com.shop.CategoryServiceRest.Jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.shop.CategoryServiceRest.Model.Category;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CategorySerializer extends StdSerializer<Category> {
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

        jsonGenerator.writeEndObject();
    }
}
