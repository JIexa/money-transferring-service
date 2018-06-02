package com.github.jiexa.model;

import lombok.Value;

import java.util.UUID;

@Value
public class Account {
    private UUID accountId;
    private Long ownerId;
}
