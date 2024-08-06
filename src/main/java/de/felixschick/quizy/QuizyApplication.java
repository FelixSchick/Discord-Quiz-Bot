package de.felixschick.quizy;

import de.felixschick.quizy.handler.QuestionCreationHandler;
import de.felixschick.quizy.handler.QuestionResponseHandler;
import de.felixschick.quizy.helper.CommandHelper;
import de.felixschick.quizy.sql.MySQL;
import de.felixschick.quizy.utils.ApplicationContextProvider;
import de.felixschick.quizy.utils.BotInformationProvider;
import de.felixschick.quizy.utils.QuizProvider;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class QuizyApplication implements ApplicationRunner {

    @Getter
    private static JDA jda;

    @Autowired
    private static QuizProvider quizProvider;

    @Autowired
    private BotInformationProvider botInformationProvider;

    public static void main(String[] args) {
        SpringApplication.run(QuizyApplication.class, args);
    }

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    @Override
    public void run(ApplicationArguments applicationArguments) {
        String[] args = applicationArguments.getSourceArgs();
        if (args.length >= 1) {

            startBot(args[0]);

            Runtime.getRuntime().addShutdownHook(new Thread()
            {
                @Override
                public void run()
                {
                    quizProvider.saveAllQuizQuestionsToSQL();
                }
            });


        } else
            throw new RuntimeException("No Token found...");
    }

    private void startBot(String botToken) {
        //init the jdaBuilder and set the Activity
        final JDABuilder jdaBuilder = JDABuilder.createDefault(botToken);
        jdaBuilder.setActivity(Activity.of(
                Activity.ActivityType.valueOf(botInformationProvider.getInfo("activity_type").toUpperCase()),
                botInformationProvider.getInfo("activity_label")
        ));
        jdaBuilder.enableIntents(GatewayIntent.GUILD_MEMBERS);
        jdaBuilder.setMemberCachePolicy(MemberCachePolicy.ALL);

        jda = jdaBuilder.build();

        registerListeners();
    }

    private static void registerListeners() {
        final Reflections reflections = new Reflections("de.felixschick.quizy.listeners");

        reflections.getSubTypesOf(ListenerAdapter.class).forEach(aClass -> {
            try {
                // Retrieve the bean from the Spring context
                ListenerAdapter listener = (ListenerAdapter) ApplicationContextProvider.getContext().getBean(aClass);
                jda.addEventListener(listener);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
