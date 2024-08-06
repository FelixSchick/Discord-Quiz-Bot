package de.felixschick.quizy.listeners;

import de.felixschick.quizy.QuizyApplication;
import de.felixschick.quizy.handler.QuestionResponseHandler;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ButtonInteractionListener extends ListenerAdapter {

    @Autowired
    private QuestionResponseHandler responseHandler;

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getComponent().getId().startsWith("quizanswer%10%")){
            responseHandler.handleButtonInteraction(event);
        }
    }
}
