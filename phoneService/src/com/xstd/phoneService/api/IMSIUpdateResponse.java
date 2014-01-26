package com.xstd.phoneService.api;

import com.plugin.internet.core.ResponseBase;
import com.plugin.internet.core.json.JsonProperty;

/**
 * Created by michael on 14-1-26.
 */
public class IMSIUpdateResponse extends ResponseBase {

    @JsonProperty("result")
    public int result;

    @JsonProperty("uploadCont")
    public int uploadCont;

    @JsonProperty("orgCount")
    public int orgCount;

}
