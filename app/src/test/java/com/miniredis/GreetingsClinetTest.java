package com.miniredis;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GreetingsClinetTest {
    @Test
    public void givenGreetingClient_whenServerRespondsWhenStarted_thenCorrect() throws Exception{
        GreetingsClient client = new GreetingsClient();
        client.startConnection("127.0.0.1", 9999);
        String response = client.sendMessage("hello server");
        assertEquals("hello client", response);
    }
}
