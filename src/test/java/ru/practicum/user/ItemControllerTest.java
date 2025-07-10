package ru.practicum.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.item.ItemController;
import ru.practicum.item.ItemDto;
import ru.practicum.item.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();
    }

    @Test
    void getItems_ShouldReturnItems() throws Exception {
        when(itemService.getItems(anyLong(), any())).thenReturn(List.of(new ItemDto()));

        mockMvc.perform(get("/items")
                        .header("X-Later-User-Id", 1L)
                        .param("state", "unread"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void addItem_ShouldReturnCreatedItem() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);

        when(itemService.addNewItem(anyLong(), any(ItemDto.class))).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Later-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"https://example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }
}
