package me.contrapost.jee_quiz.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Created by alexandershipunov on 28/10/2016.
 * AnswerMap validation
 */
@Constraint(validatedBy = AnswerMapValidator.class)
@Target({  //tells on what the @ annotation can be used
        ElementType.FIELD,
        ElementType.METHOD,
        ElementType.ANNOTATION_TYPE}
)
@Retention(RetentionPolicy.RUNTIME) //specify it should end up in the bytecode and be readable using reflection
@Documented //should be part of the JavaDoc of where it is applied to
public @interface AnswerMap {

    String message() default "Invalid question set";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
