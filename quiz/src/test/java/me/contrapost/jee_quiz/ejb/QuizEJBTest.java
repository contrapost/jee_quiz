package me.contrapost.jee_quiz.ejb;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Alexander Shipunov on 24.10.16.
 * Test of QuizEJB
 */
@RunWith(Arquillian.class)
public class QuizEJBTest extends EjbTestBase {

    @Test
    public void testCreateQuiz() {

        String question = "Question";

        createQuiz(question);

        assertEquals(question, quizEJB.getAllQuizes().get(0).getQuestion());
    }

    @Test
    public void testDeleteQuiz() {
        long quizId = createQuiz("Question");

        assertTrue(quizEJB.deleteQuiz(quizId));
        assertNull(quizEJB.getQuiz(quizId));
        assertTrue(quizEJB.getAllQuizes().size() == 0);
    }
}
