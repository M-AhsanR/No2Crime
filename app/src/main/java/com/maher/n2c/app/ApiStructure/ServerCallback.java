package com.maher.n2c.app.ApiStructure;

import org.json.JSONObject;

public interface ServerCallback {
    void onSuccess(JSONObject result, String ERROR);
}
