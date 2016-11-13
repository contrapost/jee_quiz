package me.contrapost.jee_quiz.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Map;

/**
 * Created by alexandershipunov on 28/10/2016.
 * Question map validator checks if map has correct number of questions and only one is correct
 */
public class AnswerMapValidator implements ConstraintValidator<AnswerMap, Map<String, Boolean>> {
   public void initialize(AnswerMap constraint) {
   }

   public boolean isValid(Map<String, Boolean> answersMap, ConstraintValidatorContext context) {

       if (answersMap.size() != 4) return false;

       int correct = 0;
       for (boolean b : answersMap.values()) {
           if (b) correct++;
       }

       return correct == 1;
   }
}
