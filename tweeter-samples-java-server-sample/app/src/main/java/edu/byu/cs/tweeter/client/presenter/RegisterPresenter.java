package edu.byu.cs.tweeter.client.presenter;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

import edu.byu.cs.tweeter.client.model.service.service.RegisterService;
import edu.byu.cs.tweeter.client.presenter.viewIntefaces.AuthenticationView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterPresenter extends SuperPresenter implements RegisterService.RegisterObserver {

    public RegisterPresenter(AuthenticationView view) {
        super(view);
    }


    @Override
    public void registerSuccess(AuthToken authToken, User user) {
        ((AuthenticationView)view).navigateToUser(user);
        ((AuthenticationView)view).clearErrorMessage();
        ((AuthenticationView)view).displayInfoMessage("Hello " + user.getAlias());
    }

    private String validateRegistration(String firstName, String lastName, String alias, String password, ImageView imageToUpload) {
        if (firstName.length() == 0) {
            return "First Name cannot be empty.";
        }
        if (lastName.length() == 0) {
            return "Last Name cannot be empty.";
        }
        if (alias.length() == 0) {
            return "Alias cannot be empty.";
        }
        if (alias.charAt(0) != '@') {
            return "Alias must begin with @.";
        }
        if (alias.length() < 2) {
            return "Alias must contain 1 or more characters after the @.";
        }
        if (password.length() == 0) {
            return "Password cannot be empty.";
        }
        if (imageToUpload == null) {
            return "Profile image must be uploaded.";
        }
        return null;
    }

    public void register(String firstName, String lastName, String alias, String password, ImageView imageToUpload) {
        String message = validateRegistration(firstName, lastName, alias, password, imageToUpload);
        ((AuthenticationView)view).clearErrorMessage();
        ((AuthenticationView)view).clearInfoMessage();

        if (message == null) {
            ((AuthenticationView)view).displayInfoMessage("Registering...");
            // Convert image to byte array.
            Bitmap image = ((BitmapDrawable) imageToUpload.getDrawable()).getBitmap();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] imageBytes = bos.toByteArray();
            String imageBytesBase64 = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
            new RegisterService().register(firstName, lastName, alias, password, imageBytesBase64, this);
        } else view.displayErrorMessage("Register Failed: " + message);


    }


}
