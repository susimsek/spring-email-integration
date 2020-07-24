package com.spring.email.listener;

import com.spring.email.model.Attachment;
import com.spring.email.model.Email;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class EmailListener {


    @ServiceActivator(inputChannel = "imapChannel")
    @Bean
    public MessageHandler processNewEmail() {
        MessageHandler messageHandler = new MessageHandler() {

            @Override
            public void handleMessage(org.springframework.messaging.Message<?> message) throws org.springframework.messaging.MessagingException {
                try {
                    Message receivedMessage = (Message) message.getPayload();

                    setEmail(receivedMessage);

                    } catch (IOException e) {
                    e.printStackTrace();
                } catch (MessagingException e) {
                    e.printStackTrace();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        };
        return messageHandler;
    }

    private void setEmail(Message receivedMessage) throws Exception {

        String subject=receivedMessage.getSubject();
        String from= ((InternetAddress)((Address)(receivedMessage.getFrom()[0]))).getAddress();
        Date receivedDate= receivedMessage.getReceivedDate();

        List<Attachment> attachments=getAttachments(receivedMessage);

        Email email=Email.builder()
                .mailSubject(subject)
                .mailFrom(from)
                .mailReceivedDate(receivedDate)
                .attachments(attachments)
                .build();
        System.out.println(email.toString());

    }




    public List<Attachment> getAttachments(Message message) throws Exception {
        Object content = message.getContent();
        if (content instanceof String)
            return null;

        if (content instanceof Multipart) {
            Multipart multipart = (Multipart) content;
            List<Attachment> result = new ArrayList<Attachment>();

            for (int i = 0; i < multipart.getCount(); i++) {
                result.addAll(getAttachments(multipart.getBodyPart(i)));
            }
            return result;

        }
        return null;
    }

    private List<Attachment> getAttachments(BodyPart part) throws Exception {
        List<Attachment> result = new ArrayList<Attachment>();
        Object content = part.getContent();
        if (content instanceof InputStream || content instanceof String) {
            if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition()) || StringUtils.isNotBlank(part.getFileName())) {
                if(part.getContentType().contains("image") || part.getContentType().contains("pdf")){
                    Attachment attachment = Attachment.builder()
                            .name(part.getFileName())
                            .file(IOUtils.toByteArray(part.getInputStream()))
                            .build();
                    result.add(attachment);
                }

                return result;
            } else {
                return new ArrayList<Attachment>();
            }
        }

        if (content instanceof Multipart) {
            Multipart multipart = (Multipart) content;
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                result.addAll(getAttachments(bodyPart));
            }
        }
        return result;
    }

}
