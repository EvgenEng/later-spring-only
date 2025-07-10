package ru.practicum.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> get(@RequestHeader("X-Later-User-Id") long userId,
                             @RequestParam(name = "tags", required = false) Set<String> tags) {
        if(tags == null || tags.isEmpty()) {
            return itemService.getItems(userId, tags);
        } else {
            return itemService.getItems(userId, tags);
        }
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(
            @RequestHeader("X-Later-User-Id") long userId,
            @RequestParam(name = "state", defaultValue = "UNREAD") GetItemRequest.ItemState state,
            @RequestParam(name = "contentType", defaultValue = "ALL") GetItemRequest.ContentType contentType,
            @RequestParam(name = "sort", defaultValue = "NEWEST") GetItemRequest.SortOrder sort,
            @RequestParam(name = "limit", defaultValue = "10") int limit,
            @RequestParam(name = "tags", required = false) Set<String> tags) {

        GetItemRequest request = new GetItemRequest(userId, state, contentType, sort, limit,
                tags != null ? new ArrayList<>(tags) : null);

        return itemService.searchItems(request);
    }

    @PostMapping
    public ItemDto add(@RequestHeader("X-Later-User-Id") Long userId,
                       @RequestBody ItemDto item) {
        return itemService.addNewItem(userId, item);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(
            @RequestHeader("X-Later-User-Id") Long userId,
            @PathVariable Long itemId
    ) {
        itemService.deleteItem(userId, itemId);
    }

    @PatchMapping("/update")
    public ItemDto updateItem(
            @RequestHeader("X-Later-User-Id") Long userId,
            @RequestBody ModifyItemRequest request
    ) {
        return itemService.updateItem(userId, request);
    }
}
