package org.sopt.makers.domain.playground.popup.port;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.playground.popup.Popup;

public interface PopupRepositoryPort {

  Popup save(Popup popup);

  Optional<Popup> findById(Long id);

  List<Popup> findAll();

  void delete(Popup popup);

  Optional<Popup> findCurrentPopup();
}
