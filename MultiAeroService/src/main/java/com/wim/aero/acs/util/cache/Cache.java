/**
 * Copyright sp42 frank@ajaxjs.com Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.wim.aero.acs.util.cache;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * 缓存接口
 * 
 * @author sp42 frank@ajaxjs.com
 *
 * @param <K> 键
 * @param <V> 值
 */
public interface Cache<K, V> {
	/**
	 * 根据key从缓存中获取对应的值
	 * 
	 * @param key 键
	 * @return 值
	 */
	V get(K key);

	/**
	 * 添加缓存键值对
	 * 
	 * @param key   键
	 * @param value 值
	 * @return 值
	 */
	V put(K key, V value);

	/**
	 * 添加缓存键值对
	 *
	 * @param key   键
	 * @param value 值
	 * @return 值
	 */
	V put(K key, V value, int sec);

	/**
	 * 添加缓存键值对
	 *
	 * @param key   键
	 * @param value 值
	 * @return 值
	 */
	V put(K key, V value, Supplier<Object> load);


	/**
	 * 根据 key 从缓存中删除对象
	 * 
	 * @param key 键
	 * @return 值
	 */
	V remove(K key);

	/**
	 * 从缓存中清除所有的对象
	 */
	void clear();

	boolean containsKey(K key);

	ConcurrentHashMap.KeySetView<K, V> keySet();

}