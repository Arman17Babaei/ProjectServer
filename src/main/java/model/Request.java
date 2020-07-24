package model;

import com.google.gson.JsonObject;

public class Request implements BaseModel {
    private String id;
    private JsonObject jsonObject;

    public Request(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
        this.id = jsonObject.get("id").getAsString();
    }

    @Override
    public String getId() {
        return id;
    }

    public JsonObject getJsonObject() {
        return jsonObject;
    }
}
