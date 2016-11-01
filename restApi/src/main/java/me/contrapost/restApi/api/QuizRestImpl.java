package me.contrapost.restApi.api;

import com.google.common.base.Throwables;
import io.swagger.annotations.ApiParam;
import me.contrapost.jee_quiz.ejb.CategoryEJB;
import me.contrapost.jee_quiz.ejb.QuizEJB;
import me.contrapost.restApi.dto.RootCategoryConverter;
import me.contrapost.restApi.dto.RootCategoryDTO;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.WebApplicationException;
import java.util.List;

/**
 * Created by alexandershipunov on 30/10/2016.
 * Implementation of quiz API
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class QuizRestImpl implements QuizRestApi {

    @EJB
    private CategoryEJB categoryEJB;

    @EJB
    private QuizEJB quizEJB;


    @Override
    public List<RootCategoryDTO> getAllRootCategories() {
        return RootCategoryConverter.transform(categoryEJB.getAllRootCategories());
    }

    @Override
    public RootCategoryDTO getById(@ApiParam("The numeric id of the root category") Long id) {
        return RootCategoryConverter.transform(categoryEJB.getRootCategory(id));
    }

    @Override
    public Long createRootCategory(@ApiParam("Title of a new root category. Should not specify id.") RootCategoryDTO dto) {
        /*
            Error code 400:
            the user had done something wrong, eg sent invalid input configurations
         */

        if(dto.id != null){
            throw new WebApplicationException("Cannot specify id for a newly generated root category", 400);
        }

        Long id;
        try{
            id = categoryEJB.createRootCategory(dto.title);
        }catch (Exception e){
            /*
                note: this work just because NOT_SUPPORTED,
                otherwise a rolledback transaction would propagate to the
                caller of this method
             */
            throw wrapException(e);
        }

        return id;
    }

    @Override
    public void delete(@ApiParam("The numeric id of the root category") Long id) {
        categoryEJB.deleteRootCategory(id);
    }

    //----------------------------------------------------------

    private WebApplicationException wrapException(Exception e) throws WebApplicationException{
        Throwable cause = Throwables.getRootCause(e);
        if(cause instanceof ConstraintViolationException){
            return new WebApplicationException("Invalid constraints on input: "+cause.getMessage(), 400);
        } else {
            return new WebApplicationException("Internal error", 500);
        }
    }
}
