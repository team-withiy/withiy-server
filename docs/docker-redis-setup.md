    - 빠른 재시작
    - 디버깅 용이
    - Hot Reload 가능

2. **배포 테스트는 방법 2 사용**
    - 실제 프로덕션 환경과 유사
    - 네트워크 이슈 사전 확인

3. **IntelliJ Database 도구 활용**
    - View > Tool Windows > Database
    - PostgreSQL 연결 추가
    - SQL 쿼리 직접 실행 가능

4. **로그 레벨 조정**
    - `application-local.yaml`에서 로그 레벨 설정
   ```yaml
   logging:
     level:
       org.redisson: DEBUG
       com.zaxxer.hikari: DEBUG
   ```

