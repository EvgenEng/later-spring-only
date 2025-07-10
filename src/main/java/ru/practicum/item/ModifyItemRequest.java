package ru.practicum.item;

import lombok.Data;
import java.util.Set;

@Data
public class ModifyItemRequest {
    private Long itemId;       // ID ссылки для обновления
    private String url;        // Новая URL (опционально)
    private Set<String> tags;  // Новые теги (опционально)
    private Boolean unread;    // Новый статус (прочитано/не прочитано)
}
