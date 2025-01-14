package io.github.orionlibs.orion_websocket_tools;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketMessageController
{
    @MessageMapping("/testtopic") //incoming messages sent to "/app/testtopic"
    @SendTo("/topic/testtopic") //responses sent to "/topic/testtopic" which will be received by the subscribers
    public String handleTestTopicMessage(String message)
    {
        return "Received: " + message;
    }
}
