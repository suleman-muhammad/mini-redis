package com.miniredis;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GreetingsClinetTest {
    @Test
    public void givenGreetingClient_whenServerRespondsWhenStarted_thenCorrect() throws Exception{
        GreetingsClient client = new GreetingsClient();
        client.startConnection("127.0.0.1", 6380);
        String response = client.sendMessage("hello server");
        assertEquals("hello server", response);
    }
}
