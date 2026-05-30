package be.loisirs.tfe2025.plateforme_loisirs.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class ImageStorageService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    private static final Map<String, Set<String>> ALLOWED_EXTENSIONS = Map.of(
            "image/jpeg", Set.of("jpg", "jpeg"),
            "image/png", Set.of("png"),
            "image/webp", Set.of("webp")
    );

    private static final Map<String, String> FILE_PREFIXES = Map.of(
            "products", "product",
            "activities", "activity",
            "members", "profile",
            "partners", "logo"
    );

    private final Path uploadRoot = Paths.get("uploads")
            .toAbsolutePath()
            .normalize();

    public String storeImage(MultipartFile file, String category, Long resourceId) {
        validateFile(file);
        validateCategory(category);

        if (resourceId == null || resourceId <= 0) {
            throw new IllegalArgumentException("Identifiant incorrect.");
        }

        String extension = getExtension(file.getOriginalFilename());
        validateExtension(file.getContentType(), extension);

        Path folder = uploadRoot
                .resolve(category)
                .resolve(resourceId.toString())
                .normalize();

        if (!folder.startsWith(uploadRoot)) {
            throw new IllegalArgumentException("Chemin de stockage incorrect.");
        }

        try {
            Files.createDirectories(folder);

            String filename = FILE_PREFIXES.get(category)
                    + "-"
                    + UUID.randomUUID()
                    + "."
                    + extension;

            Path target = folder.resolve(filename).normalize();

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, target);
            }

            return "/uploads/" + category + "/" + resourceId + "/" + filename;

        } catch (IOException e) {
            throw new IllegalStateException("Impossible d'enregistrer l'image.", e);
        }
    }

    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank() || imageUrl.contains("default-")) {
            return;
        }

        if (!imageUrl.startsWith("/uploads/")) {
            throw new IllegalArgumentException("URL d'image incorrecte.");
        }

        Path target = uploadRoot
                .resolve(imageUrl.substring("/uploads/".length()))
                .normalize();

        if (!target.startsWith(uploadRoot)) {
            throw new IllegalArgumentException("Chemin de suppression incorrect.");
        }

        try {
            Files.deleteIfExists(target);
        } catch (IOException e) {
            throw new IllegalStateException("Impossible de supprimer l'image.", e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Aucune image reçue.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("L'image dépasse la limite de 5 Mo.");
        }
    }

    private void validateCategory(String category) {
        if (!FILE_PREFIXES.containsKey(category)) {
            throw new IllegalArgumentException("Catégorie d'image incorrecte.");
        }
    }

    private void validateExtension(String contentType, String extension) {
        Set<String> acceptedExtensions = ALLOWED_EXTENSIONS.get(contentType);

        if (acceptedExtensions == null || !acceptedExtensions.contains(extension)) {
            throw new IllegalArgumentException(
                    "Formats acceptés : JPG, JPEG, PNG et WEBP."
            );
        }
    }

    private String getExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new IllegalArgumentException("Extension de fichier manquante.");
        }

        return originalFilename
                .substring(originalFilename.lastIndexOf('.') + 1)
                .toLowerCase();
    }
}