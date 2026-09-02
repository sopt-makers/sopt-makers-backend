package org.sopt.makers.storage.db.playground.popup.adapter;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.popup.Popup;
import org.sopt.makers.domain.playground.popup.port.PopupRepositoryPort;
import org.sopt.makers.storage.db.playground.popup.entity.PopupEntity;
import org.sopt.makers.storage.db.playground.popup.repository.PopupJpaRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PopupRepositoryAdapter implements PopupRepositoryPort {

  private final PopupJpaRepository popupJpaRepository;

  @Override
  public Popup save(Popup popup) {
    return popupJpaRepository.save(PopupEntity.from(popup)).toDomain();
  }

  @Override
  public Optional<Popup> findById(Long id) {
    return popupJpaRepository.findById(id).map(PopupEntity::toDomain);
  }

  @Override
  public List<Popup> findAll() {
    return popupJpaRepository.findAll().stream().map(PopupEntity::toDomain).toList();
  }

  @Override
  public void delete(Popup popup) {
    popupJpaRepository.findById(popup.id()).ifPresent(popupJpaRepository::delete);
  }

  @Override
  public Optional<Popup> findCurrentPopup() {
    return popupJpaRepository.findFirstCurrentPopup(LocalDate.now()).map(PopupEntity::toDomain);
  }
}
