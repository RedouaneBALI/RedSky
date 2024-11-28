package io.github.redouanebali.dto;

import java.util.List;

public interface Paginated<T> {

  String getCursor();

  List<T> retrieveItems();
}