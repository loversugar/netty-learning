package com.totti.order.common.auth;

import com.totti.order.common.OperationResult;

public class AuthOperationResult extends OperationResult {
    private boolean passAuth;

    public AuthOperationResult(boolean passAuth) {
        this.passAuth = passAuth;
    }

    public boolean isPassAuth() {
        return passAuth;
    }

    public void setPassAuth(boolean passAuth) {
        this.passAuth = passAuth;
    }
}
