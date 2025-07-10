package ru.practicum.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.user.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class ItemMapper {
    public static Item mapToItem(ItemDto itemDto, User user) {
        Item item = new Item();
        item.setUser(user);
        item.setUrl(itemDto.getUrl());
        item.setResolvedUrl(itemDto.getResolvedUrl());
        item.setMimeType(itemDto.getMimeType());
        item.setTitle(itemDto.getTitle());
        item.setHasImage(itemDto.isHasImage());
        item.setHasVideo(itemDto.isHasVideo());
        item.setUnread(itemDto.isUnread());
        item.setDateResolved(itemDto.getDateResolved());
        item.setTags(itemDto.getTags());
        return item;
    }

    public static ItemDto mapToItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getUser().getId(),
                item.getUrl(),
                item.getResolvedUrl(),
                item.getMimeType(),
                item.getTitle(),
                item.isHasImage(),
                item.isHasVideo(),
                item.isUnread(),
                item.getDateResolved(),
                new HashSet<>(item.getTags())
        );
    }

    public static List<ItemDto> mapToItemDto(Iterable<Item> items) {
        List<ItemDto> dtos = new ArrayList<>();
        for (Item item : items) {
            dtos.add(mapToItemDto(item));
        }
        return dtos;
    }
}
