package de.felixschick.mostefficientdiscordbot.listeners;

import de.felixschick.mostefficientdiscordbot.MostEfficientDiscordBot;
import de.felixschick.mostefficientdiscordbot.handler.QuestionResponseHandler;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ButtonInteractionListener extends ListenerAdapter {

    private QuestionResponseHandler responseHandler;

    public ButtonInteractionListener() {
        responseHandler = MostEfficientDiscordBot.getQuestionResponseHandler();
    }


    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getComponent().getId().startsWith("quizanswer%10%")){
            responseHandler.handleButtonInteraction(event);
        }
    }
}
