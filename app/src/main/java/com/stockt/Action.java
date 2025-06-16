package com.stockt;

import java.util.List;

public class Action {
    public enum ActionType {
        ADD_ITEM,
        UPDATE_ITEM,
        DELETE_ITEM
    }

    private ActionType actionType;
    private List<Object> actionValues; //an store item details like name, quantity, etc.

    public Action(ActionType actionType, List<Object> actionValues) {
        this.actionType = actionType;
        this.actionValues = actionValues;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public List<Object> getActionValues() {
        return actionValues;
    }
}