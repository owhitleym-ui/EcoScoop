package edu.vassar.cmpu203.ecoscoop.src.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import edu.vassar.cmpu203.ecoscoop.R;
import edu.vassar.cmpu203.ecoscoop.databinding.FragmentAuthBinding;

/**
 * Fragment that handles user registration and sign-in.
 */
public class AuthFragment extends Fragment implements AuthUI {

    private static final String IS_REGISTERED = "isRegistered";

    private FragmentAuthBinding binding;
    private AuthUI.Listener listener;
    private boolean isRegistered = false;

    /** Grabs the controller as the listener when the fragment attaches to the activity. */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AuthUI.Listener) {
            this.listener = (AuthUI.Listener) context;
        } else {
            throw new ClassCastException(context + " must implement AuthUI.Listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.binding = FragmentAuthBinding.inflate(inflater, container, false);
        return this.binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.getBoolean(IS_REGISTERED))
            activateRegisteredConfig();

        this.binding.registerButton.setOnClickListener(v -> {
            String username = this.binding.usernameEditText.getText().toString();
            String password = this.binding.passwordEditText.getText().toString();
            if (listener != null) listener.onRegister(username, password, this);
        });

        this.binding.signinButton.setOnClickListener(v -> {
            String username = this.binding.usernameEditText.getText().toString();
            String password = this.binding.passwordEditText.getText().toString();
            if (listener != null) listener.onSigninAttempt(username, password, this);
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_REGISTERED, this.isRegistered);
    }

    @Override
    public void setListener(AuthUI.Listener listener) {
        this.listener = listener;
    }

    @Override
    public void onRegisterSuccess() {
        activateRegisteredConfig();
        displayMessage(R.string.registration_success_msg);
    }

    @Override
    public void onInvalidCredentials() {
        displayMessage(R.string.invalid_credentials_msg);
    }

    @Override
    public void onUserAlreadyExists() {
        displayMessage(R.string.user_already_exists_msg);
    }

    /** Disables the register button once the user has registered, preventing duplicate accounts. */
    private void activateRegisteredConfig() {
        this.isRegistered = true;
        this.binding.registerButton.setEnabled(false);
    }

    private void displayMessage(int msgResId) {
        Snackbar.make(this.binding.getRoot(), msgResId, Snackbar.LENGTH_LONG).show();
    }
}
