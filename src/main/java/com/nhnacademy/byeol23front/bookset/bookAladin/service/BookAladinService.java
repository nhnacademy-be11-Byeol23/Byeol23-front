package com.nhnacademy.byeol23front.bookset.bookAladin.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nhnacademy.byeol23front.bookset.bookAladin.dto.AladinResult;

public interface
BookAladinService {

	AladinResult getAllBooks(String keyword, int page, int size) throws JsonProcessingException;
}
