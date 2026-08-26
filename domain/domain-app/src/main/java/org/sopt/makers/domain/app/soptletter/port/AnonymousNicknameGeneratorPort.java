package org.sopt.makers.domain.app.soptletter.port;

import java.util.List;

public interface AnonymousNicknameGeneratorPort {

  List<String> generate(int count);
}
