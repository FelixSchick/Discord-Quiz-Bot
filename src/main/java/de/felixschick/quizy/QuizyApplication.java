package de.felixschick.quizy;

import de.felixschick.quizy.handler.QuestionCreationHandler;
import de.felixschick.quizy.handler.QuestionResponseHandler;
import de.felixschick.quizy.helper.CommandHelper;
import de.felixschick.quizy.sql.MySQL;
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
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@SpringBootApplication
public class QuizyApplication implements ApplicationRunner {

    @Getter
    private static JDA jda;

    @Getter
    private static CommandHelper commandHelper;

    @Getter
    private static QuizProvider quizProvider;

    @Getter
    private static QuestionCreationHandler creationHandler;

    @Getter
    private static QuestionResponseHandler questionResponseHandler;

    @Getter
    private static MySQL mySQL;

    @Getter
    private static BotInformationProvider botInformationProvider;


    @Getter
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        SpringApplication.run(QuizyApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments applicationArguments) {
        String[] args = applicationArguments.getSourceArgs();
        if (args.length >= 1) {
            if (args.length == 6) {
                mySQL = new MySQL(args[1], args[2], args[3], args[4], args[5]);
            } else if (args.length == 5) {
                mySQL = new MySQL(args[1], args[2], args[3], args[4]);
            } else
                throw new RuntimeException("No SQL input args found");

            quizProvider = new QuizProvider();
            botInformationProvider = new BotInformationProvider();
            creationHandler = new QuestionCreationHandler();
            questionResponseHandler = new QuestionResponseHandler();
            commandHelper = new CommandHelper();

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
                jda.addEventListener(aClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
