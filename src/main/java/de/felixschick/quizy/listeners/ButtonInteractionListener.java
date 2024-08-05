package de.felixschick.quizy.listeners;

import de.felixschick.quizy.QuizyApplication;
import de.felixschick.quizy.handler.QuestionResponseHandler;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ButtonInteractionListener extends ListenerAdapter {

    private QuestionResponseHandler responseHandler;

    public ButtonInteractionListener() {
        responseHandler = QuizyApplication.getQuestionResponseHandler();
    }


    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getComponent().getId().startsWith("quizanswer%10%")){
            responseHandler.handleButtonInteraction(event);
        }
    }
}
