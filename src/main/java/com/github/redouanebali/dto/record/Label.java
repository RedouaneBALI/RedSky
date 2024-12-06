package com.github.redouanebali.dto.record;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Label {

  private int     ver;
  private String  src;
  private String  uri;
  private String  cid;
  private String  val;
  private boolean neg;
  private String  cts;
  private String  exp;
  private String  sig;
}
