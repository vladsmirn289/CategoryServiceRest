package com.shop.CategoryServiceRest.Jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.shop.CategoryServiceRest.Model.Category;
import com.shop.CategoryServiceRest.Model.Item;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class CategoryDeserializer extends StdDeserializer<Category> {
    public CategoryDeserializer() {
        this(null);
    }

    protected CategoryDeserializer(Class<Category> vc) {
        super(vc);
    }

    @Override
    public Category deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        String name = node.get("name").asText();
        Iterator<JsonNode> itemsNode = node.get("items").elements();

        Category category = new Category(name);

        if (node.hasNonNull("id")) {
            category.setId(node.get("id").asLong());
        }

        if (node.hasNonNull("parent")) {
            category.setParent(mapper.treeToValue(node.get("parent"), Category.class));
        }

        Set<Item> items = new HashSet<>();
        while (itemsNode.hasNext()) {
            items.add(mapper.treeToValue(itemsNode.next(), Item.class));
        }
        category.setItems(items);

        return category;
    }
}
