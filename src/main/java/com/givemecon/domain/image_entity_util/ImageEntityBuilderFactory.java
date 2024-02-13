package com.givemecon.domain.image_entity_util;

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

    private Map<String, ImageEntityBuilder> imageEntityMap;

    @PostConstruct
    public void init() {
        createImageEntityMap(imageEntityBuilderSet);
    }

    private void createImageEntityMap(Set<ImageEntityBuilder> imageEntityBuilderSet) {
        imageEntityMap = new HashMap<>();
        imageEntityBuilderSet.forEach(imageEntityBuilder ->
            imageEntityMap.put(imageEntityBuilder.getOwnerEntityName(), imageEntityBuilder)
        );
    }

    public ImageEntityBuilder findBy(String baseEntityName) {
        return imageEntityMap.get(baseEntityName);
    }
}
