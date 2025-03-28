package fr.cleanarchitecture.easyteach.course.domain.enums;

import fr.cleanarchitecture.easyteach.shared.domain.exceptions.BadRequestException;

public enum ResourceType {
    TEXT, VIDEOS, DOCUMENTS, AUDIOS, IMAGES;

    public static ResourceType getResourceType(String resourceType) {
        return switch (resourceType) {
            case "TEXT"-> ResourceType.TEXT;
            case "VIDEOS"-> ResourceType.VIDEOS;
            case "IMAGES" -> ResourceType.IMAGES;
            case "AUDIOS" -> ResourceType.AUDIOS;
            case "DOCUMENTS" -> ResourceType.DOCUMENTS;
            default -> throw new BadRequestException("Resource type not recognized");
        };
    }
}
