package com.mudson.The_bank_api_product.service.Impl;

import com.mudson.The_bank_api_product.dto.EmailDetails;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Objects;


@Service
public class EmailServiceImpl implements EmailService{

    @Autowired
    private JavaMailSender javaMailSender;


    @Value("${spring.mail.username}")
    private String senderEmail;

    @Override
    public void sendEmailAlert(EmailDetails emailDetails) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(senderEmail);
            mailMessage.setTo(emailDetails.getRecipient());
            mailMessage.setText(emailDetails.getMessageBody());
            mailMessage.setSubject(emailDetails.getSubject());

            javaMailSender.send(mailMessage);
            System.out.println("Mail sent successfully");
        } catch (MailException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendEmailwithAttachment(EmailDetails emailDetails) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper ;
        try{
            mimeMessageHelper =new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(senderEmail);
            mimeMessageHelper.setTo(emailDetails.getRecipient());
            mimeMessageHelper.setSubject(emailDetails.getSubject());
            mimeMessageHelper.setText(emailDetails.getMessageBody());
            FileSystemResource file = new FileSystemResource(emailDetails.getAttachment());
            mimeMessageHelper.addAttachment(Objects.requireNonNull(file.getFilename()),file);
            javaMailSender.send(mimeMessage);

            System.out.println("Email Sent");


        }
        catch(Exception e){
            throw new RuntimeException(e);
        }

    }

}
