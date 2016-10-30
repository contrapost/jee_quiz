package me.contrapost.restApi.api;

import me.contrapost.jee_quiz.ejb.CategoryEJB;
import me.contrapost.jee_quiz.ejb.QuizEJB;
import me.contrapost.restApi.dto.RootCategoryConverter;
import me.contrapost.restApi.dto.RootCategoryDTO;

import com.google.common.base.Throwables;
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
public class QuizRestImplementation implements QuizRestApi {

    @EJB
    private CategoryEJB categoryEJB;

    @EJB
    private QuizEJB quizEJB;

    @Override
    public List<RootCategoryDTO> getRoot() {
        return RootCategoryConverter.transform(categoryEJB.getAllRootCategories());
    }

    @Override
    public Long createRoot(RootCategoryDTO dto) {
        if(dto.id != null){
            throw new WebApplicationException("Cannot specify id for a newly generated root category", 400);
        }

        Long id;
        try{
            id = categoryEJB.createRootCategory(dto.title);
        }catch (Exception e){
            throw wrapException(e);
        }

        return id;
    }

    @Override
    public RootCategoryDTO getRootById(Long id) {
        return RootCategoryConverter.transform(categoryEJB.getRootCategory(id));
    }

    @Override
    public void updateRoot(RootCategoryDTO dto) {
        try {
            categoryEJB.updateCategoryTitle(Long.parseLong(dto.id), dto.title);
        } catch (Exception e){
            throw wrapException(e);
        }
    }

    @Override
    public void deleteRoot(Long id) {
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
