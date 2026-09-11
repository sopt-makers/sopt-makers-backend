-- 동일 방향(poker_id -> poked_id)으로 미답장 콕찌르기 기록은 한 번에 하나만 존재할 수 있다.
-- 동시 요청으로 중복 생성되는 걸 애플리케이션의 사전 체크만으로는 막을 수 없어서, DB 제약으로 보강한다.
CREATE UNIQUE INDEX uk_poke_history_poker_poked_unreplied
    ON poke_history (poker_id, poked_id)
    WHERE is_reply = false;

-- 솝마디(운세) 기능이 제거되어 더 이상 사용하지 않는 테이블을 정리한다.
DROP TABLE IF EXISTS user_fortune;
DROP TABLE IF EXISTS fortune_word;
DROP TABLE IF EXISTS fortune_card;
