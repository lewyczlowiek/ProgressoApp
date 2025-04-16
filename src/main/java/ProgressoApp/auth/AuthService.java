package ProgressoApp.auth;

import ProgressoApp.model.User;

public interface AuthService {

  void register(User user);

  Tokens authenticate(Credentials credentials);

  Tokens refreshTokens(Tokens tokens);
}
