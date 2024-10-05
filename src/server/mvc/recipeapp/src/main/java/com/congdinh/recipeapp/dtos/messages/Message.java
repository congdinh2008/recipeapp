package com.congdinh.recipeapp.dtos.messages;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String type;
    private String content;
}
