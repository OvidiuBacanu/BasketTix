package Repositories.RepoInterfaces;

import Domain.User;

public interface UserRepoInterface extends Repository<Long, User> {

    User findUserAfterUsernamePassword(String name, String password);
}
