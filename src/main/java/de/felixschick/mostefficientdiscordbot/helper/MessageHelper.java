package de.felixschick.mostefficientdiscordbot.helper;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.Calendar;

public class MessageHelper {

    public static void error(TextChannel targetChannel, String title, String description) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("ERROR: " + title)
                .setColor(Color.RED);

        embedBuilder.addField("description", description, false);

        embedBuilder.addField("Support", "If you need help or want to report a bug: Please open a issue", true);

        embedBuilder.setFooter(Calendar.getInstance().getTime().toString());

        targetChannel.sendMessageEmbeds(embedBuilder.build())
                .addActionRow(
                        Button.link("https://github.com/FelixSchick/Discord-Quiz-Bot/issues", "Open an issue here")
                                .withEmoji(Emoji.fromFormatted("<:github:849286315580719104>"))
                ).queue();
    }

}
