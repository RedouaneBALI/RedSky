package com.github.redouanebali.dto.login;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginRequest {

  private String identifier;
  private String password;

}