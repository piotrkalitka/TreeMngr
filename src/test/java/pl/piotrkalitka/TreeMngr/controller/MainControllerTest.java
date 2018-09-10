package pl.piotrkalitka.TreeMngr.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Random;

import pl.piotrkalitka.TreeMngr.model.Item;
import pl.piotrkalitka.TreeMngr.payload.AddItemRequestBody;
import pl.piotrkalitka.TreeMngr.payload.CopyItemRequestBody;
import pl.piotrkalitka.TreeMngr.payload.UpdateItemRequestBody;
import pl.piotrkalitka.TreeMngr.service.ItemsService;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class MainControllerTest {

    @Mock
    private ItemsService itemsService;

    @InjectMocks
    private MainController mainController;

    private MockMvc mockMvc;
    private final static String CONTENT_TYPE = "application/json";

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(mainController).build();
    }

    @Test
    public void getItems() throws Exception {
        Item subItem1 = new Item();
        Item subItem2 = new Item();
        Item subItem3 = new Item();
        Item item = new Item();
        item.getChildren().add(subItem1);
        item.getChildren().add(subItem2);
        item.getChildren().add(subItem3);

        when(itemsService.getItems()).thenReturn(item);

        mockMvc
                .perform(get("/api/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("children", hasSize(3)));
    }

    @Test
    public void addFirstItem() throws Exception {
        Integer value = new Random().nextInt();
        Item item = new Item();
        item.setValue(value);

        AddItemRequestBody requestBody = new AddItemRequestBody(value);

        when(itemsService.addItem(value)).thenReturn(item);

        mockMvc
                .perform(post("/api/")
                        .contentType(CONTENT_TYPE)
                        .content(jsonOf(requestBody)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("value", is(value)));
    }

    @Test
    public void addItem() throws Exception {
        Long parentId = new Random().nextLong();
        Integer subItemValue = new Random().nextInt();
        Integer subItemLevel = new Random().nextInt();
        Item subItem = new Item();
        subItem.setParentId(parentId);
        subItem.setValue(subItemValue);
        subItem.setLevel(subItemLevel);

        when(itemsService.addItem(subItemValue, parentId)).thenReturn(subItem);

        AddItemRequestBody requestBody = new AddItemRequestBody(subItemValue);

        mockMvc
                .perform(post("/api/" + parentId)
                        .contentType(CONTENT_TYPE)
                        .content(jsonOf(requestBody)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("parentId", is(parentId)))
                .andExpect(jsonPath("value", is(subItemValue)))
                .andExpect(jsonPath("level", is(subItemLevel)));

    }

    @Test
    public void removeItem() throws Exception {
        Long itemId = new Random().nextLong();

        mockMvc.perform(delete("/api/" + itemId))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void copyItem() throws Exception {
        Long targetId = new Random().nextLong();
        Long sourceId = new Random().nextLong();
        Integer sourceValue = new Random().nextInt();
        Integer sourceLevel = new Random().nextInt();

        Item sourceItem = new Item();
        sourceItem.setId(sourceId);
        sourceItem.setValue(sourceValue);
        sourceItem.setLevel(sourceLevel);

        Item targetItem = new Item();
        targetItem.setId(targetId);
        targetItem.setValue(sourceItem.getValue());
        targetItem.setLevel(sourceItem.getLevel());

        when(itemsService.copyItem(sourceId, targetId)).thenReturn(targetItem);

        CopyItemRequestBody requestBody = new CopyItemRequestBody(sourceId);

        mockMvc
                .perform(post("/api/" + targetId + "/copy")
                        .contentType(CONTENT_TYPE)
                        .content(jsonOf(requestBody)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("value", is(sourceValue)))
                .andExpect(jsonPath("level", is(sourceLevel)));
    }

    @Test
    public void updateItem() throws Exception {

        Long itemId = new Random().nextLong();
        Long newParentId = new Random().nextLong();
        Integer newValue = new Random().nextInt();

        UpdateItemRequestBody requestBody = new UpdateItemRequestBody(newParentId, newValue);

        Item updatedItem = new Item();
        updatedItem.setParentId(newParentId);
        updatedItem.setValue(newValue);

        when(itemsService.updateItem(itemId, newValue, newParentId)).thenReturn(updatedItem);

        mockMvc
                .perform(patch("/api/" + itemId)
                        .contentType(CONTENT_TYPE)
                        .content(jsonOf(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("parentId", is(newParentId)))
                .andExpect(jsonPath("value", is(newValue)));
    }


    private static String jsonOf(Object pojo) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(pojo);
    }
}