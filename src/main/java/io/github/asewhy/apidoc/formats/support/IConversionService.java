package io.github.asewhy.apidoc.formats.support;

import io.github.asewhy.apidoc.descriptor.DocumentedApi;

public interface IConversionService<T> {
    T convert(DocumentedApi api);
}
