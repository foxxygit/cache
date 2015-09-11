package com.foxxy.git.coder;

import java.nio.charset.Charset;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

@Service(value = "defaultJsonCacheCoder")
public class DefaultJsonCacheCoder implements CacheCoder {

	private Charset charset = Charset.forName("UTF-8");

	private static Gson gson = new GsonBuilder()
			.enableComplexMapKeySerialization()
			.setDateFormat("yyyy-MM-dd HH:mm:ss").create();

	@Override
	public String encode(Object obj) {
		if (null == obj) {
			return StringUtils.EMPTY;
		}
		return gson.toJson(obj);
	}

	@Override
	public <T> T decode(byte[] b, Class<T> requireType) {
		return decode(new String(b, charset), requireType);
	}

	@Override
	public <T> T decode(String content, Class<T> requireType) {
		return fromJson(content, requireType);
	}

	private <T> T fromJson(String entity, Class<T> ctype) {
		T t = null;
		try {
			t = gson.fromJson(entity, ctype);
		} catch (JsonSyntaxException e) {
			throw new RuntimeException(e);
		}
		return t;
	}

}
