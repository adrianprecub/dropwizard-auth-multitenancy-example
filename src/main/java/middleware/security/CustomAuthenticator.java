package middleware.security;

import com.google.common.base.Optional;
import dao.TokenDAO;
import dao.UserDAO;
import dao.entities.TokenModel;
import dao.entities.UserModel;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.hibernate.UnitOfWork;

public class CustomAuthenticator implements Authenticator<CustomCredentials, CustomAuthUser> {
  private TokenDAO tokenDAO;
  private UserDAO userDAO;

  public CustomAuthenticator(TokenDAO tokenDAO, UserDAO userDAO) {
    this.tokenDAO = tokenDAO;
    this.userDAO = userDAO;
  }

  @Override
  @UnitOfWork
  public Optional<CustomAuthUser> authenticate(CustomCredentials credentials) throws AuthenticationException {
    CustomAuthUser authenticatedUser = null;
    Optional<UserModel> user = userDAO.getUser(credentials.getUserId());

    if (user.isPresent()) {
      Optional<TokenModel> token = tokenDAO.findTokenForUser(user.get());

      if (token.isPresent()) {
        TokenModel tokenModel = token.get();

        if (tokenModel.getId().equals(credentials.getToken())) {
          authenticatedUser = new CustomAuthUser(tokenModel.getUser().getId(), tokenModel.getUser().getName());
        }
      }
    }

    return Optional.fromNullable(authenticatedUser);
  }
}