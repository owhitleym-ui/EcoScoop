package edu.vassar.cmpu203.ecoscoop.src.view;

/**
 * Interface for the authentication screen.
 * Handles user registration and sign-in.
 */
public interface AuthUI {

    interface Listener {
        /** Called when the user attempts to register a new account. */
        void onRegister(String username, String password, AuthUI ui);
        /** Called when the user attempts to sign in. */
        void onSigninAttempt(String username, String password, AuthUI ui);
    }

    /** Sets the listener that handles user events. */
    void setListener(Listener listener);

    /** Called to indicate registration was successful. */
    void onRegisterSuccess();

    /** Called to indicate the provided credentials were invalid. */
    void onInvalidCredentials();

    /** Called to indicate the username is already taken. */
    void onUserAlreadyExists();
}
