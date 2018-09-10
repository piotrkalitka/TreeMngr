package pl.piotrkalitka.TreeMngr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import pl.piotrkalitka.TreeMngr.exception.FirstItemExistsException;
import pl.piotrkalitka.TreeMngr.exception.ItemNotFoundException;
import pl.piotrkalitka.TreeMngr.exception.UnprocessableEntityException;
import pl.piotrkalitka.TreeMngr.model.Item;
import pl.piotrkalitka.TreeMngr.repository.ItemsRepository;

@Component
public class ItemsService {

    private ItemsRepository itemsRepository;

    @Autowired
    public ItemsService(ItemsRepository itemsRepository) {
        this.itemsRepository = itemsRepository;
    }

    /**
     * @return all items as tree structure or null if there is no first item
     */
    @Nullable
    public Item getItems() {
        if (!itemsRepository.existsByParentId(null)) return null;
        return itemsRepository.findByParentId(null);
    }

    /**
     * @param value - value of item that has to be created
     * @return Item which has been created
     * @throws FirstItemExistsException if there is first item already
     */
    public Item addItem(Integer value) {
        if (getItemsCount() != 0) {
            throw new FirstItemExistsException();
        }
        Item item = new Item();
        item.setValue(value);
        item.setSum(value);
        item.setLevel(0);
        itemsRepository.save(item);
        return item;
    }

    /**
     * @param value    - value of item that has to be created
     * @param parentId - id of parent item
     * @return Item which has been created
     * @throws ItemNotFoundException if item for given id does not exist
     */
    public Item addItem(Integer value, Long parentId) {
        if (!doesItemExists(parentId)) {
            throw new ItemNotFoundException(parentId);
        }

        Item child = new Item();
        child.setValue(value);
        child.setParentId(parentId);
        child.setSum(calcSum(parentId, value));
        child.setLevel(getItem(parentId).getLevel() + 1);
        itemsRepository.save(child);

        return child;
    }

    /**
     * @param id - id of item that has to be removed
     * @throws ItemNotFoundException if item for given id does not exist
     */
    public void removeItem(Long id) {
        if (!doesItemExists(id)) {
            throw new ItemNotFoundException(id);
        }
        itemsRepository.delete(getItem(id));
    }

    /**
     * @param itemId   - id of item that has to be updated
     * @param value    - value to update
     * @param parentId - id of new parent
     * @return Item with changes
     */
    public Item updateItem(Long itemId, Integer value, Long parentId) {
        if (!doesItemExists(itemId)) {
            throw new ItemNotFoundException(itemId);
        }

        if (value != null) {
            updateValue(itemId, value);
        }
        if (parentId != null) {
            if (itemId.equals(parentId) || isSubItemOf(itemId, parentId))
                throw new UnprocessableEntityException(itemId, parentId);
            changeParent(itemId, parentId);
        }
        return itemsRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));
    }

    /**
     * @param itemId      - id of item to copy
     * @param newParentId - id of target item
     * @return - new created item
     */
    public Item copyItem(Long itemId, Long newParentId) {
        Item itemToCopy = getItem(itemId);
        Item addedItem = addItem(itemToCopy.getValue(), newParentId);
        for (Item item : itemToCopy.getChildren()) {
            copyItem(item.getId(), addedItem.getId());
        }
        return addedItem;
    }


    /**
     * @param subItemId - id of item to check
     * @param itemId - id of potential parent
     * @return - true/false depends on result
     */
    private boolean isSubItemOf(Long subItemId, Long itemId) {
        Long parentId = itemId;
        while (parentId != null) {
            if (parentId.equals(subItemId)) return true;
            parentId = getItem(parentId).getParentId();
        }
        return false;
    }

    /**
     * @param itemId      - id of item which parent will be changed
     * @param newParentId - id of new parent
     */
    private void changeParent(Long itemId, Long newParentId) {
        Item item = getItem(itemId);
        item.setParentId(newParentId);
        item.setLevel(getItem(newParentId).getLevel() + 1);
        itemsRepository.save(item);
        refreshSum(item.getId());
    }

    /**
     * @param itemId - id of item that will change value
     * @param value  - new value of item
     */
    private void updateValue(Long itemId, Integer value) {
        Item item = getItem(itemId);
        item.setValue(value);
        itemsRepository.save(item);
        refreshSum(itemId);
    }

    /**
     * @param itemId - id of item which will be refreshed
     */
    private void refreshSum(Long itemId) {
        Item item = getItem(itemId);
        item.setSum(calcSum(itemId));
        itemsRepository.save(item);
        refreshChildrenSums(itemId);
    }

    /**
     * @param parentId - id of item which children sums will be refreshed
     */
    private void refreshChildrenSums(Long parentId) {
        Item parent = getItem(parentId);
        for (Item item : parent.getChildren()) {
            refreshSum(item.getId());
        }
    }

    /**
     * @param itemId - id of item for sum calculating
     * @return sum of children values and itself value
     */
    private Integer calcSum(Long itemId) {
        Integer sum = 0;
        while (itemId != null) {
            Item item = getItem(itemId);
            sum += item.getValue();
            itemId = item.getParentId();
        }
        return sum;
    }

    /**
     * @param parentId - id of parent of item to calc sum
     * @param value    - value of item
     * @return sum of children values and itself value
     */
    private Integer calcSum(Long parentId, Integer value) {
        Integer sum = value;
        sum += calcSum(parentId);
        return sum;
    }

    /**
     * @param id - id of item to return
     * @return Item for given id
     */
    private Item getItem(Long id) {
        return itemsRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(id));
    }

    /**
     * @param id - id of item to check
     * @return true/false result
     */
    private boolean doesItemExists(Long id) {
        return itemsRepository.existsById(id);
    }

    /**
     * @return count of all items in db
     */
    private long getItemsCount() {
        return itemsRepository.count();
    }

}
