package com.server.global.pagination.service;

import com.server.global.pagination.dto.CursorPageDto;
import com.server.global.pagination.strategy.PaginationContext;
import com.server.global.pagination.strategy.PaginationStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 페이지네이션 통합 서비스
 * Strategy 패턴을 사용하여 다양한 페이징 전략을 지원
 *
 * <p>이 서비스는 페이징 로직의 진입점으로,
 * 각 Service 계층에서 주입받아 사용합니다.
 *
 * <p><b>사용 예시:</b>
 * <pre>{@code
 * @Service
 * @RequiredArgsConstructor
 * public class PhotoService {
 *     private final PaginationService paginationService;
 *
 *     public CursorPageDto<Photo, Long> getPhotos(ApiCursorPaginationRequest request) {
 *         CursorQueryExecutor<Photo, Long> executor = 
 *             new PhotoCursorQueryExecutor(photoRepository, placeId);
 *         
 *         PaginationContext<Photo, Long> context = PaginationContext.<Photo, Long>builder()
 *             .request(request)
 *             .queryExecutor(executor)
 *             .idExtractor(Photo::getId)
 *             .build();
 *         
 *         return paginationService.paginate(context);
 *     }
 * }
 * }</pre>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaginationService {

	private final PaginationStrategy<Object, Object> paginationStrategy;

	/**
	 * 커서 기반 페이지네이션 실행
	 *
	 * <p>제네릭 타입을 사용하여 모든 엔티티에 대해 동일한 인터페이스를 제공합니다.
	 *
	 * @param context 페이징 컨텍스트 (요청, Executor, ID 추출 함수 포함)
	 * @param <T>     엔티티 타입
	 * @param <ID>    커서 ID 타입
	 * @return 페이징 결과
	 */
	public <T, ID> CursorPageDto<T, ID> paginate(PaginationContext<T, ID> context) {
		log.debug("Executing cursor pagination with limit: {}, cursor: {}, prev: {}",
			context.getRequest().getLimit(),
			context.getRequest().getCursor(),
			context.getRequest().getPrev());

		@SuppressWarnings("unchecked")
		PaginationStrategy<T, ID> strategy =
			(PaginationStrategy<T, ID>) paginationStrategy;

		return strategy.executePagination(context);
	}

	/**
	 * 향후 다른 페이징 전략 추가 시 사용할 메서드
	 * 예: Offset 페이징, Keyset 페이징 등
	 */
	// public <T> OffsetPageDto<T> paginateWithOffset(OffsetPaginationContext<T> context) {
	//     return offsetPaginationStrategy.executePagination(context);
	// }
}
