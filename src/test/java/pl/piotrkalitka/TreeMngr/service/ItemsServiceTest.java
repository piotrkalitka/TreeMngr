package pl.piotrkalitka.TreeMngr.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Optional;
import java.util.Random;

import javax.swing.text.html.Option;

import ch.qos.logback.core.rolling.helper.IntegerTokenConverter;
import pl.piotrkalitka.TreeMngr.exception.FirstItemExistsException;
import pl.piotrkalitka.TreeMngr.exception.ItemNotFoundException;
import pl.piotrkalitka.TreeMngr.exception.UnprocessableEntityException;
import pl.piotrkalitka.TreeMngr.model.Item;
import pl.piotrkalitka.TreeMngr.repository.ItemsRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ItemsServiceTest {

    @Mock
    private ItemsRepository itemsRepository;

    @InjectMocks
    private ItemsService itemsService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getItems() {
        when(itemsRepository.findByParentId(null)).thenReturn(new Item());

        when(itemsRepository.existsByParentId(null)).thenReturn(false);
        assertThat(itemsService.getItems()).isNull();

        when(itemsRepository.existsByParentId(null)).thenReturn(true);
        assertThat(itemsService.getItems()).isNotNull();
    }

    @Test(expected = FirstItemExistsException.class)
    public void addFirstItem() {
        Long parentId = new Random().nextLong();

        Integer value = new Random().nextInt();
        Long id = new Random().nextLong();
        Item newItem = new Item();
        newItem.setValue(value);
        newItem.setSum(value);
        newItem.setId(id);
        newItem.setParentId(parentId);

        when(itemsRepository.count()).thenReturn(1L);
        itemsService.addItem(new Random().nextInt());

        when(itemsRepository.count()).thenReturn(0L);
        when(itemsRepository.save(newItem)).thenReturn(newItem);

        Item addedItem = itemsService.addItem(value, parentId);
        assertThat(addedItem.getId()).isNotNull();
        assertThat(addedItem.getValue()).isEqualTo(value);
        assertThat(addedItem.getLevel()).isEqualTo(0);
        assertThat(addedItem.getSum()).isEqualTo(value);
    }

    @Test(expected = ItemNotFoundException.class)
    public void addItem() {
        Long parentId = new Random().nextLong();
        Integer parentValue = new Random().nextInt();
        Integer parentLevel = new Random().nextInt();
        Item parentItem = new Item(null, parentLevel, parentId, parentValue, null, new HashSet<>());

        Integer childValue = new Random().nextInt();

        when(itemsRepository.existsByParentId(parentId)).thenReturn(false);
        itemsService.addItem(childValue, parentId);


        when(itemsRepository.existsByParentId(parentId)).thenReturn(true);
        when(itemsRepository.findById(parentId)).thenReturn(Optional.of(parentItem));
        when(itemsRepository.save(new Item(null, parentLevel, parentId, childValue, null, new HashSet<>()))).thenReturn(new Item(null, parentLevel, parentId, childValue, null, new HashSet<>()));

        Item addedItem = itemsService.addItem(childValue, parentId);
        assertThat(addedItem.getParentId()).isEqualTo(parentId);
        assertThat(addedItem.getLevel()).isEqualTo(parentLevel + 1);
        assertThat(addedItem.getValue()).isEqualTo(childValue);
        assertThat(addedItem.getSum()).isEqualTo(childValue + parentValue);
    }

    @Test(expected = ItemNotFoundException.class)
    public void removeItem() {
        Long itemId = new Random().nextLong();

        when(itemsRepository.existsById(itemId)).thenReturn(false);
        itemsService.removeItem(itemId);

        when(itemsRepository.findById(itemId)).thenReturn(Optional.of(new Item()));
        itemsService.removeItem(itemId);
    }

    @Test(expected = ItemNotFoundException.class)
    public void updateItemValue() {
        Long itemId = new Random().nextLong();
        Integer newValue = new Random().nextInt();
        Long newParentId = new Random().nextLong();

        when(itemsRepository.existsById(itemId)).thenReturn(false);
        itemsService.updateItem(itemId, newValue, newParentId);
    }

    @Test(expected = UnprocessableEntityException.class)
    public void updateItemParentId() {
        Long subItemId = new Random().nextLong();
        Long parentId = subItemId;

        Item subItem = new Item(null, null, parentId, null, null, new HashSet<>());
        Item parentItem = new Item(null, null, null, null, null, new HashSet<>());
        parentItem.setId(parentId);

        when(itemsRepository.existsById(subItemId)).thenReturn(true);
        itemsService.updateItem(subItemId, null, parentId);

        when(itemsRepository.findById(subItemId)).thenReturn(Optional.of(subItem));
        when(itemsRepository.findById(parentId)).thenReturn(Optional.of(parentItem));
        itemsService.updateItem(subItemId, null, parentId);
    }
}