package com.tickets.tickets.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PageImplDeserializer extends JsonDeserializer<PageImpl<?>> {

    @Override
    public PageImpl<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode rootNode = mapper.readTree(p);
        JsonNode contentNode = rootNode.get("content");
        JsonNode pageableNode = rootNode.get("pageable");
        JsonNode totalElementsNode = rootNode.get("totalElements");
        List<?> content = mapper.convertValue(contentNode,
                mapper.getTypeFactory().constructCollectionType(ArrayList.class, Object.class));
        int pageNumber = pageableNode.get("pageNumber").asInt();
        int pageSize = pageableNode.get("pageSize").asInt();
        long totalElements = totalElementsNode.asLong();
        return new PageImpl<>(content, PageRequest.of(pageNumber, pageSize), totalElements);
    }
}