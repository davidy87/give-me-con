package com.givemecon.util.image_entity.builder;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class ImageEntityBuilderFactory {

    private final Set<ImageEntityBuilder> imageEntityBuilderSet;

    private Map<String, ImageEntityBuilder> imageEntityBuilderMap;

    @PostConstruct
    public void init() {
        createImageEntityMap(imageEntityBuilderSet);
    }

    private void createImageEntityMap(Set<ImageEntityBuilder> imageEntityBuilderSet) {
        imageEntityBuilderMap = new HashMap<>();
        imageEntityBuilderSet.forEach(imageEntityBuilder ->
                imageEntityBuilderMap.put(imageEntityBuilder.getEntityName(), imageEntityBuilder)
        );
    }

    public ImageEntityBuilder findBy(String entityName) {
        return imageEntityBuilderMap.get(entityName);
    }
}
