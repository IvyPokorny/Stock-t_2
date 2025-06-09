package com.stockt;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class HistoryManager {
    private static HistoryManager instance;
    private Stack<Action> undoStack;
    private Stack<Action> redoStack;

    private HistoryManager() {
        undoStack = new Stack<>();
        redoStack = new Stack<>();
    }

    public static synchronized HistoryManager getInstance() {
        if (instance == null) {
            instance = new HistoryManager();
        }
        return instance;
    }

    public void logAction(Action action) {
        undoStack.push(action);
        redoStack.clear(); // Clear redo stack on new action
    }

    public Action undo() {
        if (!undoStack.isEmpty()) {
            Action action = undoStack.pop();
            redoStack.push(action); // Save for redo
            return action;
        }
        return null;
    }

    public Action redo() {
        if (!redoStack.isEmpty()) {
            Action action = redoStack.pop();
            undoStack.push(action); // Restore for undo
            return action;
        }
        return null;
    }

    public List<Action> getUndoStack() {
        return new ArrayList<>(undoStack);
    }

    public List<Action> getRedoStack() {
        return new ArrayList<>(redoStack);
    }

    //Resets the entire hostory
    public void resetHistory() {
        undoStack.clear(); // Clear the undo stack
        redoStack.clear(); // Clear the redo stack
    }
}