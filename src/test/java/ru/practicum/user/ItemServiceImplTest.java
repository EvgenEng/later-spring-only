package ru.practicum.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.item.Item;
import ru.practicum.item.ItemDto;
import ru.practicum.item.ItemRepository;
import ru.practicum.item.ItemServiceImpl;
import ru.practicum.item.UrlMetadataRetriever;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository; // Добавляем мок для UserRepository

    @Mock
    private UrlMetadataRetriever urlMetadataRetriever;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void addNewItem_ShouldSaveNewItem_WhenItemNotExists() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        ItemDto itemDto = new ItemDto();
        itemDto.setUrl("https://example.com");

        Item savedItem = new Item();
        savedItem.setId(1L);
        savedItem.setUser(user); // Устанавливаем пользователя

        // Настраиваем моки
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findByResolvedUrl(any())).thenReturn(Optional.empty());
        when(urlMetadataRetriever.retrieve(any())).thenReturn(mock(UrlMetadataRetriever.UrlMetadata.class));
        when(itemRepository.save(any())).thenReturn(savedItem);

        // Act
        ItemDto result = itemService.addNewItem(userId, itemDto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(itemRepository).save(any());

        // Дополнительная проверка, что пользователь установлен
        verify(itemRepository).save(argThat(item ->
                item.getUser() != null &&
                        item.getUser().getId().equals(userId)
        ));
    }

    @Test
    void addNewItem_ShouldUpdateTags_WhenItemExists() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        ItemDto itemDto = new ItemDto();
        itemDto.setUrl("https://example.com");
        itemDto.setTags(new HashSet<>(Set.of("new")));

        Item existingItem = new Item();
        existingItem.setId(1L);
        existingItem.setTags(new HashSet<>());
        existingItem.setUser(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findByResolvedUrl(any())).thenReturn(Optional.of(existingItem));
        when(urlMetadataRetriever.retrieve(any())).thenReturn(mock(UrlMetadataRetriever.UrlMetadata.class));

        // Act
        ItemDto result = itemService.addNewItem(userId, itemDto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertTrue(existingItem.getTags().contains("new"));

        // Изменяем проверку - теперь ожидаем, что save будет вызван 1 раз
        verify(itemRepository, times(1)).save(existingItem);
    }
}
