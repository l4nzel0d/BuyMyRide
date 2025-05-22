package com.example.buymyride.util;

public class OneTimeEvent<T> {
    private final T content;
    private boolean hasBeenHandled = false;

    public OneTimeEvent(T content) {
        this.content = content;
    }

    public T getContentIfNotHandled() {
        if (hasBeenHandled) return null;

        hasBeenHandled = true;
        return content;
    }

    public T peekContent() {
        return content;
    }
}
