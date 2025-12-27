package com.drafty.entity;

public record DraftAction(
    String championId,
    String actionType, // "PICK" or "BAN"
    String team        // "BLUE" or "RED"
) {}