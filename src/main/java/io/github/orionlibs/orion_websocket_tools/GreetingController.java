package io.github.orionlibs.orion_websocket_tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class GreetingController
{
    private SimpMessagingTemplate messenger;


    @Autowired
    public GreetingController(SimpMessagingTemplate messenger)
    {
        this.messenger = messenger;
    }


    @PostMapping(value = "/someControllerMethod")
    public void someControllerMethod()
    {
        this.messenger.convertAndSend("/topic/greetings", "someControllerMethod called");
    }


    @MessageMapping("/isAuthenticated")
    @SendToUser("/topic/isAuthenticated")
    public String userStatus(String userStatus)
    {
        return "new: " + userStatus;
    }


    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleException(Throwable exception)
    {
        return exception.getMessage();
    }
}
