package com.example.bingo;

public class Event {
    String description;
    boolean marked;

    // Конструктор
    Event(String description) {
        this.description = description;
        this.marked = false; // Початково не помічено
    }


    public String getDescription() {
        return description;
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }
}