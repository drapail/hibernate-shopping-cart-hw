package mate.academy.service.impl;

import mate.academy.exception.AuthenticationException;
import mate.academy.exception.RegistrationException;
import mate.academy.lib.Inject;
import mate.academy.lib.Service;
import mate.academy.model.ShoppingCart;
import mate.academy.model.User;
import mate.academy.service.AuthenticationService;
import mate.academy.service.ShoppingCartService;
import mate.academy.service.UserService;
import mate.academy.util.HashUtil;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    @Inject
    private UserService userService;
    @Inject
    private ShoppingCartService shoppingCartService;

    @Override
    public User login(String email, String password) throws AuthenticationException {
        Optional<User> userFromDbOptional = userService.findByEmail(email);
        User user = userFromDbOptional.get();
        if (userFromDbOptional.isEmpty() || !user.getPassword()
                .equals(HashUtil.hashPassword(password, user.getSalt()))) {
            throw new AuthenticationException("Can't authenticate user: email = " + email);
        }
        return user;
    }

    @Override
    public User register(String email, String password) throws RegistrationException {
        if (userService.findByEmail(email).isPresent()) {
            throw new RegistrationException("This email is already used: " + email);
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        User hibernateCreatedUser = userService.add(user);
        shoppingCartService.registerNewShoppingCart(hibernateCreatedUser);
        return hibernateCreatedUser;
    }
}