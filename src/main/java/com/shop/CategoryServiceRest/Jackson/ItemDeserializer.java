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
import java.time.LocalDateTime;

public class ItemDeserializer extends StdDeserializer<Item> {
    public ItemDeserializer() {
        this(null);
    }

    protected ItemDeserializer(Class<Item> vc) {
        super(vc);
    }

    @Override
    public Item deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        String name = node.get("name").asText();
        Long count = node.get("count").asLong();
        Double weight = node.get("weight").asDouble();
        Double price = node.get("price").asDouble();
        String description = node.get("description").asText();
        String characteristics = node.get("characteristics").asText();
        String image = node.get("image").asText();
        String code = node.get("code").asText();
        Category category = objectMapper.treeToValue(node.get("category"), Category.class);
        String createdOnString = node.get("createdOn").asText();
        LocalDateTime createdOn = LocalDateTime.parse(createdOnString);

        Item item = new Item(name, count, weight, price, code);
        item.setDescription(description);
        item.setCharacteristics(characteristics);
        item.setImage(image);
        item.setCategory(category);
        item.setCreatedOn(createdOn);

        if (node.hasNonNull("id")) {
            item.setId(node.get("id").asLong());
        }

        return item;
    }
}
