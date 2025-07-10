package ru.practicum.item;

import lombok.Builder;
import lombok.Value;
import org.springframework.http.MediaType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpHeaders;
import java.time.Instant;

@Service
public class UrlMetadataRetrieverImpl implements UrlMetadataRetriever {
    private final HttpClient httpClient;

    public UrlMetadataRetrieverImpl() {
        this.httpClient = HttpClient.newHttpClient();
    }

    @Override
    public UrlMetadata retrieve(String urlString) {
        final URI uri;
        try {
            uri = new URI(urlString);
        } catch (URISyntaxException e) {
            throw new ItemRetrieverException("The URL is malformed: " + urlString, e);
        }

        HttpResponse<Void> resp = connect(uri, "HEAD", HttpResponse.BodyHandlers.discarding());

        String contentType = resp.headers()
                .firstValue("Content-Type")
                .orElse("*");

        MediaType mediaType = MediaType.parseMediaType(contentType);

        final UrlMetadataImpl result;

        if (mediaType.getType().equals("text")) {
            result = handleText(resp.uri());
        } else if (mediaType.getType().equals("image")) {
            result = handleImage(resp.uri());
        } else if (mediaType.getType().equals("video")) {
            result = handleVideo(resp.uri());
        } else {
            throw new ItemRetrieverException("The content type [" + mediaType
                    + "] at the specified URL is not supported.");
        }

        return result.toBuilder()
                .normalUrl(urlString)
                .resolvedUrl(resp.uri().toString())
                .mimeType(mediaType.getType())
                .dateResolved(Instant.now())
                .build();
    }

    private <T> HttpResponse<T> connect(URI url, String method, HttpResponse.BodyHandler<T> responseBodyHandler) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .method(method, HttpRequest.BodyPublishers.noBody())
                .build();

        try {
            return httpClient.send(request, responseBodyHandler);
        } catch (InterruptedException e) {
            // Восстанавливаем прерванное состояние
            Thread.currentThread().interrupt();
            throw new ItemRetrieverException("Thread was interrupted while retrieving URL: " + url, e);
        } catch (Exception e) {
            throw new ItemRetrieverException("Failed to retrieve URL: " + url, e);
        }
    }

    private UrlMetadataImpl handleText(URI url) {
        HttpResponse<String> resp = connect(url, "GET", HttpResponse.BodyHandlers.ofString());
        Document doc = Jsoup.parse(resp.body());
        Elements imgElements = doc.getElementsByTag("img");
        Elements videoElements = doc.getElementsByTag("video");

        return UrlMetadataImpl.builder()
                .title(doc.title())
                .hasImage(!imgElements.isEmpty())
                .hasVideo(!videoElements.isEmpty())
                .build();
    }

    private UrlMetadataImpl handleVideo(URI url) {
        String name = new File(url.getPath()).getName();
        return UrlMetadataImpl.builder()
                .title(name)
                .hasVideo(true)
                .build();
    }

    private UrlMetadataImpl handleImage(URI url) {
        String name = new File(url.getPath()).getName();
        return UrlMetadataImpl.builder()
                .title(name)
                .hasImage(true)
                .build();
    }

    @Value
    @Builder(toBuilder = true)
    static class UrlMetadataImpl implements UrlMetadata {
        String normalUrl;
        String resolvedUrl;
        String mimeType;
        String title;
        boolean hasImage;
        boolean hasVideo;
        Instant dateResolved;
    }
}
