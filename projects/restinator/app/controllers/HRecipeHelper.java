package controllers;

import play.mvc.Controller;

/**
 * Manages data submitted from the hRecipeHelper app to track some recipes
 * @author leeclarke
 */
public class HRecipeHelper extends Controller {

    /**
     * Use to save recipe info, {title, author, date, urlToPosting} Should make this spawn an async update and immediately return a 200.
     * Only reason to async is to check for existing record. key =(title+author+date )
     * 
     * Will a post have same same-site-origin issues as a get?
     */
    public void saveRecipe() {
        //TODO: Build DataObject w/ Siena 
        //TODO: code this posting
        //TODO: add Async process.
        //TODO: add admin page for viewing results if cant be done well enough through admin.
    }
}
