package com.spring.email.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attachment {

    private String name;
    private byte[] file;

    @Override
    public String toString() {
        return "Attachment{" +
                "name='" + name + '\'' +
                '}';
    }
}
