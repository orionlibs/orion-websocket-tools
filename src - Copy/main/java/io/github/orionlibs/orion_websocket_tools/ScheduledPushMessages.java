package io.github.orionlibs.orion_websocket_tools;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledPushMessages
{
    private final SimpMessagingTemplate simpMessagingTemplate;


    public ScheduledPushMessages(SimpMessagingTemplate simpMessagingTemplate)
    {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }


    @Scheduled(fixedRate = 2000)
    public void sendMessage()
    {
        //String time = new SimpleDateFormat("HH:mm").format(new Date());
        simpMessagingTemplate.convertAndSend("/topic/chatmessages", "chuck norris message");
    }
}
