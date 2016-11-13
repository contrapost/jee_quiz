package me.contrapost.jee_quiz.ejb;

import me.contrapost.jee_quiz.entity.Quiz;
import me.contrapost.jee_quiz.entity.SpecifyingCategory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Alexander Shipunov on 24.10.16.
 * QuizEJB
 */
@Stateless
public class QuizEJB {

    @PersistenceContext
    protected EntityManager em;

    public long createQuiz(@NotNull String question, @NotNull Map<String, Boolean> answers, @NotNull long specifyingCategoryId) {
        SpecifyingCategory category = em.find(SpecifyingCategory.class, specifyingCategoryId);
        if (category == null) throw new IllegalArgumentException("No such specifying category: " + specifyingCategoryId);

        Quiz quiz = new Quiz();
        quiz.setQuestion(question);
        quiz.setAnswerMap(answers);
        quiz.setSpecifyingCategory(category);

        em.persist(quiz);

        category.getQuizzes().put(quiz.getId(), quiz);

        return quiz.getId();
    }

    public boolean deleteQuiz(@NotNull long id) {
        Quiz quiz = em.find(Quiz.class, id);
        if (quiz == null) return false;
        SpecifyingCategory specifyingCategory = em.find(SpecifyingCategory.class,
                quiz.getSpecifyingCategory().getId());
        specifyingCategory.getQuizzes().remove(id);
        return true;
    }

    public boolean updateQuizQuestion(@NotNull long quizId, @NotNull String newQuestionText) {
        Quiz quiz = em.find(Quiz.class, quizId);
        if (quiz == null) return false;
        quiz.setQuestion(newQuestionText);
        return true;
    }

    public boolean updateAnswersMap(@NotNull long quizId, @NotNull String previousAnswer, @NotNull String newAnswer) {
        Quiz quiz = em.find(Quiz.class, quizId);
        if (quiz == null || quiz.getAnswerMap().get(previousAnswer) == null) 
            throw new IllegalArgumentException("Quiz doesn't exist or the previous answer doesn't match one in the map");
        boolean isCorrect = quiz.getAnswerMap().get(previousAnswer);
        quiz.getAnswerMap().remove(previousAnswer);
        quiz.getAnswerMap().put(newAnswer, isCorrect);
        return true;
    }

    public Quiz getQuiz(long id) {
        return em.find(Quiz.class, id);
    }

    public List<Quiz> getAllQuizzes() {
        //noinspection unchecked
        return em.createNamedQuery(Quiz.GET_ALL_QUIZZES).getResultList();
    }

    public boolean updateQuiz(Long id, String newQuestion, Map<String, Boolean> newAnswerMap) {
        Quiz quiz = em.find(Quiz.class, id);
        if (quiz == null) return false;
        quiz.setQuestion(newQuestion);
        quiz.setAnswerMap(newAnswerMap);
        return true;
    }

    public List<Long> getRandomQuizzes(int numberOfQuizzes) {
        List<Quiz> quizzes = getAllQuizzes();
        List<Long> ids = new ArrayList<>();
        while(ids.size() != numberOfQuizzes && quizzes.size() != 0) {
            ids.add(quizzes.remove(new Random().nextInt(quizzes.size())).getId());
        }
        return ids;
    }
}
