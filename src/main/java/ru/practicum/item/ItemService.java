package ru.practicum.item;

import java.util.List;
import java.util.Set;

public interface ItemService {
    List<ItemDto> getItems(long userId);

    List<ItemDto> getItems(long userId, Set<String> tags);

    ItemDto addNewItem(long userId, ItemDto itemDto);

    void deleteItem(long userId, long itemId);

    List<ItemDto> searchItems(GetItemRequest request);

    ItemDto updateItem(Long userId, ModifyItemRequest request);

    void deleteItem(Long userId, Long itemId);
}
