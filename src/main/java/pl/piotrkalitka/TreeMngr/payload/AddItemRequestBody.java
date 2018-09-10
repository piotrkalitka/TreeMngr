package pl.piotrkalitka.TreeMngr.payload;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;

public class AddItemRequestBody {

    @NotNull
    @ApiModelProperty(notes = "Initial item value", required = true)
    private Integer value;

    public AddItemRequestBody(Integer value) {
        this.value = value;
    }

    public AddItemRequestBody() {
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
