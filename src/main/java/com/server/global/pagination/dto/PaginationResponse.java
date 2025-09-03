package com.server.global.pagination.dto;

import java.util.List;

public interface PaginationResponse<T> {

	List<T> getData();

	long getTotal();
}
