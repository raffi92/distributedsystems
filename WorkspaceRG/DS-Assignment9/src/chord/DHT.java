package chord;

import java.rmi.MarshalledObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DHT<K,T> implements Map<K,T>{
	private int key;
	private MarshalledObject Value;
	
	@Override
	public int size() {
		throw new UnsupportedOperationException("This method isn't availalbe for DHT");
	}

	@Override
	public boolean isEmpty() {
		throw new UnsupportedOperationException("This method isn't available for DHT");
	}

	@Override
	public boolean containsKey(Object key) {
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		return false;
	}

	@Override
	public T get(Object key) {
		//backhash
		return null;
	}

	@Override
	public T put(K key, T value) {
		key.hashCode();
		return null;
	}
	
	@Override
	public T remove(Object key) {
		return null;
	}

	@Override
	public void putAll(Map<? extends K, ? extends T> m) {
		throw new UnsupportedOperationException("This method isn't available for DHT");
		
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException("This method isn't available for DHT");
		
	}

	@Override
	public Set<K> keySet() {
		throw new UnsupportedOperationException("This method isn't available for DHT");
	}

	@Override
	public Collection<T> values() {
		throw new UnsupportedOperationException("This method isn't available for DHT");
	}

	@Override
	public Set<java.util.Map.Entry<K, T>> entrySet() {
		throw new UnsupportedOperationException("This method isn't available for DHT");
	}

}
