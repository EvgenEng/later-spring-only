package ru.practicum.item;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;
import ru.practicum.user.UserRepository;
import ru.practicum.item.UrlMetadataRetriever;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserRepository userRepository;
    private final UrlMetadataRetriever urlMetadataRetriever;

    @Override
    @Transactional
    public ItemDto addNewItem(long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Проверяем, существует ли уже такой resolvedUrl
        UrlMetadataRetriever.UrlMetadata metadata = urlMetadataRetriever.retrieve(itemDto.getUrl());
        Optional<Item> existingItemOpt = repository.findByResolvedUrl(metadata.getResolvedUrl());

        if (existingItemOpt.isPresent()) {
            // Если ссылка уже существует, обновляем только теги
            Item existingItem = existingItemOpt.get();
            existingItem.getTags().addAll(itemDto.getTags());
            repository.save(existingItem);
            return ItemMapper.mapToItemDto(existingItem);
        }

        // Создаем новую запись
        Item item = ItemMapper.mapToItem(itemDto, user);
        item.setResolvedUrl(metadata.getResolvedUrl());
        item.setMimeType(metadata.getMimeType());
        item.setTitle(metadata.getTitle());
        item.setHasImage(metadata.isHasImage());
        item.setHasVideo(metadata.isHasVideo());
        item.setDateResolved(metadata.getDateResolved());

        Item savedItem = repository.save(item);
        return ItemMapper.mapToItemDto(savedItem);
    }

    @Override
    public List<ItemDto> getItems(long userId) {
        List<Item> userItems = repository.findByUserId(userId);
        return ItemMapper.mapToItemDto(userItems);
    }

    @Override
    public List<ItemDto> getItems(long userId, Set<String> tags) {
        BooleanExpression byUserId = QItem.item.user.id.eq(userId);
        BooleanExpression byAnyTag = QItem.item.tags.any().in(tags);
        Iterable<Item> foundItems = repository.findAll(byUserId.and(byAnyTag));
        return ItemMapper.mapToItemDto(foundItems);
    }

    @Override
    @Transactional
    public void deleteItem(long userId, long itemId) {
        repository.deleteByUserIdAndId(userId, itemId);
    }

    @Override
    public List<ItemDto> searchItems(GetItemRequest request) {
        BooleanExpression byUserId = QItem.item.user.id.eq(request.getUserId());

        // Фильтр по состоянию (прочитано/не прочитано)
        BooleanExpression byState = request.getState() == GetItemRequest.ItemState.ALL ?
                null : QItem.item.unread.eq(request.getState() == GetItemRequest.ItemState.UNREAD);

        // Фильтр по типу контента
        BooleanExpression byContentType = request.getContentType() == GetItemRequest.ContentType.ALL ?
                null : QItem.item.mimeType.eq(request.getContentType().name().toLowerCase());

        // Фильтр по тегам
        BooleanExpression byTags = request.getTags() == null || request.getTags().isEmpty() ?
                null : QItem.item.tags.any().in(request.getTags());

        // Комбинируем условия
        BooleanExpression finalExpression = byUserId;
        if (byState != null) finalExpression = finalExpression.and(byState);
        if (byContentType != null) finalExpression = finalExpression.and(byContentType);
        if (byTags != null) finalExpression = finalExpression.and(byTags);

        // Сортировка
        OrderSpecifier<?> orderSpecifier;
        switch (request.getSort()) {
            case OLDEST:
                orderSpecifier = QItem.item.dateResolved.asc();
                break;
            case TITLE:
                orderSpecifier = QItem.item.title.asc();
                break;
            case NEWEST:
            default:
                orderSpecifier = QItem.item.dateResolved.desc();
        }

        // Выполняем запрос с пагинацией
        List<Item> items = new ArrayList<>();
        repository.findAll(finalExpression, orderSpecifier).forEach(items::add);

        // Применяем лимит
        if (items.size() > request.getLimit()) {
            items = items.subList(0, request.getLimit());
        }

        return ItemMapper.mapToItemDto(items);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long userId, ModifyItemRequest request) {
        // 1. Проверяем, что ссылка принадлежит пользователю
        Item item = (Item) repository.findByIdAndUserId(request.getItemId(), userId)
                .orElseThrow(() -> new RuntimeException("Item not found or access denied"));

        // 2. Обновляем поля (если они указаны в запросе)
        if (request.getUrl() != null) {
            UrlMetadataRetriever.UrlMetadata metadata = urlMetadataRetriever.retrieve(request.getUrl());
            item.setUrl(request.getUrl());
            item.setResolvedUrl(metadata.getResolvedUrl());
            item.setTitle(metadata.getTitle());
            // ... другие метаданные
        }
        if (request.getTags() != null) {
            item.setTags(request.getTags());
        }
        if (request.getUnread() != null) {
            item.setUnread(request.getUnread());
        }

        // 3. Сохраняем изменения
        Item updatedItem = repository.save(item);
        return ItemMapper.mapToItemDto(updatedItem);
    }

    @Override
    @Transactional
    public void deleteItem(Long userId, Long itemId) {
        // Проверяем принадлежность ссылки пользователю
        if (!repository.existsByIdAndUserId(itemId, userId)) {
            throw new RuntimeException("Item not found or access denied");
        }
        repository.deleteById(itemId);
    }
}
