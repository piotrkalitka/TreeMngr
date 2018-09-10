package pl.piotrkalitka.TreeMngr.payload;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;

public class CopyItemRequestBody {

    @NotNull
    @ApiModelProperty(notes = "Id of item which will be copied")
    private Long itemId;

    public CopyItemRequestBody() {
    }

    public CopyItemRequestBody(Long sourceId) {
        this.itemId = sourceId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }
}
