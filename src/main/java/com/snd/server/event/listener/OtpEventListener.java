package com.snd.server.event.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.snd.server.constant.RabbitMqConstant;
import com.snd.server.event.domain.OtpEvent;
import com.snd.server.service.EmailService;

@Component
public class OtpEventListener {
    private final EmailService emailService;

    public OtpEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = RabbitMqConstant.OTP_QUEUE)
    public void handleOtpEvent(OtpEvent event) {
        try {

            switch (event.getEventType()) {
                case REGISTER_OTP:
                    emailService.sendVerificationEmail(event.getFullName(), event.getEmail(), event.getOtp());
                    break;
                case VERIFY_OTP:
                    emailService.sendVerificationEmail(event.getFullName(), event.getEmail(), event.getOtp());
                    break;
                case FORGOT_PASSWORD:
                    emailService.sendVerificationPassword(event.getFullName(), event.getEmail(), event.getOtp());
                    break;
                case RESET_PASSWORD:
                    emailService.sendPasswordResetConfirmation(event.getFullName(), event.getEmail());
                    break;
                default:
                    System.err.println("Invalid event type: " + event.getEventType());
            }
        } catch (Exception e) {
            System.err.println("Error processing event: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
