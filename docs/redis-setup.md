# Redis 설정 가이드

## 개요

로컬 개발 환경에서 Redis를 선택적으로 사용할 수 있도록 설정되어 있습니다.

## 환경별 설정

### 1. 로컬 개발 (Redis 없이)

**기본 설정으로 Redis 없이 애플리케이션 실행 가능**

환경 변수 설정 불필요:

```bash
# application-local.yaml의 기본값 사용
spring.data.redis.enabled=false (기본값)
```

### 2. 로컬 개발 (Redis 사용)

**Redis를 사용하려면 환경 변수 설정**

#### 방법 1: 환경 변수로 설정

```bash
export REDIS_ENABLED=true
export REDIS_HOST=localhost
export REDIS_PORT=6379
export REDIS_PASSWORD=  # 비밀번호 없으면 빈 문자열
```

#### 방법 2: IntelliJ Run Configuration

1. Run > Edit Configurations
2. Environment variables에 추가:

```
REDIS_ENABLED=true;REDIS_HOST=localhost;REDIS_PORT=6379;REDIS_PASSWORD=
```

#### 방법 3: Docker Compose로 Redis 실행

```bash
docker run -d -p 6379:6379 --name redis redis:latest
```

### 3. 배포 환경 (Production)

**Redis 필수 사용**

`application-deploy.yaml`에서 `spring.data.redis.enabled=true`로 고정되어 있습니다.

## 실무 팁

### 로컬 개발 시 추천 방법

1. **Redis 없이 개발** (기본)
    - 대부분의 기능 개발은 Redis 없이 가능
    - 빠른 로컬 환경 구축

2. **Docker Compose 사용** (Redis 필요 시)
   ```yaml
   # docker-compose.local.yml
   version: '3.8'
   services:
     redis:
       image: redis:latest
       ports:
         - "6379:6379"
       command: redis-server --requirepass yourpassword
   ```

   실행:
   ```bash
   docker-compose -f docker-compose.local.yml up -d
   ```

3. **Embedded Redis** (테스트용)
    - 이미 `TestRedisConfig`에 설정되어 있음
    - 테스트 실행 시 자동으로 Embedded Redis 사용

## 주의사항

### Redis가 비활성화된 상태에서 사용 불가능한 기능

- Redis 기반 캐싱
- 분산 락 (Redisson)
- Redis 테스트 API (`/redis/test/*`)

### 에러 발생 시

Redis 연결 관련 에러가 발생하면:

1. `REDIS_ENABLED=false` 확인
2. 애플리케이션 재시작
3. Redis 관련 코드에서 `@ConditionalOnProperty` 추가 확인

## 코드 예시

### Redis 의존성이 있는 새로운 서비스 추가 시

```java

@Service
@ConditionalOnProperty(
	name = "spring.data.redis.enabled",
	havingValue = "true"
)
public class RedisCacheService {

	private final RedissonClient redissonClient;

	// Redis 로직...
}
```

### Redis 없이 대체 로직 제공

```java

@Service
@Primary
@ConditionalOnProperty(
	name = "spring.data.redis.enabled",
	havingValue = "false",
	matchIfMissing = true
)
public class InMemoryCacheService implements CacheService {
	// 메모리 기반 캐싱...
}

@Service
@ConditionalOnProperty(
	name = "spring.data.redis.enabled",
	havingValue = "true"
)
public class RedisCacheService implements CacheService {
	// Redis 기반 캐싱...
}
```

## 참고

- [Spring Boot Conditional Beans](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.developing-auto-configuration.condition-annotations)
- [Redisson Documentation](https://github.com/redisson/redisson)

