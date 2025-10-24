package com.innosistemas.authenticator.service;

import com.innosistemas.authenticator.dto.AuthenticatorRequest;
import com.innosistemas.authenticator.dto.AuthenticatorResponse;

public interface IAuthenticatorService {
    AuthenticatorResponse login(AuthenticatorRequest request);
}