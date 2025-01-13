package io.github.orionlibs.orion_websocket_tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class GreetingController
{
    private SimpMessagingTemplate messenger;


    @Autowired
    public GreetingController(SimpMessagingTemplate messenger)
    {
        this.messenger = messenger;
    }


    @GetMapping(value = "/users/info", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String getInfo()
    //public ResponseEntity<String> getInfo()
    {
        return "<!doctype html><html lang=\"en\"><head><title>HTTP Status 200 – Found info</title></head><h1>HTTP Status 200 – Found info</h1></body></html>";
        /*return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_HTML)
                        .body("<!doctype html><html lang=\"en\"><head><title>HTTP Status 200 – Found info</title></head><h1>HTTP Status 200 – Found info</h1></body></html>");*/
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
    @SendToUser("/users/queue/errors")
    public String handleException(Throwable exception)
    {
        return exception.getMessage();
    }
}
