package me.contrapost.gameApi;

import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.ClassRule;

/**
 * Created by alexandershipunov on 21/11/2016.
 *
 */
public class GameApplicationTest extends GameApplicationTestBase{

    @ClassRule
    public static final DropwizardAppRule<GameConfiguration> RULE =
            new DropwizardAppRule<>(GameApplication.class);
}
