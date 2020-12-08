package com.totti.order.common.auth;

import com.totti.order.common.Operation;

public class AuthOperation extends Operation {
    private final String userName;
    private final String password;

    public AuthOperation(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    @Override
    public AuthOperationResult execute() {
        if ("admin".equalsIgnoreCase(userName)) {
            AuthOperationResult result = new AuthOperationResult(true);
            result.setPassAuth(true);
            return result;
        }
        return new AuthOperationResult(false);
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
}
