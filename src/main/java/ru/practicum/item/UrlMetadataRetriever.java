package ru.practicum.item;

import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.Instant;

@Component
public interface UrlMetadataRetriever {

    UrlMetadata retrieve(String urlString);

    interface UrlMetadata {

        String getNormalUrl();

        String getResolvedUrl();

        String getMimeType();

        String getTitle();

        boolean isHasImage();

        boolean isHasVideo();

        Instant getDateResolved();
    }
}
