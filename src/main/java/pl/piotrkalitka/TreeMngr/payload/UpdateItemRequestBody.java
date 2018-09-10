package pl.piotrkalitka.TreeMngr.payload;

import io.swagger.annotations.ApiModelProperty;

public class UpdateItemRequestBody {

    @ApiModelProperty(notes = "Id of new parent for item (Moving element)")
    private Long parentId;
    @ApiModelProperty(notes = "New value for item")
    private Integer value;

    public UpdateItemRequestBody() {
    }

    public UpdateItemRequestBody(Long parentId, Integer value) {
        this.parentId = parentId;
        this.value = value;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
