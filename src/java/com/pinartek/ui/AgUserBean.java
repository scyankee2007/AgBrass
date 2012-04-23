/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinartek.ui;

import com.corejsf.util.Messages;
import com.pinartek.dbentities.DbEntityException;
import com.pinartek.dbentities.EntityTemplate;
import com.pinartek.dbentities.MemberEntity;
import com.pinartek.dbentities.UserEntity;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * AgUserBean - manages user information
 * @author Mike Williamson
 */
@Named(value = "agUserBean")
@SessionScoped
public class AgUserBean implements Serializable {

    static final Logger logger = Logger.getLogger("com.pinartek.ui");
    static final String TRYAGAIN = "";
    static final String REGISTER = "register";
    static final String LOGIN = "login";
    static final String SUCCESS = "success";
    static final String FAIL = null;
    static final String LOGOUT = "logout";
    
    
    @Pattern( regexp="^[-+.\\w]{1,64}@[-.\\w]{1,64}\\.[-.\\w]{2,6}$", message="{com.pinartek.emailAddressError}") 
    private String emailAddress = "";

    @Size( min=6, message="{com.pinartek.passwordError}") 
    private String registerPassword = "";
    
    private String password = "";
    private String confirmPassword = "";

    @NotNull( message="{com.pinartek.firstNameError}") 
    @Size( min=1, message="{com.pinartek.firstNameError}" )
    private String firstName = "";
    
    @NotNull( message="{com.pinartek.lastNameError}") 
    @Size( min=1, message="{com.pinartek.lastNameError}" )
    private String lastName = "";
    
    private Integer idUser = null;
    
    private UserEntity theUser = null;
    private MemberEntity theMember = null;
    
    
    /**
     * addErrorMessage - Adds and error message to the FacesContext
     * 
     * @param summary - Summary of the error message
     * @param detail  - Detail of the error message
     */
    private void addErrorMessage( FacesMessage error  ) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage( null , error );
    }


    /**
     * cleanBean - sets the Bean initial state
     */
    private void cleanBean() {

        emailAddress = "";
        registerPassword = "";
        password = "";
        confirmPassword = "";
        firstName = "";
        lastName = "";
        idUser = null;
        theUser = null;
        theMember = null;

    }
    /**
     * loadBean - copies the data from the UserEntity tool to
     * this bean object for the user interface to use.
     */
    private void loadBean() {
        
        if( theUser != null ) {
            this.idUser = theUser.getIdUser();
            this.emailAddress = theUser.getEmailAddress();
            this.firstName = theUser.getFirstName();
            this.lastName = theUser.getLastName();
            
            // Each user also has a member.  Load that up now too
            try {
                if( theMember == null ) theMember = new MemberEntity();                
                theMember.setIdUser( this.idUser );
                theMember.findOneByIdUser();
            }
            catch( DbEntityException e ) {
                logger.log(Level.SEVERE, e.getMessage() );
            }
        }
    }
        
    /** Creates a new instance of AgUserBean */
    public AgUserBean() {
        
        // Session scope means this gets called when the Session is established
        theUser = new UserEntity();
        theMember = new MemberEntity();
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getRegisterPassword() {
        return registerPassword;
    }
    
    public void setRegisterPassword( String registerPassword ) {
        this.registerPassword = registerPassword;
    }
    
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String password) {
        this.confirmPassword = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }
    
    public Integer getIdMember() {
        if( theMember != null ) 
            return theMember.getIdMember();
        else
            return null;
    }
    
    
    /**
     * checkEmailAction - Use the AgUserBean to lookup the user
     * by email address and if found, throws an error because the
     * user has already registered.  If not found, allows the
     * user to move on to Register
     * 
     * @return REGISTER if the user is not found and they can register with
     * the application
     *          TRYAGAIN if the user was found and they need to do something
     * else to get into the application.
     * 
     */
    public String checkEmailAction() {
       
        logger.log( Level.INFO, "Inside checkEmail" );
        Integer result = null;
        
        // Grab the email address and see if it already exists
        theUser.setEmailAddress( this.emailAddress );
        
        try {
            result = theUser.findOneByEmail();
            loadBean();
        }
        catch( Exception e ) {
            logger.log(Level.SEVERE, e.getMessage() );
            addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "technial_error", null ));
            return TRYAGAIN;
        }
        
        if( result != UserEntity.SUCCESS ) {
            /*
             * Welcome a new user - clear the agUserBean to start with
             */
            theUser = new UserEntity();
            theMember = new MemberEntity();
            this.idUser = null;
            this.firstName = null;
            this.lastName = null;
            this.registerPassword = null;
            this.password = null;
            this.confirmPassword = null;
            return REGISTER;
        }
        else {
            addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "welcome_already_used_email", null ));
            return TRYAGAIN;
        }
        
    }


    /**
     * loginAction - Look up the email address and match the password.
     * If the email address is not in the database, suggest that they 
     * register.  If the password doesn't match, suggest they reset it
     * to something new.
     * 
     * @return 
     */
    public String loginAction() {
        
        logger.log( Level.INFO, "Inside loginAction" );
        // Grab the email address entered after it has passed validation
        
        // Grab the email address and see if it already exists
        theUser.setEmailAddress( this.emailAddress );
        
        try {
            theUser.findOneByEmail();
            
            // Found a user, check the password
            if( !theUser.validPassword( this.password ) ) {
                addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "welcome_invalid_login", null ));
                return TRYAGAIN;
            }
            
            loadBean();
        }
        catch( Exception e ) {
            logger.log(Level.SEVERE, e.getMessage() );
            
            // Add a message to the FacesMessages object so the
            // page can display it.
            
            return TRYAGAIN;
        }
                     
        return LOGIN;
        
    }
    
    
    /**
     * registerUserAction - calls the user bean register function and
     * if successful moves on
     * 
     * @return SUCCESS if successful
     *          FAIL if not successful
     */
    public String registerUserAction() {
        
        logger.log( Level.INFO, "Inside registerUserAction" );
        
        theUser.setEmailAddress( this.getEmailAddress());
        theUser.setPassword( this.getRegisterPassword() );
        theUser.setFirstName( this.getFirstName());
        theUser.setLastName( this.getLastName());
        Integer result = null;
       
        try {
            result = theUser.insert();
            if( result == EntityTemplate.SUCCESS ) {
                theUser.findOneByEmail(); // Ensure we have newly generated idUser
                theMember.setIdUser( theUser.getIdUser() );
                theMember.insert();
            }

            // Load the bean so UI can see the results
            loadBean();
        }
        catch( Exception e ) {
            logger.log(Level.SEVERE, e.getMessage() );
            return FAIL;
        }
        
        if( result != EntityTemplate.SUCCESS )
            return FAIL;
        else
            return SUCCESS;

    }
    
    /**
     * Kills all of the session variables and allows the user to logout
     * 
     * @return  LOGOUT;
     */
    public String logout() {

        logger.log( Level.INFO, "Inside logout" );
        
        // Fetch the session object and invalidate the session
        ExternalContext ectx = FacesContext.getCurrentInstance().getExternalContext();
        HttpSession session = (HttpSession)ectx.getSession(false);
        session.invalidate(); 
        return LOGOUT;
    }

}
