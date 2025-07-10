package ru.practicum.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetItemRequest {
    private long userId;
    private ItemState state;
    private ContentType contentType;
    private SortOrder sort;
    private int limit;
    private List<String> tags;

    public enum ItemState {
        ALL, UNREAD, READ
    }

    public enum ContentType {
        ALL, ARTICLE, IMAGE, VIDEO
    }

    public enum SortOrder {
        NEWEST, OLDEST, TITLE
    }
}
