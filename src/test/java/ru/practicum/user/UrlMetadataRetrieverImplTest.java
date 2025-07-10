/*package ru.practicum.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import ru.practicum.item.ItemRetrieverException;
import ru.practicum.item.UrlMetadataRetriever;
import ru.practicum.item.UrlMetadataRetrieverImpl;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlMetadataRetrieverImplTest {

    @Mock
    private HttpClient httpClient;

    @InjectMocks
    private UrlMetadataRetrieverImpl urlMetadataRetriever;

    @Test
    void retrieve_ShouldReturnMetadata_ForTextContent() throws Exception {
        // Arrange
        HttpResponse<Void> headResponse = mock(HttpResponse.class);
        when(headResponse.statusCode()).thenReturn(HttpStatus.OK.value());
        when(headResponse.uri()).thenReturn(URI.create("https://example.com"));

        // Исправленный способ создания заголовков
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        when(headResponse.headers()).thenReturn(headers);

        HttpResponse<String> getResponse = mock(HttpResponse.class);
        when(getResponse.body()).thenReturn("<html><head><title>Test</title></head><body></body></html>");

        // Исправленный вызов thenReturn с правильной последовательностью
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(headResponse)
                .thenReturn(getResponse);

        // Act
        UrlMetadataRetriever.UrlMetadata metadata = urlMetadataRetriever.retrieve("https://example.com");

        // Assert
        assertNotNull(metadata);
        assertEquals("Test", metadata.getTitle());
        assertTrue(metadata.getMimeType().startsWith("text"));
        verify(httpClient, times(2)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @Test
    void retrieve_ShouldThrowException_ForInvalidUrl() {
        assertThrows(ItemRetrieverException.class,
                () -> urlMetadataRetriever.retrieve("invalid url"));
    }
}
*/