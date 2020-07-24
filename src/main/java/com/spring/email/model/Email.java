package com.spring.email.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Email {

    private String mailSubject;
    private String mailFrom;
    private Date mailReceivedDate;
    private List<Attachment> attachments;
}
