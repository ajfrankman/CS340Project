package edu.byu.cs.tweeter.server.factoryinterfaces;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public interface AuthDAOInterface {
    public AuthToken generateAuthToken(String userAlias);
}
