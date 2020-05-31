package me.arkorwan.srpg;

import org.objenesis.strategy.StdInstantiatorStrategy;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.ClosureSerializer;

public class SerializerHelper {

	static Kryo kryo = new Kryo();

	static {
		kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(
				new StdInstantiatorStrategy()));

		// fix this in the next kryo release.
		// https://github.com/EsotericSoftware/kryo/pull/415

		try {
			kryo.register(
					Class.forName("com.esotericsoftware.kryo.Kryo$Closure"),
					new ClosureSerializer());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static <T> T cloneEntity(T entity) {
		return kryo.copy(entity);
	}

}