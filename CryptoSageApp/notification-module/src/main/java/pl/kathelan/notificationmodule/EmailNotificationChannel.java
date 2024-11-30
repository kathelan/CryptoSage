package pl.kathelan.notificationmodule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import pl.kathelan.commons.dto.AlertDTO;

@Service
@ConditionalOnProperty(name = "notification.channel.enabled", havingValue = "true", matchIfMissing = true)
@Slf4j
public class EmailNotificationChannel implements NotificationChannel {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    private final EmailNotificationProperties emailProps;


    public EmailNotificationChannel(JavaMailSender mailSender, EmailNotificationProperties emailProps) {
        this.mailSender = mailSender;
        this.emailProps = emailProps;
    }

    @Override
    public void sendAlert(AlertDTO alertDTO) throws Exception {
        try {
            log.info("sending alert: {}", alertDTO);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(emailProps.getRecipients().toArray(new String[0]));
            message.setSubject("Alert dla " + alertDTO.getCryptoPair());
            message.setText("Sygnał: " + alertDTO.getSignalType() + "\nData: " + alertDTO.getCreatedAt());
            mailSender.send(message);
            log.info("Sent e-mail with alert for {} to {}", alertDTO.getCryptoPair(), emailProps.getRecipients());
        } catch (Exception e) {
            log.error("Error while sending email {}: {}", alertDTO.getCryptoPair(), e.getMessage());
            throw e;
        }
    }
}
