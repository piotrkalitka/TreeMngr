package pl.piotrkalitka.TreeMngr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import io.swagger.annotations.ApiOperation;
import pl.piotrkalitka.TreeMngr.model.Item;
import pl.piotrkalitka.TreeMngr.payload.AddItemRequestBody;
import pl.piotrkalitka.TreeMngr.payload.CopyItemRequestBody;
import pl.piotrkalitka.TreeMngr.payload.UpdateItemRequestBody;
import pl.piotrkalitka.TreeMngr.service.ItemsService;

@RestController
@CrossOrigin
@RequestMapping("/api/")
public class MainController {

    private ItemsService itemsService;

    @Autowired
    public MainController(ItemsService itemsService) {
        this.itemsService = itemsService;
    }


    /**
     * @return all items as tree structure
     */
    @ApiOperation(value = "Get all items as tree structure", response = Item.class)
    @GetMapping()
    public ResponseEntity<?> getItems() {
        return ResponseEntity.ok(itemsService.getItems());
    }

    /**
     * @param requestBody - body with item value
     * @return added item
     * @throws pl.piotrkalitka.TreeMngr.exception.FirstItemExistsException if first item already exists
     */
    @ApiOperation(value = "Create first item", response = Item.class)
    @PostMapping("/")
    public ResponseEntity<?> addFirstItem(@Valid @RequestBody AddItemRequestBody requestBody) {
        Item item = itemsService.addItem(requestBody.getValue());
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }

    /**
     * @param requestBody - body with item value
     * @param parentId    - id of item which has to become parent
     * @return added item
     * @throws pl.piotrkalitka.TreeMngr.exception.ItemNotFoundException if item for given parentId is not found
     */
    @ApiOperation(value = "Create sub item", response = Item.class)
    @PostMapping("/{parentId}")
    public ResponseEntity<?> addItem(@Valid @RequestBody AddItemRequestBody requestBody, @PathVariable("parentId") Long parentId) {
        Item item = itemsService.addItem(requestBody.getValue(), parentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }

    /**
     * @param itemId id of item that has to be deleted
     * @return 201 no content
     * @throws pl.piotrkalitka.TreeMngr.exception.ItemNotFoundException if item for given itemId is not found
     */
    @ApiOperation(value = "Remove item")
    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> removeItem(@PathVariable("itemId") Long itemId) {
        itemsService.removeItem(itemId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * @param requestBody - if of item to copy
     * @param targetId    - id of item where other item will be copied
     * @return Item with changes
     */
    @ApiOperation(value = "Copy item", response = Item.class)
    @PostMapping("/{targetId}/copy")
    public ResponseEntity<?> copyItem(@Valid @RequestBody CopyItemRequestBody requestBody, @PathVariable("targetId") Long targetId) {
        Item item = itemsService.copyItem(requestBody.getItemId(), targetId);
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }

    /**
     * @param itemId      - id of item that has to be updated
     * @param requestBody - body with new parameters
     * @return Item with changes
     * @throws pl.piotrkalitka.TreeMngr.exception.ItemNotFoundException if item for given itemId is not found
     */
    @ApiOperation(value = "Update item values", response = Item.class)
    @PatchMapping("/{itemId}")
    public ResponseEntity<?> updateItem(@RequestBody UpdateItemRequestBody requestBody, @PathVariable("itemId") Long itemId) {
        Item item = itemsService.updateItem(itemId, requestBody.getValue(), requestBody.getParentId());
        return ResponseEntity.ok(item);
    }

}