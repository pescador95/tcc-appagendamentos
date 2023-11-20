package app.core.model.DTO;

import app.core.utils.BasicFunctions;

import java.util.ArrayList;
import java.util.List;

public class Responses {

    private Integer status;

    private Object data;

    private List<Object> datas;

    private List<String> messages;

    private Boolean ok;

    public Responses() {
        this.datas = new ArrayList<>();
        this.messages = new ArrayList<>();
        this.ok = Boolean.TRUE;
    }

    public Responses(int i, Object data, List<Object> datas, List<String> messages, Boolean ok) {
        this.status = i;
        this.data = data;
        this.datas = datas;
        this.messages = messages;
        this.ok = ok;
    }

    public Boolean hasMessages() {
        return BasicFunctions.isNotEmpty(this.messages);
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public List<Object> getDatas() {
        return datas;
    }

    public void setDatas(List<Object> datas) {
        this.datas = datas;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public Boolean getOk() {
        return ok;
    }

    public void setOk(Boolean ok) {
        this.ok = ok;
    }
}
