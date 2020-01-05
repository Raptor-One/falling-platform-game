package com.rptr1.fpg.msgs;

import java.util.ArrayList;
import java.util.List;

public class GameEvent
{
    private List<Message> messages = new ArrayList<>();
    private List<String> recipients = new ArrayList<>();

    public GameEvent( )
    {
    }

    public GameEvent( List<String> recipients, List<Message> messages )
    {
        this.recipients = recipients;
        this.messages = messages;
    }

    public GameEvent( List<String> recipients, Message message )
    {
        this.recipients = recipients;
        this.messages.add( message );
    }

    public GameEvent( String recipientUid, Message message )
    {
        this.recipients.add( recipientUid );
        this.messages.add( message );
    }
    public GameEvent( String recipientUid, List<Message> messages )
    {
        this.recipients.add( recipientUid );
        this.messages = messages;
    }

    public void addMessage( Message message )
    {
        this.messages.add( message );
    }

    public void addRecipient( String recipient )
    {
        this.recipients.add( recipient );
    }

    public List<Message> getMessages()
    {
        return messages;
    }

    public List<String> getRecipients()
    {
        return recipients;
    }

}
