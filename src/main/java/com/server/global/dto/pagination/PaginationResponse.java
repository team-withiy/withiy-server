package com.server.global.dto.pagination;

import java.util.List;

public interface PaginationResponse<T> {

	List<T> getData();

	long getTotal();
}
