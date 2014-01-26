package com.xstd.phoneService.api;

import com.plugin.internet.core.RequestBase;
import com.plugin.internet.core.annotations.RequiredParam;
import com.plugin.internet.core.annotations.RestMethodUrl;

/**
 * Created by michael on 14-1-26.
 */

@RestMethodUrl("http://www.xinsuotd.net/imsi2phone/")
public class IMSIUpdateRequest extends RequestBase<IMSIUpdateResponse> {

    @RequiredParam("data")
    public String jsonData;

    @RequiredParam("unique")
    public String unique;

    public IMSIUpdateRequest(String updateJsonData, String unique) {
        jsonData = updateJsonData;
        this.unique = unique;
    }

}
