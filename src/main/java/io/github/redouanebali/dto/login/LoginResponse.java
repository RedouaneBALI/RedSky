package io.github.redouanebali.dto.login;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginResponse {

  private String did;
  private String handle;
  private String accessJwt;
  private String refreshJwt;

  private DidDocDTO didDoc;

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class DidDocDTO {

    @JsonProperty("@context")
    private List<String> context;

    private String                      id;
    private List<String>                alsoKnownAs;
    private List<VerificationMethodDTO> verificationMethod;
    private List<ServiceDTO>            service;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ServiceDTO {

      private String id;
      private String type;
      private String serviceEndpoint;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VerificationMethodDTO {

      private String id;
      private String type;
      private String controller;
      private String publicKeyMultibase;
    }
  }

}
