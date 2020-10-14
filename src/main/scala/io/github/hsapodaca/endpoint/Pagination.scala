package io.github.hsapodaca.endpoint

import org.http4s.QueryParamDecoder
import org.http4s.dsl.impl.OptionalQueryParamDecoderMatcher

object Pagination {
  import QueryParamDecoder._
  object PageSizeMatcher extends OptionalQueryParamDecoderMatcher[Int]("pageSize")
  object OffsetMatcher extends OptionalQueryParamDecoderMatcher[Int]("offset")
}
