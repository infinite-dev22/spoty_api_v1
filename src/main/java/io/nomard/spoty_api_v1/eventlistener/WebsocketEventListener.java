package io.nomard.spoty_api_v1.eventlistener;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Log
public class WebsocketEventListener {

    @EventListener
    public void handleWebsocketDisconnectListener(SessionDisconnectEvent event) {
        //TODO -- To Be Implemented
    }
}
