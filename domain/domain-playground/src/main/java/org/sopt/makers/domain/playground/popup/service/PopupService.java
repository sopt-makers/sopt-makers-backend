package org.sopt.makers.domain.playground.popup.service;

import static org.sopt.makers.domain.playground.popup.exception.PopupFailure.NOT_FOUND_POPUP;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.popup.Popup;
import org.sopt.makers.domain.playground.popup.exception.PopupException;
import org.sopt.makers.domain.playground.popup.port.PopupRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PopupService {

  private final PopupRepositoryPort popupRepositoryPort;

  @Transactional
  public Popup createPopup(
      LocalDate startDate,
      LocalDate endDate,
      String pcImageUrl,
      String mobileImageUrl,
      String linkUrl,
      Boolean openInNewTab,
      Boolean showOnlyToRecentGeneration) {
    return popupRepositoryPort.save(
        new Popup(
            null,
            startDate,
            endDate,
            pcImageUrl,
            mobileImageUrl,
            linkUrl,
            openInNewTab,
            showOnlyToRecentGeneration,
            null,
            null));
  }

  @Transactional(readOnly = true)
  public List<Popup> getAllPopups() {
    return popupRepositoryPort.findAll();
  }

  @Transactional(readOnly = true)
  public Popup getPopupById(Long id) {
    return popupRepositoryPort.findById(id).orElseThrow(() -> new PopupException(NOT_FOUND_POPUP));
  }

  @Transactional
  public Popup updatePopup(
      Long id,
      LocalDate startDate,
      LocalDate endDate,
      String pcImageUrl,
      String mobileImageUrl,
      String linkUrl,
      Boolean openInNewTab,
      Boolean showOnlyToRecentGeneration) {
    getPopupById(id);
    return popupRepositoryPort.save(
        new Popup(
            id,
            startDate,
            endDate,
            pcImageUrl,
            mobileImageUrl,
            linkUrl,
            openInNewTab,
            showOnlyToRecentGeneration,
            null,
            null));
  }

  @Transactional
  public void deletePopup(Long id) {
    Popup popup = getPopupById(id);
    popupRepositoryPort.delete(popup);
  }

  @Transactional(readOnly = true)
  public Popup getCurrentPopup() {
    return popupRepositoryPort.findCurrentPopup().orElse(null);
  }
}
