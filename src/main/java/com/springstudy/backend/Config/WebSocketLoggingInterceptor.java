/*
package com.springstudy.backend.Config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WebSocketLoggingInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // STOMP 헤더 추출
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        StompCommand command = accessor.getCommand();
        String sessionId = accessor.getSessionId();

        if (command != null) {
            switch (command) {
                case CONNECT -> log.info("📡 WebSocket CONNECT: sessionId={}", sessionId);
                case SUBSCRIBE -> log.info("🔔 SUBSCRIBE to: {}, sessionId={}", accessor.getDestination(), sessionId);
                case SEND -> log.info("📤 SEND message to: {}, payload={}", accessor.getDestination(), message.getPayload());
                case DISCONNECT -> log.info("❌ DISCONNECT: sessionId={}", sessionId);
                default -> log.info("🌀 WebSocket Command: {}, sessionId={}", command, sessionId);
            }
        }

        return message;
    }
}
*/
