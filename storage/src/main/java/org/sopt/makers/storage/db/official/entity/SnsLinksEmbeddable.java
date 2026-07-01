package org.sopt.makers.storage.db.official.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SnsLinksEmbeddable {

  @Column(name = "sns_email", length = 100)
  public String email;

  @Column(name = "sns_linkedin", length = 200)
  public String linkedin;

  @Column(name = "sns_github", length = 200)
  public String github;

  @Column(name = "sns_behance", length = 200)
  public String behance;
}
